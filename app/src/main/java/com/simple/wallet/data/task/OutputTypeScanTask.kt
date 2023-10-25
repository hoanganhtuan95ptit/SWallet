package com.simple.wallet.data.task

import com.simple.task.LowException
import com.simple.wallet.domain.entities.scan.CameraOutputType
import com.simple.wallet.domain.tasks.OutputTypeTask

class OutputTypeScanTask : OutputTypeTask {

    override suspend fun executeTask(param: String): List<Enum<*>> {

        if (!param.equals("scan", true)) {
            throw LowException("")
        }

        return CameraOutputType.values().toList()
    }
}