package com.simple.wallet.di

import com.simple.navigation.NavigationProvider
import com.simple.wallet.presentation.camera.CameraProvider
import com.simple.wallet.presentation.chain.SelectChainProvider
import com.simple.wallet.presentation.message.sign.SignMessageConfirmProvider
import com.simple.wallet.presentation.wallet.add.AddWalletProvider
import com.simple.wallet.presentation.wallet.add.confirm.ConfirmWalletProvider
import com.simple.wallet.presentation.wallet.add.create.CreateWalletProvider
import com.simple.wallet.presentation.wallet.add.import.ImportWalletProvider
import com.simple.wallet.presentation.wallet.select.SelectWalletProvider
import com.simple.wallet.presentation.walletconnect.approveconnect.ApproveConnectProvider
import org.koin.dsl.bind
import org.koin.dsl.module

val navigationModule = module {

    single { CameraProvider() } bind NavigationProvider::class


    single { AddWalletProvider() } bind NavigationProvider::class

    single { CreateWalletProvider() } bind NavigationProvider::class

    single { ImportWalletProvider() } bind NavigationProvider::class

    single { ConfirmWalletProvider() } bind NavigationProvider::class


    single { SelectChainProvider() } bind NavigationProvider::class

    single { SelectWalletProvider() } bind NavigationProvider::class


    single { ApproveConnectProvider() } bind NavigationProvider::class


    single { SignMessageConfirmProvider() } bind NavigationProvider::class
}
