package com.simple.wallet.di

import com.simple.wallet.presentation.camera.CameraViewModel
import com.simple.wallet.presentation.wallet.add.AddWalletViewModel
import com.simple.wallet.presentation.wallet.add.create.CreateWalletViewModel
import com.simple.wallet.presentation.wallet.add.import.ImportWalletViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

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
}