package com.simple.wallet.domain.entities.extra

import com.simple.wallet.domain.entities.Token
import com.simple.wallet.domain.entities.Transaction
import kotlinx.parcelize.Parcelize
import java.math.BigInteger

data class TransferExtra(
    val tokenTransfer: Token,
    val amountTransfer: BigInteger,

    val receiverAddress: String
) : Transaction.Extra