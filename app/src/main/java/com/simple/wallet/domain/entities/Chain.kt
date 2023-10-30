package com.simple.wallet.domain.entities

data class Chain(

    val id: Long,

    var name: String = "",

    var image: String = "",


    var type: Type = Type.EVM,

    var explorer: Explorer? = null,


    var config: Map<Config, String>? = null
) : Entity {


    enum class Type(val value: String) {

        EVM("EVM"),
        SOL("SOL");
    }

    enum class Config(val data: String) {


    }

    data class Rpc(
        val chainId: Long,
        var priority: Int = 0,

        var url: String = "",
        var name: String = "",
    )

    data class Explorer(
        val url: String = "",
        val name: String = "",
    ) : Entity

    data class SmartContract(
        val chainId: Long = 0,

        val type: String = "",
        val address: String = "",
    ) : Entity


    companion object {

        const val ALL_NETWORK = -1000L

        val ALL = Chain(ALL_NETWORK)


        const val ETHEREUM_ID = 1L

        val ETHEREUM = Chain(
            id = ETHEREUM_ID,
            name = "Ethereum",
            image = "https://raw.githubusercontent.com/hoanganhtuan95ptit/cryptodata/main/chain/images/ethereum.png"
        )


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