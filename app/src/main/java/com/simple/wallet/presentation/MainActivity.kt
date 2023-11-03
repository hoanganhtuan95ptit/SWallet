package com.simple.wallet.presentation

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.simple.bottomsheet.ActivityScreen
import com.simple.core.utils.extentions.asObject
import com.simple.coreapp.ui.base.activities.BaseViewModelActivity
import com.simple.coreapp.utils.extentions.getColorFromAttr
import com.simple.coreapp.utils.extentions.toPx
import com.simple.navigation.Navigation
import com.simple.wallet.R
import com.simple.wallet.databinding.ActivityMainBinding
import com.simple.wallet.presentation.home.HomeFragment
import org.koin.android.ext.android.getKoin

class MainActivity : BaseViewModelActivity<ActivityMainBinding, MainViewModel>(), ActivityScreen, Navigation {

    private val viewObserves: List<ViewObserve> by lazy {
        getKoin().getAll()
    }

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

        viewObserves.forEach {

            it.setOwner(this)
        }

        viewModel
    }

    override fun onPercent(percent: Float) {

        val binding = binding ?: return

        binding.fragmentContainer.setRadius(percent * 40.toPx())
    }

    override suspend fun navigateTo(fragmentManager: FragmentManager, containerViewId: Int, fragment: Fragment, tag: String?) {

        super.navigateTo(fragmentManager, R.id.fragment_container, fragment, tag)
    }
}