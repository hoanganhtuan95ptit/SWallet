package com.simple.wallet.di

import com.simple.wallet.data.socket.WalletConnectSocket
import com.simple.wallet.data.socket.WalletConnectSocketImpl
import org.koin.dsl.module

internal val socketModule = module {

    single<WalletConnectSocket> {
        WalletConnectSocketImpl(get())
    }
}