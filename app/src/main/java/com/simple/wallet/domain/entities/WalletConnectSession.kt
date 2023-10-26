package com.simple.wallet.domain.entities

import android.os.Parcelable
import com.simple.core.utils.extentions.toObjectOrNull
import com.walletconnect.web3.wallet.client.Wallet
import kotlinx.parcelize.Parcelize

@Parcelize
data class WalletConnectSession(
    val topic: String,

    val extra: String,
    val walletId: String,

    val pairToken: String,
    val connectSource: String,

    val timeRequest: Long = System.currentTimeMillis(),
    val timeConnected: Long = System.currentTimeMillis()
) : Parcelable {

    val session: Wallet.Model.Session?
        get() = extra.toObjectOrNull<Wallet.Model.Session>()

    val timeExpired: Long
        get() = timeRequest + 7 * 24 * 60 * 60 * 1000L
}