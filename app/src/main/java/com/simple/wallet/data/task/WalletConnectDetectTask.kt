//package com.simple.app.data.task
//
//import com.kyrd.base.utils.ext.validate
//import com.kyrd.krystal.domain.usecase.ResultState
//import com.kyrd.walletconnect.utils.ext.isWalletConnectPair
//import com.smile.camera.domain.entities.ScanData
//import com.smile.camera.domain.entities.ScanOutputType
//import com.smile.camera.domain.tasks.DetectTask
//import com.swallet.wallet.domain.entities.CameraOutputType
//
//class WalletConnectDetectTask : DetectTask {
//
//    override suspend fun executeTask(param: DetectTask.Param): ResultState<List<ScanData>> {
//
//        if (CameraOutputType.WALLET_CONNECT !in param.outputTypeList) {
//
//            return ResultState.Success(emptyList())
//        }
//
//        return param.dataList.filter {
//
//            it.outputType == ScanOutputType.Qrcode && it.text.isWalletConnectPair()
//        }.validate {
//
//            it.outputType = CameraOutputType.WALLET_CONNECT
//        }.let {
//
//            ResultState.Success(it)
//        }
//    }
//}