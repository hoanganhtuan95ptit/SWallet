package com.simple.wallet.domain.entities

import android.os.Parcelable
import com.simple.wallet.domain.entities.Wallet.Companion.toWalletType
import kotlinx.parcelize.Parcelize
import java.io.Serializable

data class Chain(

    val id: Long,

    var name: String = "",

    var image: String = "",

    var type: Type = Type.EVM
) : Entity {

    enum class Type(val value: String) {

        EVM("EVM"),
        SOL("SOL");
    }

    companion object {

        fun String.toChainType() = Chain.Type.values().find { this.equals(it.value, true) } ?: error("not support $this")

        fun String.fromNamespace() = if (this == "eip155") {
            Type.EVM
        } else if (this == "solana") {
            Type.SOL
        } else {
            error("not support")
        }

        fun Type.toNamespace() = if (this == Type.EVM) {
            "eip155"
        } else if (this == Type.SOL) {
            "solana"
        } else {
            error("not support")
        }
    }
}