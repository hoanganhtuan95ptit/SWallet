package com.simple.wallet.presentation.browser

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Bundle
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.simple.core.utils.extentions.asObjectOrNull
import com.simple.coreapp.ui.base.fragments.BaseViewModelFragment
import com.simple.coreapp.utils.ext.getStringOrEmpty
import com.simple.coreapp.utils.extentions.doOnHeightStatusAndHeightNavigationChange
import com.simple.coreapp.utils.extentions.resize
import com.simple.coreapp.utils.extentions.setDebouncedClickListener
import com.simple.navigation.ChildNavigation
import com.simple.navigation.NavigationProvider
import com.simple.navigation.domain.entities.DeeplinkNavigationEvent
import com.simple.navigation.domain.entities.NavigationEvent
import com.simple.navigation.utils.ext.offerDeepLink
import com.simple.wallet.PARAM_DATA
import com.simple.wallet.PARAM_PAIR
import com.simple.wallet.PARAM_SLIDE
import com.simple.wallet.PARAM_URL
import com.simple.wallet.R
import com.simple.wallet.databinding.FragmentBrowserBinding
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.utils.exts.decodeUrl
import com.simple.wallet.utils.exts.encodeUrl
import com.simple.wallet.utils.exts.getBitmap
import com.simple.wallet.utils.exts.isWalletConnect
import com.simple.wallet.utils.exts.isWalletConnectPair
import com.simple.wallet.utils.exts.takeIfNotBlank
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class BrowserFragment : BaseViewModelFragment<FragmentBrowserBinding, BrowserViewModel>(), ChildNavigation {


    private val url: String? by lazy {

        arguments.getStringOrEmpty(PARAM_URL)
    }


    private var urlBack: String? = null


    private var webView: WebView? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = binding?.webView

        setupSearch()
        setupWebView()
        setupStatusBar()
    }

    override fun onResume() {
        super.onResume()

        webView?.onResume()
    }

    override fun onPause() {
        super.onPause()

        webView?.onPause()
    }

    override fun onDestroyView() {

        webView?.destroy()

        webView = null

        super.onDestroyView()
    }

    override fun updateEvent(event: NavigationEvent) {

        val binding = binding ?: return

        val deepLink = event.asObjectOrNull<DeeplinkNavigationEvent>()?.deepLink ?: return

        binding.webView.loadUrl(deepLink)
    }

    override fun onBackPressed(): Boolean {

        return if (binding != null && binding!!.webView.canGoBack() && urlBack != binding!!.webView.url) {

            urlBack = binding!!.webView.url

            binding!!.webView.goBack()
            true
        } else {

            false
        }
    }

    private fun setupSearch() {

        val binding = binding ?: return

        binding.ivBack.setDebouncedClickListener {

            dismiss()
        }

        binding.tvSearch.setOnClickListener {

            offerDeepLink("/search?$PARAM_DATA=${binding.webView.url?.encodeUrl() ?: ""}")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {

        val binding = binding ?: return

        val webView = binding.webView

//        webView.settings.useWideViewPort = true
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
//        webView.settings.loadWithOverviewMode = true
//        webView.settings.javaScriptCanOpenWindowsAutomatically = true

        webView.settings.userAgentString = webView.settings.userAgentString + getString(R.string.app_name)

        webView.webChromeClient = object : WebChromeClient() {

            var job: Job? = null

            override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {

                if (view == null) {

                    return
                }

                job?.cancel()

                job = viewLifecycleOwner.lifecycleScope.launch {

                    val bitmap = view.getBitmap() ?: return@launch

                    val navigationBarColor = bitmap.getPixel(bitmap.width - 1, bitmap.height - 1)

                    bitmap.recycle()

                    binding.navigation.setBackgroundColor(navigationBarColor)
                }
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {

                binding.tvSearch.text = title
            }

            override fun onProgressChanged(webview: WebView, newProgress: Int) {

                binding.progressBar.progress = newProgress

                binding.progressBar.isVisible = newProgress != 100
            }
        }

        webView.webViewClient = object : WebViewClient() {

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)

            }

            override fun onPageFinished(view: WebView?, url: String?) {

                if (view == null) {

                    return
                }
            }

            override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {

                handler?.cancel()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {

                val url = request?.url.toString()

                return if (url.isWalletConnect()) {

                    if (url.isWalletConnectPair()) offerDeepLink("/wallet-connect?$PARAM_PAIR=${url.encodeUrl()}&$PARAM_SLIDE=${Request.Slide.APP.value}")
                    true
                } else {

                    super.shouldOverrideUrlLoading(view, request)
                }
            }
        }

        url?.takeIfNotBlank()?.let {

            webView.loadUrl(it)
        }
    }

    private fun setupStatusBar() = doOnHeightStatusAndHeightNavigationChange { heightStatusBar, heightNavigationBar ->

        val binding = binding ?: return@doOnHeightStatusAndHeightNavigationChange

        binding.status.resize(height = heightStatusBar)
        binding.navigation.resize(height = heightNavigationBar)
    }
}

class BrowserProvider : NavigationProvider {

    override fun deepLink(): String {

        return "https:"
    }

    override fun provideFragment(deepLink: String, params: Map<String, String>): Fragment {

        return BrowserFragment().apply {

            arguments = bundleOf(PARAM_URL to deepLink.decodeUrl(), *params.mapValues { it.value.decodeUrl() }.toList().toTypedArray())
        }
    }
}