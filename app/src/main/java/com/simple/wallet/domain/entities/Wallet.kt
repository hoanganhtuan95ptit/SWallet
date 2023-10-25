package com.simple.wallet.domain.entities

import kotlinx.parcelize.Parcelize

data class Wallet(

    var id: String? = null,

    var name: String = "",

    var type: Type = Type.EMPTY,

    var addressMap: Map<String, Chain.Type> = emptyMap()
) : Entity {

    enum class Type(val value: String) {

        SEED_PHASE("SEED_PHASE"), PRIVATE("PRIVATE"), ADDRESS("ADDRESS"), EMPTY("EMPTY")
    }

    companion object {

        const val ID_ALL = "ID_ALL"

        const val ID_EMPTY = "ID_EMPTY"

        val ALL = Wallet(ID_ALL)

        val EMPTY = Wallet(ID_EMPTY)

        fun String.toWalletType() = Wallet.Type.values().find { this.equals(it.value, true) } ?: error("not support $this")
    }
}