package com.simple.wallet.utils.exts

import com.simple.wallet.domain.entities.Chain
import org.web3j.utils.Numeric
import wallet.core.jni.CoinType

fun Chain.Type.toCoinType(): CoinType = when (this) {

    Chain.Type.EVM -> CoinType.ETHEREUM

    else -> error("not support ${this.name}")
}


fun ByteArray.toHex(coinType: CoinType = CoinType.ETHEREUM) = if (coinType == CoinType.ETHEREUM) {
    Numeric.toHexString(this)
} else {
    error("not support ${coinType.name}")
}

fun String.fromHex(coinType: CoinType = CoinType.ETHEREUM) = if (coinType == CoinType.ETHEREUM) {
    Numeric.hexStringToByteArray(this).takeIf { isNotEmpty() }
} else {
    error("not support ${coinType.name}")
}