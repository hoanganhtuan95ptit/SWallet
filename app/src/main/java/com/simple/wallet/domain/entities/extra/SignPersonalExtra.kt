package com.simple.wallet.domain.entities.extra

import com.simple.wallet.domain.entities.Message
import kotlinx.parcelize.Parcelize

data class SignPersonalExtra(
    val decode: String,
) : Message.Extra