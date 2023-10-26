package com.simple.wallet.utils.exts

import com.simple.coreapp.utils.extentions.image.Image
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.coreapp.utils.extentions.toImage
import com.simple.coreapp.utils.extentions.toText
import com.simple.wallet.R
import com.simple.wallet.domain.entities.Chain

val Chain.nameDisplay: Text
    get() = if (id == Chain.ALL_NETWORK) {

        R.string.all_network.toText()
    } else {

        name.toText()
    }

val Chain.imageDisplay: Image
    get() = if (id == Chain.ALL_NETWORK) {

        R.drawable.img_all_network.toImage()
    } else {

        image.toImage()
    }