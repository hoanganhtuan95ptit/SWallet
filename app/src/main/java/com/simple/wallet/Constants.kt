package com.simple.wallet

import com.simple.coreapp.utils.extentions.toPx
import java.math.BigDecimal
import java.math.BigInteger

val LOGO_APP by lazy { "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcR2wNCC0-K9uP7MXLt5E7_i9YmEEDOxNwhp5YVwtzl2YDXEOqG7xGcP6erV5MHl4YRIyto&usqp=CAU" }

val DATA by lazy { "DATA" }

val PARAM_DATA by lazy { "PARAM_DATA" }
val PAYLOAD_PAIR by lazy { "PAYLOAD_PAIR" }
val PAYLOAD_SLIDE by lazy { "PAYLOAD_SLIDE" }
val PARAM_RESULT_STATUS by lazy { "PARAM_RESULT_STATUS" }


val UNLIMITED by lazy { 2.toBigInteger().pow(256) - BigInteger.ONE }

val UNLIMITED_DISPLAY by lazy { BigDecimal.TEN.pow(27) }

val UNLIMITED_BIG_DECIMAL by lazy { UNLIMITED.toBigDecimal() }


val GAS_LIMIT_DEFAULT by lazy { "500000".toBigInteger() }


val DP_8 by lazy { 8.toPx() }
val DP_12 by lazy { 12.toPx() }
val DP_16 by lazy { 16.toPx() }
val DP_20 by lazy { 20.toPx() }
val DP_24 by lazy { 24.toPx() }
val DP_28 by lazy { 28.toPx() }
val DP_32 by lazy { 32.toPx() }
val DP_36 by lazy { 36.toPx() }
val DP_40 by lazy { 40.toPx() }
