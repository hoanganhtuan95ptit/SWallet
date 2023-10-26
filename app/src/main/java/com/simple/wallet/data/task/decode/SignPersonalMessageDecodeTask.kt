//package com.kyrd.transaction.data.task.decode
//
//import com.kyrd.krystal.domain.usecase.ResultState
//import com.kyrd.transaction.entities.MessageType
//import com.kyrd.transaction.entities.Request
//import com.kyrd.transaction.entities.extra.SignPersonalMessageExtra
//import com.kyrd.wallet.utils.ext.fromHex
//import com.simple.wallet.domain.tasks.RequestDecodeTask
//
//class SignPersonalMessageDecodeTask : RequestDecodeTask {
//
//    override suspend fun executeTask(param: Request): ResultState<Request> {
//
//        val message = param.message ?: error("not support function")
//
//        if (message.type != MessageType.SIGN_PERSONAL_MESSAGE) {
//
//            error("not support function")
//        }
//
//        message.apply {
//
//            extra = SignPersonalMessageExtra(
//                decode = this.message.fromHex()!!.toString(Charsets.UTF_8)
//            )
//        }
//
//        return ResultState.Success(param)
//    }
//}