package com.simple.wallet.domain.entities.scan

import com.simple.wallet.domain.entities.Entity
import kotlinx.parcelize.Parcelize

data class ScanData(
    val text: String,

    var outputType: Enum<*>,

    var inputType: ScanInputType = ScanInputType.Qrcode,
) : Entity