package com.simple.wallet.di

import com.simple.wallet.presentation.ViewObserve
import com.simple.wallet.presentation.walletconnect.WalletConnectViewObserve
import org.koin.dsl.bind
import org.koin.dsl.module

val viewObserveModule = module {

    single { WalletConnectViewObserve() } bind ViewObserve::class
}
