package com.simple.wallet.domain.tasks

import com.simple.task.Task
import com.simple.wallet.domain.entities.scan.ScanInputType

interface InputTypeTask : Task<String, List<ScanInputType>> {
}