package com.simple.wallet.utils.exts

import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.getExtra
import com.simple.wallet.domain.entities.putExtra
import com.walletconnect.web3.wallet.client.Wallet

fun Wallet.Model.SessionProposal.toSessionRequest() = Request(
    id = System.currentTimeMillis(),
    method = Method.CONNECT
).apply {

    Request.Power(
        url = url,
        name = name,
        logo = icons.getOrNull(0)?.toString() ?: "https://www.google.com/s2/favicons?sz=128&domain=${url}"
    ).let {

        putExtra(Request.ExtraType.POWER, it)
    }

    putExtra(ExtraType.SESSION_PROPOSAL, this@toSessionRequest)
}

val Request.sessionProposal
    get() = getExtra<Wallet.Model.SessionProposal>(ExtraType.SESSION_PROPOSAL)


enum class Method {

    CONNECT
}

enum class ExtraType {

    SESSION_PROPOSAL
}