package com.simple.wallet.domain.tasks

import com.simple.task.Task

interface OutputTypeTask : Task<String, List<Enum<*>>> {

    override suspend fun logStart(param: String, taskId: String) {
    }

    override suspend fun logSuccess(param: String, taskId: String) {
    }

    override suspend fun logFailed(param: String, taskId: String, throwable: Throwable) {
    }
}