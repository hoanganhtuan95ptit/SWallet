package com.simple.wallet

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.simple.bottomsheet.ActivityScreen
import com.simple.core.utils.extentions.asObject
import com.simple.coreapp.ui.base.activities.BaseViewBindingActivity
import com.simple.coreapp.utils.extentions.doOnHeightNavigationChange
import com.simple.coreapp.utils.extentions.getColorFromAttr
import com.simple.coreapp.utils.extentions.toPx
import com.simple.navigation.Navigation
import com.simple.navigation.utils.ext.offerDeepLink
import com.simple.wallet.databinding.ActivityMainBinding
import com.simple.wallet.presentation.home.HomeFragment
import com.simple.wallet.ui.theme.SWalletTheme
import kotlinx.coroutines.launch

class MainActivity : BaseViewBindingActivity<ActivityMainBinding>(), ActivityScreen, Navigation {

    override fun onCreate(savedInstanceState: Bundle?) {

        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT

        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenResumed {

            supportFragmentManager.beginTransaction().add(R.id.fragment_container, HomeFragment()).commitAllowingStateLoss()
        }

        val binding = binding ?: return

        binding.root.parent.asObject<View>().setBackgroundColor(binding.root.context.getColorFromAttr(com.google.android.material.R.attr.colorOnBackground))
    }

    override fun onPercent(percent: Float) {

        val binding = binding ?: return

        binding.fragmentContainer.setRadius(percent * 40.toPx())
    }

    override suspend fun navigateTo(fragmentManager: FragmentManager, containerViewId: Int, fragment: Fragment, tag: String?) {

        super.navigateTo(fragmentManager, R.id.fragment_container, fragment, tag)
    }
}