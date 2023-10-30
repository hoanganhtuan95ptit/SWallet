package com.simple.wallet.di

import com.simple.analytics.Analytics
import com.simple.crashlytics.Crashlytics
import com.simple.wallet.data.task.CameraActionScanTask
import com.simple.wallet.data.task.CameraInfoScanTask
import com.simple.wallet.data.task.OutputTypeScanTask
import com.simple.wallet.data.task.SeedPhraseDetectTask
import com.simple.wallet.data.task.WalletConnectDetectTask
import com.simple.wallet.data.task.chain.ChainSyncTask
import com.simple.wallet.data.task.chain.DefaultChainSyncTask
import com.simple.wallet.data.task.chaintype.SeedPhraseWalletTypeDetectTask
import com.simple.wallet.data.task.chaintype.evm.EvmAddressDetectTask
import com.simple.wallet.data.task.chaintype.evm.EvmGenerateAddressTask
import com.simple.wallet.data.task.chaintype.evm.EvmPrivateKeyDetectTask
import com.simple.wallet.data.task.chaintype.evm.EvmWalletTypeDetectTask
import com.simple.wallet.data.task.crashlytics.LogAnalytics
import com.simple.wallet.data.task.crashlytics.LogCrashlytics
import com.simple.wallet.data.task.decode.NoneRequestDecodeTask
import com.simple.wallet.data.task.decode.SignPersonalDecodeTask
import com.simple.wallet.data.task.sign.SignMessageEvmTask
import com.simple.wallet.data.task.sign.SignMessageTypeEvmTask
import com.simple.wallet.data.task.sign.SignPersonalMessageEvmTask
import com.simple.wallet.data.task.sign.SignTask
import com.simple.wallet.domain.tasks.CameraActionTask
import com.simple.wallet.domain.tasks.CameraDetectTask
import com.simple.wallet.domain.tasks.CameraInfoTask
import com.simple.wallet.domain.tasks.GenerateAddressTask
import com.simple.wallet.domain.tasks.OutputTypeTask
import com.simple.wallet.domain.tasks.RequestDecodeTask
import com.simple.wallet.domain.tasks.WalletTypeDetectTask
import org.koin.dsl.bind
import org.koin.dsl.module

val taskModule = module {

    single { LogAnalytics() } bind Analytics::class

    single { LogCrashlytics() } bind Crashlytics::class


    single { CameraInfoScanTask() } bind CameraInfoTask::class

    single { CameraActionScanTask() } bind CameraActionTask::class


    single { OutputTypeScanTask() } bind OutputTypeTask::class


    single { EvmAddressDetectTask() } bind CameraDetectTask::class

    single { SeedPhraseDetectTask() } bind CameraDetectTask::class

    single { EvmPrivateKeyDetectTask() } bind CameraDetectTask::class

    single { WalletConnectDetectTask() } bind CameraDetectTask::class


    single { EvmGenerateAddressTask() } bind GenerateAddressTask::class


    single { EvmWalletTypeDetectTask() } bind WalletTypeDetectTask::class

    single { SeedPhraseWalletTypeDetectTask() } bind WalletTypeDetectTask::class


    single { SignMessageEvmTask() } bind SignTask::class

    single { SignMessageTypeEvmTask() } bind SignTask::class

    single { SignPersonalMessageEvmTask() } bind SignTask::class


    single { NoneRequestDecodeTask() } bind RequestDecodeTask::class

    single { SignPersonalDecodeTask() } bind RequestDecodeTask::class
//
//    single { TransferTransactionDecodeTask(get(), get(), get()) } bind RequestDecodeTask::class
//
//    single { PermitMessageDecodeTask(get(), get(), get(), getAll(), getAll()) } bind RequestDecodeTask::class
//
//    single { ApproveTransactionDecodeTask(get(), get(), get(), getAll(), getAll()) } bind RequestDecodeTask::class
//
//    single { TransferErc20TransactionDecodeTask(get(), get(), get(), getAll(), getAll()) } bind RequestDecodeTask::class


    single { DefaultChainSyncTask(get(), get(), get(), get(), get(), get()) } bind ChainSyncTask::class

}
