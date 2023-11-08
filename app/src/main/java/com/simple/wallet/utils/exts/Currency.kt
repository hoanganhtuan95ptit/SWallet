package com.simple.wallet.utils.exts

import com.simple.coreapp.utils.extentions.text.Text
import com.simple.coreapp.utils.extentions.text.TextStr
import com.simple.wallet.domain.entities.Currency

fun Text.format(currency: Currency) = TextStr(currency.format, this)