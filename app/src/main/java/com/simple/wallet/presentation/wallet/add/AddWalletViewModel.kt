package com.simple.wallet.presentation.wallet.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.ui.adapters.SpaceViewItem
import com.simple.coreapp.ui.base.viewmodels.BaseViewModel
import com.simple.coreapp.utils.extentions.Event
import com.simple.coreapp.utils.extentions.liveData
import com.simple.coreapp.utils.extentions.postDifferentValue
import com.simple.coreapp.utils.extentions.postDifferentValueIfActive
import com.simple.coreapp.utils.extentions.toEvent
import com.simple.coreapp.utils.extentions.toImage
import com.simple.coreapp.utils.extentions.toPx
import com.simple.coreapp.utils.extentions.toText
import com.simple.state.ResultState
import com.simple.wallet.LOGO_APP
import com.simple.wallet.R
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.domain.usecases.CreateWalletUseCase
import com.simple.wallet.domain.usecases.ImportWalletUseCase
import com.simple.wallet.presentation.adapters.HeaderViewItem
import com.simple.wallet.presentation.wallet.add.adapters.OptionViewItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddWalletViewModel(
    private val importWalletUseCase: ImportWalletUseCase,
    private val createWalletUseCase: CreateWalletUseCase,
) : BaseViewModel() {

    val viewItemList: LiveData<List<ViewItemCloneable>> = liveData {

        val list = arrayListOf<ViewItemCloneable>()

        HeaderViewItem(
            "HeaderViewItem",
            LOGO_APP.toImage(),
            title = R.string.title_add_wallet.toText(),
            caption = R.string.caption_add_wallet.toText()
        ).let {

            list.add(it)
        }

        SpaceViewItem(height = 16.toPx()).let {

            list.add(it)
        }

        OptionViewItem(
            id = createWallet,
            image = R.drawable.ic_add_on_background_24dp.toImage(),
            title = R.string.title_create_wallet.toText(),
            caption = R.string.caption_create_wallet.toText(),
            background = R.drawable.bg_corners_16dp_stroke_dash_1dp_divider,
            paddingVertical = 16.toPx(),
            paddingHorizontal = 16.toPx()
        ).let {

            list.add(it)
        }

        SpaceViewItem(height = 16.toPx()).let {

            list.add(it)
        }

        OptionViewItem(
            id = importWallet,
            image = R.drawable.ic_import_wallet_on_background_24dp.toImage(),
            title = R.string.title_import_wallet.toText(),
            caption = R.string.caption_import_wallet.toText(),
            background = R.drawable.bg_corners_16dp_stroke_dash_1dp_divider,
            paddingVertical = 16.toPx(),
            paddingHorizontal = 16.toPx()
        ).let {

            list.add(it)
        }

        SpaceViewItem(height = 100.toPx()).let {

            list.add(it)
        }

        postDifferentValueIfActive(list)
    }


    val addWalletState: LiveData<ResultState<Wallet>> = MediatorLiveData()

    val addWalletStateEvent:LiveData<Event<ResultState<Wallet>>> = addWalletState.toEvent()


    fun importWallet(walletName: String, walletKey: String, walletType: Wallet.Type) = viewModelScope.launch(handler + Dispatchers.IO) {

        addWalletState.postDifferentValue(ResultState.Start)

        kotlin.runCatching {

            addWalletState.postDifferentValue(ResultState.Success(importWalletUseCase.execute(ImportWalletUseCase.Param(walletName, walletKey, walletType))))
        }.getOrElse {

            addWalletState.postDifferentValue(ResultState.Failed(it))
        }
    }

    fun createWallet(walletName: String) = viewModelScope.launch(handler + Dispatchers.IO) {

        addWalletState.postDifferentValue(ResultState.Start)

        kotlin.runCatching {

            addWalletState.postDifferentValue(ResultState.Success(createWalletUseCase.execute(walletName)))
        }.getOrElse {

            addWalletState.postDifferentValue(ResultState.Failed(it))
        }
    }
}

internal val createWallet by lazy {
    "/create-wallet"
}

private val importWallet by lazy {
    "/import-wallet"
}