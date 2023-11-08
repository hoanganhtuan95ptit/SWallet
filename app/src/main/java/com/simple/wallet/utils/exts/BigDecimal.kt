package com.simple.wallet.utils.exts

import com.simple.coreapp.utils.extentions.text.Text
import com.simple.coreapp.utils.extentions.toText
import java.math.BigDecimal
import java.math.BigInteger
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun BigInteger.decimal(decimals: Int? = 0, roundingMode: RoundingMode = RoundingMode.FLOOR): BigDecimal {

    return toBigDecimal().decimal(decimals, roundingMode)
}

fun BigDecimal.decimal(decimals: Int? = 0, roundingMode: RoundingMode = RoundingMode.FLOOR): BigDecimal {

    return divide(BigDecimal.TEN.pow(decimals ?: 0))
}

fun BigDecimal.multiplyToPowerTen(decimals: Int?): BigDecimal {

    return this.multiply(BigDecimal(10).pow(decimals ?: 0))
}


fun String?.toBigDecimalOrNull(): BigDecimal? {

    if (this.isNullOrBlank()) {
        return null
    }

    return try {

        val numberFormat = DecimalFormat.getInstance(Locale.US)

        return if (numberFormat is DecimalFormat) {
            val df: DecimalFormat = numberFormat
            df.isParseBigDecimal = true
            df.parse(this) as BigDecimal
        } else {
            val format = numberFormat.parse(this)?.toString()
            BigDecimal(format)
        }

    } catch (ex: Exception) {

        null
    }
}

fun String?.toBigDecimalOrZero(): BigDecimal = toBigDecimalOrNull() ?: BigDecimal.ZERO

fun String?.toBigDecimalOrDefaultZero(): BigDecimal = toBigDecimalOrNull() ?: BigDecimal.ZERO


private val K__ by lazy { 1000.toBigDecimal() }
private val M__ by lazy { 1000000.toBigDecimal() }
private val B__ by lazy { 1000000000.toBigDecimal() }
private val T__ by lazy { 1000000000000.toBigDecimal() }
private val QUA by lazy { 1000000000000000.toBigDecimal() }
private val QUI by lazy { 1000000000000000000.toBigDecimal() }

/**
 * tìm vị trí có nghĩa
 * @param length độ dài có nghĩa
 */
fun BigDecimal.findIndexMean(length: Int): Int {

    val decimal = subtract(setScale(0, RoundingMode.FLOOR))

    if (decimal == BigDecimal.ZERO) {
        return 0
    }

    var text = decimal.toPlainString()
    text = text.substring(2, kotlin.math.min(text.length, 18))

    var index = text.length
    var count = 0
    var hasMean = false

    // tìm vị trí có nghĩa
    for (i in text.indices) {

        if (text[i].code != 48 && !hasMean) {
            hasMean = true
        }

        if (hasMean) {
            count++
        }

        if (count == length) {
            index = i + 1
            break
        }
    }

    // làm tròn giá trị
    for (i in index - 1 downTo 0) {

        if (i == 0 && text[i].code == 48) {
            return 0
        } else if (text[i].code != 48) {
            return i + 1
        }
    }

    return index
}

/**
 * format number
 */
fun BigDecimal.toDisplay(afterCommaLength: Int? = null): String {

    val afterCommaLength = afterCommaLength ?: findIndexMean(100)

    val pattern = if (afterCommaLength <= 0) {

        "#,##0"
    } else {

        var end = ""

        for (i in 1..afterCommaLength) {
            end += "0"
        }

        "#,##0.$end"
    }

    return DecimalFormat(pattern, DecimalFormatSymbols.getInstance()).format(this)
}

fun String.toDisplay(type: FormatNumberType): Text {

    return toBigDecimalOrDefaultZero().toDisplay(type)
}

fun String.smallConvertToShort(): String {
    if (this == "0") return this

    var count = 0

    var decimalPoint = '.'

    for (c in this.toCharArray()) {
        if (c == '.' || c == ',') decimalPoint = c
        else if (c == '0') count += 1
        else break
    }

    val startIndex = (count + 1).takeIf { it <= this.length } ?: count

    return "0${decimalPoint}0(${count - 1})${this.substring(startIndex)}"
}

fun BigDecimal.toDisplay(type: FormatNumberType): Text = (when {
    type == FormatNumberType.NORMAL -> {
        this.toDisplay()
    }

    type == FormatNumberType.BALANCE -> {
        setScale(6, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type == FormatNumberType.VALUE_USD -> {
        setScale(2, BigDecimal.ROUND_HALF_UP).toDisplay()
    }

    type == FormatNumberType.VALUE_BTC -> {
        setScale(8, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type == FormatNumberType.VALUE_ETH -> {
        setScale(7, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type == FormatNumberType.VALUE_BNB -> {
        setScale(6, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type == FormatNumberType.VALUE_MATIC -> {
        setScale(3, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type == FormatNumberType.VALUE_AVAX -> {
        setScale(5, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type == FormatNumberType.VALUE_FTM -> {
        setScale(3, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type == FormatNumberType.VALUE_CRO -> {
        setScale(3, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type == FormatNumberType.VALUE_KLAY -> {
        setScale(3, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type == FormatNumberType.VALUE_IDR -> {
        setScale(0, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type == FormatNumberType.PERCENTAGE -> {
        setScale(2, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type == FormatNumberType.RATE -> {
        setScale(kotlin.math.min(findIndexMean(3), 1000), BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type == FormatNumberType.SLIPPAGE -> {
        setScale(1, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type == FormatNumberType.MIN_RECEIVED -> {
        setScale(6, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type == FormatNumberType.GAS_FEE && this < BigDecimal.ONE -> {
        setScale(findIndexMean(4), BigDecimal.ROUND_HALF_UP).toDisplay()
    }

    type == FormatNumberType.GAS_FEE && this > BigDecimal.ONE -> {
        setScale(2, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type in listOf(
        FormatNumberType.VALUE_2,
        FormatNumberType.REALTIME_PRICE,
        FormatNumberType.ALL_TIME_HIGH,
        FormatNumberType.ALL_TIME_LOW
    ) && this < BigDecimal(0.0001) -> {

        val priceScale = setScale(kotlin.math.min(findIndexMean(4), 16), BigDecimal.ROUND_FLOOR).toDisplay()
        priceScale.smallConvertToShort()
    }

    type in listOf(
        FormatNumberType.VALUE_2,
        FormatNumberType.REALTIME_PRICE,
        FormatNumberType.ALL_TIME_HIGH,
        FormatNumberType.ALL_TIME_LOW
    ) && this < BigDecimal.ONE -> {

        setScale(kotlin.math.min(findIndexMean(4), 16), BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type in listOf(
        FormatNumberType.VALUE_2,
        FormatNumberType.REALTIME_PRICE,
        FormatNumberType.ALL_TIME_HIGH,
        FormatNumberType.ALL_TIME_LOW
    ) && this >= BigDecimal(100000) -> {
        setScale(0, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type in listOf(
        FormatNumberType.VALUE_2,
        FormatNumberType.REALTIME_PRICE,
        FormatNumberType.ALL_TIME_HIGH,
        FormatNumberType.ALL_TIME_LOW
    ) && this > BigDecimal.ONE -> {
        setScale(2, BigDecimal.ROUND_FLOOR).toDisplay()
    }

    type in listOf(FormatNumberType.MARKET_CAP, FormatNumberType.VOLUME, FormatNumberType.TOTAL_SUPPLY, FormatNumberType.LIQUIDITY) && this < BigDecimal.ONE -> {
        setScale(kotlin.math.min(findIndexMean(1), 8), BigDecimal.ROUND_HALF_UP).toDisplay()
    }

    type in listOf(FormatNumberType.MARKET_CAP, FormatNumberType.VOLUME, FormatNumberType.TOTAL_SUPPLY, FormatNumberType.LIQUIDITY) && this < K__ -> {
        setScale(1, BigDecimal.ROUND_HALF_UP).toDisplay(1)
    }

    type in listOf(FormatNumberType.MARKET_CAP, FormatNumberType.VOLUME, FormatNumberType.TOTAL_SUPPLY, FormatNumberType.LIQUIDITY) && this < M__ -> {
        this.divide(K__, MathContext.DECIMAL128).setScale(1, BigDecimal.ROUND_HALF_UP).toDisplay(1) + "K"
    }

    type in listOf(FormatNumberType.MARKET_CAP, FormatNumberType.VOLUME, FormatNumberType.TOTAL_SUPPLY, FormatNumberType.LIQUIDITY) && this < B__ -> {
        this.divide(M__, MathContext.DECIMAL128).setScale(1, BigDecimal.ROUND_HALF_UP).toDisplay(1) + "M"
    }

    type in listOf(FormatNumberType.MARKET_CAP, FormatNumberType.VOLUME, FormatNumberType.TOTAL_SUPPLY, FormatNumberType.LIQUIDITY) && this < T__ -> {
        this.divide(B__, MathContext.DECIMAL128).setScale(1, BigDecimal.ROUND_HALF_UP).toDisplay(1) + "B"
    }

    type in listOf(FormatNumberType.MARKET_CAP, FormatNumberType.VOLUME, FormatNumberType.TOTAL_SUPPLY, FormatNumberType.LIQUIDITY) && this < QUA -> {
        this.divide(T__, MathContext.DECIMAL128).setScale(1, BigDecimal.ROUND_HALF_UP).toDisplay(1) + "t"
    }

    type in listOf(FormatNumberType.MARKET_CAP, FormatNumberType.VOLUME, FormatNumberType.TOTAL_SUPPLY, FormatNumberType.LIQUIDITY) && this < QUI -> {
        this.divide(QUA, MathContext.DECIMAL128).setScale(1, BigDecimal.ROUND_HALF_UP).toDisplay(1) + "q"
    }

    type in listOf(FormatNumberType.MARKET_CAP, FormatNumberType.VOLUME, FormatNumberType.TOTAL_SUPPLY, FormatNumberType.LIQUIDITY) -> {
        this.divide(QUI, MathContext.DECIMAL128).setScale(1, BigDecimal.ROUND_HALF_UP).toDisplay(1) + "Q"
    }

    else -> {
        setScale(4, BigDecimal.ROUND_HALF_UP).toDisplay()
    }
}).toText()


enum class FormatNumberType {
    BALANCE,
    VALUE_USD,
    VALUE_IDR,
    VALUE_BTC,
    VALUE_ETH,
    VALUE_BNB,
    VALUE_MATIC,
    VALUE_AVAX,
    VALUE_FTM,
    VALUE_CRO,
    VALUE_KLAY,

    VALUE_2,
    REALTIME_PRICE,
    ALL_TIME_HIGH,
    ALL_TIME_LOW,

    MARKET_CAP,
    VOLUME,
    TOTAL_SUPPLY,
    LIQUIDITY,

    PERCENTAGE,
    RATE,
    SLIPPAGE,
    MIN_RECEIVED,
    GAS_FEE,

    NORMAL
}

enum class FormatAddressType {
    FULL_ADDRESS,
    SHORT_ADDRESS
}