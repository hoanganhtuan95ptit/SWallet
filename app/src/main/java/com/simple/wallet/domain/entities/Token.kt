package com.simple.wallet.domain.entities

import java.math.BigDecimal
import java.math.BigInteger

data class Token(
    var address: String = "",

    var symbol: String = "",

    var name: String = "",

    var decimals: Int = 0,

    var logo: String = "",

    var chainId: Long = 0,

    var tag: Tag = Tag.UNKNOWN,

    var type: Type = Type.ERC_20,

    var geckoId: String = ""
) : Entity {


    var chain: Chain? = null

    var price: Price? = null


    var walletAddressAndBalance: Map<String, BigInteger> = hashMapOf()

    val balance: BigInteger
        get() = walletAddressAndBalance.values.sumOf { it }


    data class Price(
        val chainId: Long,
        var address: String = "",

        var price: BigDecimal,
        var priceChange: Map<Change, BigDecimal> = emptyMap()
    ) : Entity {

        enum class Change(val value: String) {
            CHANGE_1H("CHANGE_1H"),
            CHANGE_24H("CHANGE_24H"),
            CHANGE_7D("CHANGE_7D"),
            CHANGE_14D("CHANGE_14D"),
            CHANGE_30D("CHANGE_30D"),
            CHANGE_200D("CHANGE_200D"),
            CHANGE_1Y("CHANGE_1Y"),
        }
    }

    enum class Type(val value: String) {

        NATIVE("NATIVE"),
        ERC_20("ERC_20")
    }

    enum class Tag(val value: String, val level: Int) {

        SCAM("SCAM", 3),
        VERIFIED("VERIFIED", 0),
        PROMOTION("PROMOTION", 1),
        UNKNOWN("UNKNOWN", 2)
    }

    companion object {

        const val TOKEN_NATIVE_ADDRESS_DEFAULT = "0x"

        fun String.toTokenTag() = Token.Tag.values().first { this.equals(it.value, true) }

        fun String.toTokenType() = Token.Type.values().first { this.equals(it.value, true) }


        fun String.toTokenPriceChange() = toTokenPriceChangeOrNull()!!

        fun String.toTokenPriceChangeOrNull() = Token.Price.Change.values().firstOrNull { it.value.equals(this, true) }
    }
}