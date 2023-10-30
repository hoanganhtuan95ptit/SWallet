package com.simple.wallet.domain.tasks

import com.simple.task.Task
import com.simple.wallet.domain.entities.scan.ScanData

interface CameraDetectTask : Task<CameraDetectTask.Param, List<ScanData>> {

    override suspend fun logStart(taskId: String) {
    }

    override suspend fun logSuccess(taskId: String) {
    }

    override suspend fun logFailed(taskId: String, throwable: Throwable) {
    }


    data class Param(val outputTypeList: List<Enum<*>>, val dataList: List<ScanData>)
}