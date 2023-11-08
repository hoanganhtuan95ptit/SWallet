package com.simple.wallet.domain.entities

data class Chain(

    val id: Long,

    var name: String = "",

    var image: String = "",

    var index: Int = 0,

    var type: Type = Type.EVM,

    var explorer: Explorer? = null,


    var config: Map<Config, String>? = null
) : Entity {

    val isEIP1559: Boolean
        get() = config?.get(Config.EIP_1559).toBoolean()

    val isAllNetwork: Boolean
        get() = id == ALL_NETWORK


    enum class Type(val value: String) {

        EVM("EVM"),
        SOL("SOL");
    }

    enum class Config(val value: String) {
        EIP_1559("EIP_1559"),
        IS_TESTNET("IS_TESTNET"),
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
    ) : Entity {

        enum class Type(val value: String) {

            MULTI_CALL_V3("MULTI_CALL_V3"),
        }
    }

    companion object {

        const val ID_MAIN_NET = -1001L
        val MAIN_NET = Chain(ID_MAIN_NET)


        const val ID_TEST_NET = -1002L
        val TEST_NET = Chain(ID_TEST_NET)


        const val ALL_NETWORK = -1000L
        val ALL = Chain(ALL_NETWORK)


        const val ETHEREUM_ID = 1L

        val ETHEREUM = Chain(
            id = ETHEREUM_ID,
            name = "Ethereum",
            image = "https://raw.githubusercontent.com/hoanganhtuan95ptit/cryptodata/main/chain/images/ethereum.png"
        )


        fun String.toChainType() = toChainTypeOrNull()!!

        fun String.toChainTypeOrNull() = Chain.Type.values().firstOrNull { this.equals(it.value, true) }


        fun String.toChainConfig() = toChainConfigOrNull()!!

        fun String.toChainConfigOrNull() = Chain.Config.values().firstOrNull { this.equals(it.value, true) }


        fun String.toChainSmartContractType() = toChainSmartContractTypeOrNull()!!

        fun String.toChainSmartContractTypeOrNull() = Chain.SmartContract.Type.values().firstOrNull { this.equals(it.value, true) }


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