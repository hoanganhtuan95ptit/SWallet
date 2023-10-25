package com.simple.wallet.domain.entities.extra

import com.simple.wallet.domain.entities.Message
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.entities.Transaction
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

data class ApproveExtra(
    val tokenApprove: Token,
    val amountApprove: BigInteger,

    val senderAddress: String
) : Transaction.Extra, Message.Extra