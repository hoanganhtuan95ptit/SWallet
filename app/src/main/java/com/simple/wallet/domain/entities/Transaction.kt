package com.simple.wallet.domain.entities

import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import java.math.BigInteger

data class Transaction(
    val txHash: String,

    val to: String, val from: String,

    val data: String, val value: BigInteger,

    val nonce: Int, val gasPrice: BigDecimal, val gasLimit: BigInteger, val priorityFee: BigDecimal
) : Entity {

    var time: Long = 0

    var chainId: Long = 0


    var type: Type = Type.EXECUTION

    var extra: Extra? = null

    var status: Status = Status.UNDEFINED


    enum class Type {

        SEND, RECEIVED, SWAP, APPROVAL, DEPOSIT, WITHDRAWAL, CLAIM_REWARD, MULTI_SEND, BRIDGE, STAKE, UN_STAKE, CLAIM_STAKE, CLAIM_STAKE_REWARD, EXECUTION;
    }


    interface Extra : Entity


    enum class Status(val value: String) {

        UNDEFINED("undefined"), PENDING("pending"), SUCCESS("success"), FAILED("failed"), DROP("drop");

        companion object {

            fun String.toTransactionStatus() = values().find { it.value.equals(this, true) }
        }
    }
}