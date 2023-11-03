package com.simple.wallet.data.task.chaintype.evm

import com.simple.core.utils.extentions.validate
import com.simple.wallet.domain.entities.scan.CameraOutputType
import com.simple.wallet.domain.entities.scan.ScanData
import com.simple.wallet.domain.entities.scan.ScanOutputType
import com.simple.wallet.domain.tasks.CameraDetectTask
import com.simple.wallet.utils.exts.isEvmAddress


class EvmAddressCameraDetectTask : CameraDetectTask {

    override suspend fun executeTask(param: CameraDetectTask.Param): List<ScanData> {

        if (CameraOutputType.WALLET_ADDRESS !in param.outputTypeList) {

            return emptyList()
        }

        return param.dataList.filter {

            it.outputType == ScanOutputType.Qrcode && it.text.isEvmAddress()
        }.validate {

            it.outputType = CameraOutputType.WALLET_ADDRESS
        }
    }
}