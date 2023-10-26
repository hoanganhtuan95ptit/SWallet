package com.simple.wallet.domain.entities

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

        const val ALL_NETWORK = -1000L

        val ALL = Chain(ALL_NETWORK)

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