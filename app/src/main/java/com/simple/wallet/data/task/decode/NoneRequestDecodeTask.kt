package com.simple.wallet.data.task.decode

import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.tasks.RequestDecodeTask

class NoneRequestDecodeTask : RequestDecodeTask {

    override fun priority(): Int {
        return -1
    }

    override suspend fun executeTask(param: Request): Request {

        return param
    }
}
