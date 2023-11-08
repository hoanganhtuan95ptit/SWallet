package com.simple.wallet.domain.entities

import java.math.BigInteger

data class Balance(
    val chainId: Long,
    var tokenAddress: String = "",
    var walletAddress: String = "",

    var balance: BigInteger = BigInteger.ZERO,
) : Entity