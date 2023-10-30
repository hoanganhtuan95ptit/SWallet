package com.simple.wallet.di

import com.simple.wallet.data.repositories.ChainRepositoryImpl
import com.simple.wallet.data.repositories.WalletConnectRepositoryImpl
import com.simple.wallet.data.repositories.WalletRepositoryImpl
import com.simple.wallet.domain.repositories.ChainRepository
import com.simple.wallet.domain.repositories.WalletConnectRepository
import com.simple.wallet.domain.repositories.WalletRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<WalletRepository> {
        WalletRepositoryImpl(get(), get(), get(), getAll())
    }

    single<ChainRepository> {
        ChainRepositoryImpl(get(), get(), getAll())
    }

    single<WalletConnectRepository> {
        WalletConnectRepositoryImpl(get(), get())
    }
}