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
import com.simple.wallet.domain.entities.putExtra
import com.walletconnect.web3.wallet.client.Wallet


fun Wallet.Model.SessionRequest.toSessionRequest() = Request(
    id = this.request.id,
    method = this.request.method.toSessionMethod(),
).apply {

    val chainId = this@toSessionRequest.chainId?.split(":")?.lastOrNull()?.toLong() ?: Chain.ALL_NETWORK

    val paramList = this@toSessionRequest.request.params.toListOrEmpty<JsonNode>()

    Request.Power(
        url = this@toSessionRequest.peerMetaData!!.url,
        name = this@toSessionRequest.peerMetaData!!.name,
        logo = this@toSessionRequest.peerMetaData!!.icons[0]
    ).let {

        putExtra(Request.ExtraType.POWER, it)
    }

    paramList.getMessageOrNull(chainId, method)?.let {

        Message("it", chainId, this@toSessionRequest.request.method.toMessageType())
    }?.let {

        putExtra(Request.ExtraType.MESSAGE, it)
    }

    paramList.getTransactionOrNull(chainId, method)?.let {

        putExtra(Request.ExtraType.TRANSACTION, it)
    }

    paramList.getWalletAddressOrNull(chainId, method)?.let {

        putExtra(Request.ExtraType.WALLET_ADDRESS, it)
    }

    putExtra(Request.ExtraType.TOPIC, this@toSessionRequest.topic)
}

private fun List<JsonNode>.getMessageOrNull(chainId: Long, method: Enum<*>) = takeIf {

    method in listOf(Request.Method.SIGN_PERSONAL_MESSAGE, Request.Method.SIGN_MESSAGE, Request.Method.SIGN_MESSAGE_TYPED)
}?.find {

    val item = it.asObjectOrNull<TextNode>()?.getString()

    !item.isAddress(chainId)
}.run {

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

        nonce = 0,
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
} ?: run {

    getTransactionOrNull(chainId, method)?.from
}
