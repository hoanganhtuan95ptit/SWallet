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

    var price: BigDecimal = BigDecimal.ZERO,

    var tag: Tag = Tag.UNKNOWN,

    var type: Type = Type.ERC_20,

    var geckoId: String = ""
) : Entity {

    var balance: BigInteger = BigInteger.ZERO

    enum class Type(val value: String) {

        NATIVE("NATIVE"), ERC_20("ERC_20")
    }

    enum class Tag(val value: String) {
        SCAM("SCAM"), VERIFIED("VERIFIED"), PROMOTION("PROMOTION"), UNKNOWN("UNKNOWN")
    }

    companion object {

        const val TOKEN_NATIVE_ADDRESS_DEFAULT = "0x"

        fun String.toTokenTag() = Token.Tag.values().first { this.equals(it.value, true) }

        fun String.toTokenType() = Token.Type.values().first { this.equals(it.value, true) }
    }
}