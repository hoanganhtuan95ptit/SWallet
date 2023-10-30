package com.simple.wallet.data.task.sign

import com.simple.wallet.domain.entities.Request
import com.simple.wallet.utils.exts.fromHex
import com.simple.wallet.utils.exts.toHex
import org.web3j.crypto.Credentials
import org.web3j.crypto.Sign
import wallet.core.jni.CoinType

class SignMessageEvmTask : SignTask {

    override suspend fun executeTask(param: SignParam): String {

        val request = param.request

        val message = request.message?.message

        if (message == null || request.method != Request.Method.SIGN_MESSAGE) {

            error("not support")
        }

        val credentials = Credentials.create(param.privateKey.data().toHex(CoinType.ETHEREUM))

        return Sign.signMessage(message.fromHex(), credentials.ecKeyPair).let {

            it.r.plus(it.s).plus(it.v).toHex()
        }
    }
}