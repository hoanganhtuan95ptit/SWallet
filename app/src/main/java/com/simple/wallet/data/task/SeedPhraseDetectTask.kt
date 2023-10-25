package com.simple.wallet.data.task

import com.simple.core.utils.extentions.validate
import com.simple.wallet.domain.entities.scan.CameraOutputType
import com.simple.wallet.domain.entities.scan.ScanData
import com.simple.wallet.domain.entities.scan.ScanOutputType
import com.simple.wallet.domain.tasks.CameraDetectTask


class SeedPhraseDetectTask : CameraDetectTask {

    override suspend fun executeTask(param: CameraDetectTask.Param): List<ScanData> {

        if (CameraOutputType.SEED_PHRASE !in param.outputTypeList) {

            return emptyList()
        }

        return param.dataList.filter {

            it.outputType == ScanOutputType.Qrcode && it.text.split(" ").size == 12
        }.validate {

            it.outputType = CameraOutputType.SEED_PHRASE
        }
    }

    fun String.isEvmAddress(): Boolean {
        return (startsWith("0x") && length == 42)
    }
}