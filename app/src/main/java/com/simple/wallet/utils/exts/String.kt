package com.simple.wallet.utils.exts

import org.web3j.utils.Numeric
import wallet.core.jni.Base58
import wallet.core.jni.Curve
import wallet.core.jni.PrivateKey
import kotlin.experimental.and


fun String?.isSeedPhrase(): Boolean {

    if (this.isNullOrBlank()) {

        return false
    }

    return replace("  ", " ").trim().split(" ").size == 12
}

fun String?.isEvmAddress(): Boolean {

    if (this.isNullOrBlank()) {

        return false
    }

    return length == 42 && startsWith("0x")
}

fun String?.isEvmPrivateKey(): Boolean {

    if (isNullOrBlank()) {

        return false
    }

    return length >= 64 && PrivateKey.isValid(Numeric.hexStringToByteArray(this), Curve.SECP256K1)
}

fun String?.isSolPrivateKey(): Boolean {

    if (isNullOrBlank()) {

        return false
    }

    val length = Base58.decodeNoCheck(this)?.size

    return length == 64 || length == 32
}



fun String.uppercaseFirst(): String = if (length < 1) {

    this
} else {

    substring(0, 1).uppercase() + substring(1)
}