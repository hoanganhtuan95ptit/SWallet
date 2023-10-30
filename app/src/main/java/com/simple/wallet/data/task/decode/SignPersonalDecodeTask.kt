package com.simple.wallet.data.task.decode

import com.simple.wallet.domain.entities.Message
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.extra.SignPersonalExtra
import com.simple.wallet.domain.tasks.RequestDecodeTask
import com.simple.wallet.utils.exts.fromHex

class SignPersonalDecodeTask : RequestDecodeTask {

    override suspend fun executeTask(param: Request): Request {

        val message = param.message ?: error("not support function")

        if (message.type != Message.Type.SIGN_PERSONAL_MESSAGE) {

            error("not support function")
        }

        message.apply {

            extra = SignPersonalExtra(
                decode = this.message.fromHex()!!.toString(Charsets.UTF_8)
            )
        }

        return param
    }
}