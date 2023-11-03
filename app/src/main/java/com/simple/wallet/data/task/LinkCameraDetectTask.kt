package com.simple.wallet.data.task

import com.simple.core.utils.extentions.validate
import com.simple.wallet.domain.entities.scan.CameraOutputType
import com.simple.wallet.domain.entities.scan.ScanData
import com.simple.wallet.domain.entities.scan.ScanOutputType
import com.simple.wallet.domain.tasks.CameraDetectTask

class LinkCameraDetectTask : CameraDetectTask {

    override suspend fun executeTask(param: CameraDetectTask.Param): List<ScanData> {

        if (CameraOutputType.LINK !in param.outputTypeList) {

            return emptyList()
        }

        return param.dataList.filter {

            it.outputType == ScanOutputType.Qrcode && it.text.startsWith("http")
        }.validate {

            it.outputType = CameraOutputType.LINK
        }
    }
}