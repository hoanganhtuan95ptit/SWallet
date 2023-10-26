package com.simple.wallet.utils.exts

import com.simple.coreapp.utils.extentions.emptyImage
import com.simple.coreapp.utils.extentions.image.Image
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.coreapp.utils.extentions.toImage
import com.simple.coreapp.utils.extentions.toText
import com.simple.wallet.DP_24
import com.simple.wallet.R
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Wallet


val Wallet.nameDisplay: Text
    get() = if (id == Wallet.ID_ALL) {

        R.string.all_wallet.toText()
    } else {

        name.toText()
    }

val Wallet.typeDisplay: Text
    get() = if (id == Wallet.ID_ALL) {

        R.string.all_wallet.toText()
    } else if (type == Wallet.Type.SEED_PHASE) {

        R.string.multi_chain.toText()
    } else if (type == Wallet.Type.PRIVATE && addressMap.any { it.value == Chain.Type.EVM }) {

        R.string.evm.toText()
    } else {

        R.string.watch.toText()
    }

val Wallet.imageDisplay: Image
    get() = if (id == Wallet.ID_ALL) {

        R.drawable.img_all_network.toImage()
    } else {

        addressMap.keys.firstOrNull()?.toDrawable(DP_24) ?: emptyImage()
    }


fun String.shortenValue(): String = StringBuilder()
    .append(substring(0, if (length > 8) 8 else length))
    .append("...")
    .append(substring(if (length > 4) (length - 4) else length))
    .toString()
