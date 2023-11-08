package com.simple.wallet.domain.tasks

import com.simple.task.Task
import com.simple.wallet.domain.entities.scan.ScanData

interface CameraDetectTask : Task<CameraDetectTask.Param, List<ScanData>> {

    override suspend fun logStart(param: Param, taskId: String) {
    }

    override suspend fun logSuccess(param: Param, taskId: String) {
    }

    override suspend fun logFailed(param: Param, taskId: String, throwable: Throwable) {
    }


    data class Param(val outputTypeList: List<Enum<*>>, val dataList: List<ScanData>)
}