package com.simple.wallet.presentation.search

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.simple.adapter.MultiAdapter
import com.simple.coreapp.ui.adapters.SpaceAdapter
import com.simple.coreapp.ui.base.fragments.BaseViewModelDialogFragment
import com.simple.coreapp.ui.dialogs.OptionFragment
import com.simple.coreapp.utils.autoCleared
import com.simple.coreapp.utils.ext.getStringOrEmpty
import com.simple.coreapp.utils.extentions.doOnHeightStatusAndHeightNavigationChange
import com.simple.coreapp.utils.extentions.resize
import com.simple.navigation.NavigationProvider
import com.simple.navigation.utils.ext.offerDeepLink
import com.simple.navigation.utils.ext.setNavigationResult
import com.simple.wallet.DATA
import com.simple.wallet.PARAM_DATA
import com.simple.wallet.R
import com.simple.wallet.databinding.FragmentSearchBinding
import com.simple.wallet.presentation.browser.adapters.UrlAdapter
import com.simple.wallet.utils.exts.decodeUrl
import com.simple.wallet.utils.exts.showKeyboard
import com.simple.wallet.utils.exts.takeIfNotBlank


class SearchFragment : BaseViewModelDialogFragment<FragmentSearchBinding, SearchViewModel>() {


    private val query: String? by lazy {

        arguments?.getStringOrEmpty(PARAM_DATA)
    }

    private val keyRequest: String by lazy {

        arguments?.getStringOrEmpty(OptionFragment.KEY_REQUEST) ?: ""
    }


    private var adapter by autoCleared<MultiAdapter>()


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = super.onCreateDialog(savedInstanceState)

        val window = dialog.window ?: return dialog

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearch()
        setupStatusBar()
        setupRecyclerView()

        observeData()
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    private fun setupSearch() {

        val binding = binding ?: return

        binding.edtSearch.doAfterTextChanged {

            if (binding.edtSearch.isFocused) {

                viewModel.query(it.toString())
            }
        }
    }

    private fun setupStatusBar() = doOnHeightStatusAndHeightNavigationChange { heightStatusBar, heightNavigationBar ->

        val binding = binding ?: return@doOnHeightStatusAndHeightNavigationChange

        binding.status.resize(height = heightStatusBar)
    }

    private fun setupRecyclerView() {

        val binding = binding ?: return

        val urlAdapter = UrlAdapter { view, item ->

            dismiss()

            if (keyRequest.isBlank()) {

                offerDeepLink(item.data.url)
            } else {

                setNavigationResult(keyRequest, bundleOf(DATA to item.data.url))
            }
        }

        val spaceAdapter = SpaceAdapter()

        adapter = MultiAdapter(
            urlAdapter,
            spaceAdapter,
        ).apply {

            scrollTop(true)

            setRecyclerView(binding.recyclerView)
        }

        binding.recyclerView.itemAnimator = null

        binding.recyclerView.setOnClickListener {

            binding.edtSearch.clearFocus()
        }
    }

    private fun observeData() = with(viewModel) {

        urlViewItemList.observe(viewLifecycleOwner) {

            adapter?.submitList(it)
        }


        val binding = binding ?: return@with

        query?.takeIfNotBlank()?.let {

            binding.edtSearch.setText(it)
        }

        binding.edtSearch.requestFocus()
        showKeyboard(binding.edtSearch)
    }
}

class SearchProvider : NavigationProvider {

    override fun deepLink(): String {

        return "/search"
    }

    override fun provideFragment(deepLink: String, params: Map<String, String>): Fragment {

        return SearchFragment().apply {

            arguments = bundleOf(*params.mapValues { it.value.decodeUrl() }.toList().toTypedArray())
        }
    }
}