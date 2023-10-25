package com.simple.wallet.di

import com.simple.wallet.domain.usecases.CreateWalletUseCase
import com.simple.wallet.domain.usecases.DetectUseCase
import com.simple.wallet.domain.usecases.GetInputUseCase
import com.simple.wallet.domain.usecases.ImportWalletUseCase
import org.koin.dsl.module

val useCaseModule = module {

    single {
        DetectUseCase(getAll(), getAll())
    }

    single {
        GetInputUseCase(getAll())
    }

    single {

        CreateWalletUseCase(get(), getAll())
    }

    single {

        ImportWalletUseCase(get(), getAll())
    }
}