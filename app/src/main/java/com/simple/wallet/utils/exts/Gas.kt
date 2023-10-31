package com.simple.wallet.utils.exts

import com.simple.coreapp.utils.extentions.text.TextRes
import com.simple.wallet.R
import com.simple.wallet.domain.entities.Gas


fun Gas?.getSettingOption() = when (this?.id) {
    Gas.GAS_ID_SUPER_FAST -> TextRes(R.string.gas_super_fast_title)
    Gas.GAS_ID_FAST -> TextRes(R.string.gas_fast_title)
    Gas.GAS_ID_STANDARD -> TextRes(R.string.gas_standard_title)
    Gas.GAS_ID_SLOW -> TextRes(R.string.gas_slow_title)
    else -> TextRes(R.string.gas_custom_title)
}

