package com.simple.wallet.di

import com.simple.wallet.data.repositories.ChainRepositoryImpl
import com.simple.wallet.data.repositories.WalletRepositoryImpl
import com.simple.wallet.domain.repositories.ChainRepository
import com.simple.wallet.domain.repositories.WalletRepository
import org.koin.dsl.module

val repositoryModule = module {

    single<WalletRepository> {
        WalletRepositoryImpl(get())
    }

    single<ChainRepository> {
        ChainRepositoryImpl()
    }
}