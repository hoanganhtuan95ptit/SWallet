package com.simple.wallet.domain.tasks

import com.simple.task.Task

interface OutputTypeTask : Task<String, List<Enum<*>>> {

    override suspend fun logStart(taskId: String) {
    }

    override suspend fun logSuccess(taskId: String) {
    }

    override suspend fun logFailed(taskId: String, throwable: Throwable) {
    }
}