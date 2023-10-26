package com.simple.wallet.domain.tasks

import com.simple.task.Task
import com.simple.wallet.domain.entities.Request

interface SignTask : Task<SignParam, String>

data class SignParam(
    val request: Request
)
