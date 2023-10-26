package com.simple.wallet.utils.exts

import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.util.Base64
import com.caverock.androidsvg.SVG
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.image.ImageDrawable
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.coreapp.utils.extentions.text.TextRes
import com.simple.coreapp.utils.extentions.toText
import com.simple.wallet.R
import com.simple.wallet.utils.Hex
import jdenticon.Jdenticon
import org.web3j.utils.Numeric
import wallet.core.jni.Base58
import wallet.core.jni.CoinType
import wallet.core.jni.Curve
import wallet.core.jni.PrivateKey
import java.math.BigDecimal
import java.math.BigInteger
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


fun String?.isSeedPhrase(): Boolean {

    if (this.isNullOrBlank()) {

        return false
    }

    return replace("  ", " ").trim().split(" ").size == 12
}


fun String?.isAddress(chainId: Long): Boolean = this?.isEvmAddress() ?: false


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


fun String.toDrawable(sizePx: Int): ImageDrawable {

    var genAddress = this

    if (!isEvmAddress()) {

        val base64String = Base64.encode(toByteArray(charset("UTF-8")), Base64.DEFAULT)
        genAddress = base64String.toHex(CoinType.ETHEREUM)
    }

    if (genAddress.isEmpty()) {

        genAddress = "0x0000000000000000000000000000000000000000"
    }

    val svg = SVG.getFromString(Jdenticon.toSvg(genAddress.removePrefix("0x"), sizePx))

    return ImageDrawable(PictureDrawable(svg.renderToPicture()))
}


fun String.takeIfBlank() = takeIf { it.isBlank() }


fun String.takeIfNotBlank() = takeIf { it.isNotBlank() }


fun String.uppercaseFirst(): String = if (length < 1) {

    this
} else {

    substring(0, 1).uppercase() + substring(1)
}


fun String?.hexToBigIntegerOrNull() = Hex.hexToBigInteger(this)

fun String?.hexToBigIntegerOrZero() = hexToBigIntegerOrNull() ?: BigInteger.ZERO


fun String?.hexToBigDecimalOrNull() = hexToBigIntegerOrNull()?.toBigDecimal()

fun String?.hexToBigDecimalOrZero() = hexToBigDecimalOrNull() ?: BigDecimal.ZERO


fun String?.takeIfWalletConnectPair() = (if (this == null) {

    null
} else if (this.startsWith("http")) kotlin.runCatching {

    Uri.parse(this).getQueryParameter("uri")!!
}.getOrElse {

    this
} else {

    this
})?.takeIf {

    it.startsWith("wc:") && it.contains("@2?relay-protocol")
}


fun String?.isWalletConnect() = this?.takeIf { it.startsWith("wc:") && it.contains("@2") } != null

fun String?.isWalletConnectPair() = takeIfWalletConnectPair() != null


fun String.toPairingTopic() = substring(3, indexOf("@2?"))


internal fun Long?.toWalletConnectDateAgo(): Text {

    val time = this

    if (time == null || time == 0L) return emptyText()

    val calendar = Calendar.getInstance().also {
        it.time = Date(time)
    }

    val calendarCurrent = Calendar.getInstance()

    val isSameYear = calendar.get(Calendar.YEAR) == calendarCurrent.get(Calendar.YEAR)

    return if (isSameYear && calendar.get(Calendar.DAY_OF_YEAR) == calendarCurrent.get(Calendar.DAY_OF_YEAR)) {
        TextRes(R.string.message_wallet_connect_time, R.string.time_today.toText(), SimpleDateFormat("hh:mm", Locale.US).format(this).toText())
    } else if (isSameYear && calendar.get(Calendar.DAY_OF_YEAR) == (calendarCurrent.get(Calendar.DAY_OF_YEAR) - 1)) {
        TextRes(R.string.message_wallet_connect_time, R.string.time_one_day_ago.toText(), SimpleDateFormat("hh:mm", Locale.US).format(this).toText())
    } else {
        SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(this).toText()
    }
}
