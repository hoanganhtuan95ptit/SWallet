package com.simple.wallet.di

import com.simple.core.utils.extentions.asObject
import com.simple.wallet.data.repositories.BalanceRepositoryImpl
import com.simple.wallet.data.repositories.ChainRepositoryImpl
import com.simple.wallet.data.repositories.TokenRepositoryImpl
import com.simple.wallet.data.repositories.TransactionRepositoryImpl
import com.simple.wallet.data.repositories.UrlRepositoryImpl
import com.simple.wallet.data.repositories.WalletConnectRepositoryImpl
import com.simple.wallet.data.repositories.WalletRepositoryImpl
import com.simple.wallet.domain.repositories.BalanceRepository
import com.simple.wallet.domain.repositories.ChainRepository
import com.simple.wallet.domain.repositories.TokenRepository
import com.simple.wallet.domain.repositories.TransactionRepository
import com.simple.wallet.domain.repositories.UrlRepository
import com.simple.wallet.domain.repositories.WalletConnectRepository
import com.simple.wallet.domain.repositories.WalletRepository
import org.koin.dsl.module

val repositoryModule = module {

    single { UrlRepositoryImpl(get(), getAll()).asObject<UrlRepository>() }

    single { WalletRepositoryImpl(get(), get(), get(), getAll()).asObject<WalletRepository>() }

    single { ChainRepositoryImpl(get(), get(), get(), get(), getAll()).asObject<ChainRepository>() }

    single { BalanceRepositoryImpl(get(), getAll(), getAll(), getAll()).asObject<BalanceRepository>() }

    single { WalletConnectRepositoryImpl(get(), get()).asObject<WalletConnectRepository>() }

    single { TokenRepositoryImpl(get(), get(), getAll(), getAll()).asObject<TokenRepository>() }

    single { TransactionRepositoryImpl(getAll(), getAll(), getAll()).asObject<TransactionRepository>() }
}