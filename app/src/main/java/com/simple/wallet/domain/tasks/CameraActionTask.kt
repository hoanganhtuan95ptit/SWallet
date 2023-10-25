package com.simple.wallet.domain.tasks

import com.simple.adapter.ViewItemCloneable
import com.simple.task.Task
import com.simple.wallet.domain.entities.scan.ScanInputType

interface CameraActionTask : Task<CameraActionTask.Param, List<ViewItemCloneable>> {

    data class Param(val action: String, val scanInputType: ScanInputType)
}