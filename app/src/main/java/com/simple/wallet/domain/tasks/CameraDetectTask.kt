package com.simple.wallet.domain.tasks

import com.simple.task.Task
import com.simple.wallet.domain.entities.scan.ScanData

interface CameraDetectTask : Task<CameraDetectTask.Param, List<ScanData>> {

    data class Param(val outputTypeList: List<Enum<*>>, val dataList: List<ScanData>)
}