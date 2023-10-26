package com.simple.wallet.utils.exts

import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.putExtra
import com.walletconnect.web3.wallet.client.Wallet

fun Wallet.Model.AuthRequest.toSessionRequest() = Request(
    id = id,
    method = Request.Method.SIGN_AUTH
).apply {

//    Auth(
//        statement = payloadParams.statement ?: ""
//    ).let {
//
//        putExtra(Request.ExtraType.AUTH, it)
//    }

    Request.Power(
        url = payloadParams.aud,
        name = payloadParams.domain,
        logo = "https://www.google.com/s2/favicons?sz=128&domain=${payloadParams.aud}"
    ).let {

        putExtra(Request.ExtraType.POWER, it)
    }

    putExtra(Request.ExtraType.TOPIC, pairingTopic)
}
