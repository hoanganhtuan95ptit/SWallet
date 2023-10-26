package com.simple.wallet.di

import com.simple.wallet.domain.usecases.ApproveConnectUseCase
import com.simple.wallet.domain.usecases.ApproveRequestUseCase
import com.simple.wallet.domain.usecases.CreateWalletUseCase
import com.simple.wallet.domain.usecases.DetectUseCase
import com.simple.wallet.domain.usecases.GetAllChainUseCase
import com.simple.wallet.domain.usecases.GetAllWalletUseCase
import com.simple.wallet.domain.usecases.GetConnectAsyncUseCase
import com.simple.wallet.domain.usecases.GetConnectInfoAsyncUseCase
import com.simple.wallet.domain.usecases.GetInputUseCase
import com.simple.wallet.domain.usecases.GetRequestAsyncUseCase
import com.simple.wallet.domain.usecases.ImportWalletUseCase
import com.simple.wallet.domain.usecases.PairConnectUseCase
import com.simple.wallet.domain.usecases.RejectConnectUseCase
import com.simple.wallet.domain.usecases.RejectRequestUseCase
import org.koin.dsl.module

val useCaseModule = module {

    single { DetectUseCase(getAll(), getAll()) }

    single { GetInputUseCase(getAll()) }

    single { CreateWalletUseCase(get(), getAll()) }

    single { ImportWalletUseCase(get(), getAll()) }

    single { GetAllChainUseCase(get()) }

    single { GetAllWalletUseCase(get()) }

    single { PairConnectUseCase(get()) }

    single { GetConnectInfoAsyncUseCase(get()) }


    single { RejectConnectUseCase(get()) }

    single { ApproveConnectUseCase(get()) }

    single { GetConnectAsyncUseCase(get()) }


    single { RejectRequestUseCase(get()) }

    single { ApproveRequestUseCase(get()) }

    single { GetRequestAsyncUseCase(get()) }
}