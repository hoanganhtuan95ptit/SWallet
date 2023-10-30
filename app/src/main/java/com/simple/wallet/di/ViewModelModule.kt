package com.simple.wallet.di

import com.simple.wallet.MainViewModel
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.presentation.camera.CameraViewModel
import com.simple.wallet.presentation.chain.SelectChainViewModel
import com.simple.wallet.presentation.home.HomeViewModel
import com.simple.wallet.presentation.message.sign.SignMessageConfirmViewModel
import com.simple.wallet.presentation.wallet.add.AddWalletViewModel
import com.simple.wallet.presentation.wallet.add.create.CreateWalletViewModel
import com.simple.wallet.presentation.wallet.add.import.ImportWalletViewModel
import com.simple.wallet.presentation.wallet.select.SelectWalletViewModel
import com.simple.wallet.presentation.walletconnect.WalletConnectViewModel
import com.simple.wallet.presentation.walletconnect.approveconnect.ApproveConnectConfirmViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {


    viewModel {
        MainViewModel(get())
    }


    viewModel {
        HomeViewModel()
    }

    viewModel {
        AddWalletViewModel(get(), get())
    }

    viewModel {
        CreateWalletViewModel()
    }

    viewModel {
        ImportWalletViewModel(getAll())
    }

    viewModel { (action: String) ->
        CameraViewModel(action, get(), get(), getAll(), getAll())
    }

    viewModel { (chainId: Long, isSupportAllChain: Boolean) ->
        SelectChainViewModel(chainId, isSupportAllChain, get())
    }

    viewModel { (walletId: String, isSupportAllWallet: Boolean) ->
        SelectWalletViewModel(walletId, isSupportAllWallet, get())
    }

    viewModel {
        WalletConnectViewModel(get(), get(), get(), get())
    }

    viewModel { (pair: String?, slide: Request.Slide?) ->

        ApproveConnectConfirmViewModel(pair, slide, get(), get(), get(), get(), get(), get(), get())
    }

    viewModel { (request: Request) ->

        SignMessageConfirmViewModel(request, get(), get(), get(), get())
    }
}