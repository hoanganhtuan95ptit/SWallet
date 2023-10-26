package com.simple.wallet.domain.entities

import java.math.BigInteger

data class Token(
    var address: String = "",

    var symbol: String = "",

    var name: String = "",

    var decimals: Int = 0,

    var logo: String = "",

    var chainId: Long = 0,

    var tag: String = ""
) : Entity {

    var cgkId: String = ""

    var balance: BigInteger = BigInteger.ZERO
}