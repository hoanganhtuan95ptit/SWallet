package com.simple.wallet.domain.entities

data class Wallet(

    var id: String? = null,

    var name: String = "",

    var cipher: String = "",

    var type: Type = Type.EMPTY,

    var addressMap: Map<String, Chain.Type> = emptyMap()
) : Entity {


    val isEmpty: Boolean
        get() = id == ID_EMPTY

    val isAllWallet: Boolean
        get() = id == ID_ALL


    val isWatch: Boolean
        get() = type == Type.ADDRESS


    val address: String
        get() = addressMap.toList().first().first

    val chainType: Chain.Type
        get() = addressMap.toList().first().second


    val chainTypeList: List<Chain.Type>
        get() = addressMap.toList().associateBy { it.second }.keys.toList()


    enum class Type(val value: String) {

        SEED_PHASE("SEED_PHASE"), PRIVATE("PRIVATE"), ADDRESS("ADDRESS"), EMPTY("EMPTY")
    }

    companion object {

        const val ID_ALL = "ID_ALL"

        const val ID_EMPTY = "ID_EMPTY"

        val ALL = Wallet(ID_ALL)

        val EMPTY = Wallet(ID_EMPTY)

        fun String.toWalletType() = Wallet.Type.values().first { this.equals(it.value, true) }
    }
}