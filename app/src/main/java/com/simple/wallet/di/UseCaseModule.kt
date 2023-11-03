package com.simple.wallet.di

import com.simple.wallet.domain.usecases.DetectRequestAsyncUseCase
import com.simple.wallet.domain.usecases.SyncUseCase
import com.simple.wallet.domain.usecases.camera.CameraDetectUseCase
import com.simple.wallet.domain.usecases.camera.GetInputUseCase
import com.simple.wallet.domain.usecases.chain.GetAllChainUseCase
import com.simple.wallet.domain.usecases.chain.GetChainByUseCase
import com.simple.wallet.domain.usecases.chain.GetChainSelectedUseCase
import com.simple.wallet.domain.usecases.message.SignMessageUseCase
import com.simple.wallet.domain.usecases.token.GetTokenByUseCase
import com.simple.wallet.domain.usecases.transaction.GetGasAsyncUseCase
import com.simple.wallet.domain.usecases.transaction.GetGasLimitAsyncUseCase
import com.simple.wallet.domain.usecases.transaction.SendTransactionUseCase
import com.simple.wallet.domain.usecases.url.QueryUseCase
import com.simple.wallet.domain.usecases.wallet.CreateWalletUseCase
import com.simple.wallet.domain.usecases.wallet.GetAllWalletUseCase
import com.simple.wallet.domain.usecases.wallet.GetWalletByUseCase
import com.simple.wallet.domain.usecases.wallet.GetWalletSelectedUseCase
import com.simple.wallet.domain.usecases.wallet.ImportWalletUseCase
import com.simple.wallet.domain.usecases.walletconnect.ApproveConnectUseCase
import com.simple.wallet.domain.usecases.walletconnect.ApproveRequestUseCase
import com.simple.wallet.domain.usecases.walletconnect.GetConnectAsyncUseCase
import com.simple.wallet.domain.usecases.walletconnect.GetConnectInfoAsyncUseCase
import com.simple.wallet.domain.usecases.walletconnect.GetRequestAsyncUseCase
import com.simple.wallet.domain.usecases.walletconnect.PairConnectUseCase
import com.simple.wallet.domain.usecases.walletconnect.RejectConnectUseCase
import com.simple.wallet.domain.usecases.walletconnect.RejectRequestUseCase
import org.koin.dsl.module

val useCaseModule = module {

    single { SyncUseCase(get(), get()) }


    single { CameraDetectUseCase(getAll(), getAll()) }

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


    single { DetectRequestAsyncUseCase(get(), getAll()) }


    single { GetChainSelectedUseCase(get()) }

    single { GetWalletSelectedUseCase(get()) }


    single { SignMessageUseCase(get()) }

    single { SendTransactionUseCase(get(), get()) }


    single { QueryUseCase(get()) }


    single { GetChainByUseCase(get()) }

    single { GetTokenByUseCase(get()) }

    single { GetWalletByUseCase(get()) }

    single { GetGasAsyncUseCase(get(), get()) }

    single { GetGasLimitAsyncUseCase(get(), get()) }
}