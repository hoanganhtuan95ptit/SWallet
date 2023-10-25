package com.simple.wallet.di

import com.simple.navigation.NavigationProvider
import com.simple.wallet.presentation.camera.CameraProvider
import com.simple.wallet.presentation.wallet.add.AddWalletProvider
import com.simple.wallet.presentation.wallet.add.confirm.ConfirmWalletProvider
import com.simple.wallet.presentation.wallet.add.create.CreateWalletProvider
import com.simple.wallet.presentation.wallet.add.import.ImportWalletProvider
import org.koin.dsl.bind
import org.koin.dsl.module

val navigationModule = module {

    single { CameraProvider() } bind NavigationProvider::class

    single { AddWalletProvider() } bind NavigationProvider::class

    single { CreateWalletProvider() } bind NavigationProvider::class

    single { ImportWalletProvider() } bind NavigationProvider::class

    single { ConfirmWalletProvider() } bind NavigationProvider::class
}
