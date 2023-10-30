package com.simple.wallet.data.task.sign

import com.simple.task.Task
import com.simple.wallet.domain.entities.Request
import wallet.core.jni.PrivateKey

interface SignTask : Task<SignParam, String>

data class SignParam(
    val privateKey: PrivateKey,

    val request: Request
)
