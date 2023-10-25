package com.simple.wallet.di

import com.simple.analytics.Analytics
import com.simple.crashlytics.Crashlytics
import com.simple.wallet.data.task.crashlytics.LogAnalytics
import com.simple.wallet.data.task.crashlytics.LogCrashlytics
import com.simple.wallet.data.task.CameraActionScanTask
import com.simple.wallet.data.task.CameraInfoScanTask
import com.simple.wallet.data.task.chaintype.evm.EvmAddressDetectTask
import com.simple.wallet.data.task.chaintype.evm.EvmPrivateKeyDetectTask
import com.simple.wallet.data.task.OutputTypeScanTask
import com.simple.wallet.data.task.SeedPhraseDetectTask
import com.simple.wallet.data.task.chaintype.SeedPhraseWalletTypeDetectTask
import com.simple.wallet.data.task.chaintype.evm.EvmGenerateAddressTask
import com.simple.wallet.data.task.chaintype.evm.EvmWalletTypeDetectTask
import com.simple.wallet.domain.tasks.CameraActionTask
import com.simple.wallet.domain.tasks.CameraInfoTask
import com.simple.wallet.domain.tasks.CameraDetectTask
import com.simple.wallet.domain.tasks.GenerateAddressTask
import com.simple.wallet.domain.tasks.OutputTypeTask
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


    single { EvmGenerateAddressTask() } bind GenerateAddressTask::class


    single { EvmWalletTypeDetectTask() } bind WalletTypeDetectTask::class

    single { SeedPhraseWalletTypeDetectTask() } bind WalletTypeDetectTask::class
}
