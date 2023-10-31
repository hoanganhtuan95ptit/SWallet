package com.simple.wallet.utils.exts

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import com.simple.core.utils.extentions.asObjectOrNull
import com.simple.core.utils.extentions.toListOrEmpty
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Message
import com.simple.wallet.domain.entities.Message.Type.Companion.toMessageType
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.Request.Method.Companion.toSessionMethod
import com.simple.wallet.domain.entities.Transaction
import com.walletconnect.web3.wallet.client.Wallet
import java.math.BigInteger


fun Wallet.Model.SessionRequest.toSessionRequest() = Request(
    id = this.request.id,
    method = this.request.method.toSessionMethod(),
).apply {

    val _chainId = this@toSessionRequest.chainId?.split(":")?.lastOrNull()?.toLong() ?: Chain.ALL_NETWORK

    val paramList = this@toSessionRequest.request.params.toListOrEmpty<JsonNode>()

    topic = this@toSessionRequest.topic

    power = Request.Power(
        url = this@toSessionRequest.peerMetaData!!.url,
        name = this@toSessionRequest.peerMetaData!!.name,
        logo = this@toSessionRequest.peerMetaData!!.icons[0]
    )

    message = paramList.getMessageOrNull(_chainId, method)?.let {

        Message(it, _chainId, this@toSessionRequest.request.method.toMessageType())
    }

    chainId = _chainId

    transaction = paramList.getTransactionOrNull(_chainId, method)

    walletAddress = paramList.getWalletAddressOrNull(_chainId, method)
}

private fun List<JsonNode>.getMessageOrNull(chainId: Long, method: Enum<*>) = takeIf {

    method in listOf(Request.Method.SIGN_PERSONAL_MESSAGE, Request.Method.SIGN_MESSAGE, Request.Method.SIGN_MESSAGE_TYPED)
}?.find {

    val item = it.asObjectOrNull<TextNode>()?.getString()

    !item.isAddress(chainId)
}?.run {

    getString()
}

private fun List<JsonNode>.getTransactionOrNull(chainId: Long, method: Enum<*>) = takeIf {

    method in listOf(Request.Method.SEND_TRANSACTION, Request.Method.SIGN_TRANSACTION, Request.Method.SIGN_TRANSACTION_RAW)
}?.find {

    val item = it.asObjectOrNull<ObjectNode>() ?: return@find false

    item.get("to") != null || item.get("from") != null
}?.run {

    val transaction = Transaction(
        txHash = "",

        to = this.getString("to"),
        from = this.getString("from"),

        data = this.getStringOrNull("raw") ?: this.getStringOrNull("data") ?: "",
        value = this.getString("value").hexToBigIntegerOrZero(),

        nonce = BigInteger.ZERO,
        gasPrice = this.getString("maxFeePerGas").hexToBigDecimalOrZero(),
        gasLimit = this.getString("gasLimit").hexToBigIntegerOrZero(),
        priorityFee = this.getString("maxPriorityFeePerGas").hexToBigDecimalOrZero()
    )

    transaction.chainId = chainId

    transaction
}

private fun List<JsonNode>.getWalletAddressOrNull(chainId: Long, method: Enum<*>) = takeIf {

    method in listOf(Request.Method.SIGN_PERSONAL_MESSAGE, Request.Method.SIGN_MESSAGE, Request.Method.SIGN_MESSAGE_TYPED)
}?.find {

    val item = it.asObjectOrNull<TextNode>()?.getString()

    item.isAddress(chainId)
}?.getString() ?: run {

    getTransactionOrNull(chainId, method)?.from
}
