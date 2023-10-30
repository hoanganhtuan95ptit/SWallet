package com.simple.wallet.data.task.transaction.gasprice

import com.one.web3.task.EvmCall
import com.one.web3.utils.fromWei
import com.simple.wallet.domain.entities.Gas
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.MathContext

class GasPriceEvmCallTask : GasPriceTask, EvmCall {

    override suspend fun executeTask(param: com.one.web3.Param): List<Gas> {

        val estimatedGasPrice = call("eth_gasPrice", emptyList(), param.rpcUrls, param.sync).textValue()!!.let { Numeric.decodeQuantity(it).toBigDecimal().fromWei() }

        val priorityFee = kotlin.runCatching { call("eth_maxPriorityFeePerGas", emptyList(), param.rpcUrls, param.sync) }.getOrNull()?.textValue()?.let { Numeric.decodeQuantity(it).toBigDecimal().fromWei() } ?: BigDecimal.ZERO


        val baseFee = estimatedGasPrice?.minus(priorityFee)


        return estimatedGasPrice.toGas(_baseFee = baseFee, _priorityFee = priorityFee)
    }
}

fun BigDecimal.toGas(_baseFee: BigDecimal? = null, _priorityFee: BigDecimal? = null): List<Gas> {

    val gasPriceStandard = this

    val baseFeeStandard = _baseFee ?: gasPriceStandard

    val priorityFeeStandard = _priorityFee ?: BigDecimal.ZERO

    val list = arrayListOf<Gas>()

    Gas(
        id = Gas.GAS_ID_SLOW,
        baseFee = baseFeeStandard.toPlainString(),
        gasPrice = gasPriceStandard.divide(1.2.toBigDecimal(), MathContext.DECIMAL128).toPlainString(),
        priorityFee = priorityFeeStandard.divide(1.4.toBigDecimal(), MathContext.DECIMAL128).toPlainString(),
    ).let {

        list.add(it)
    }

    Gas(
        id = Gas.GAS_ID_FAST,
        baseFee = baseFeeStandard.toPlainString(),
        gasPrice = gasPriceStandard.multiply(1.4.toBigDecimal(), MathContext.DECIMAL128).toPlainString(),
        priorityFee = priorityFeeStandard.multiply(1.3.toBigDecimal(), MathContext.DECIMAL128).toPlainString(),
        isDefault = true
    ).let {

        list.add(it)
    }

    Gas(
        id = Gas.GAS_ID_SUPER_FAST,
        baseFee = baseFeeStandard.toPlainString(),
        gasPrice = gasPriceStandard.multiply(2.8.toBigDecimal(), MathContext.DECIMAL128).toPlainString(), // gasPriceFast*2
        priorityFee = priorityFeeStandard.multiply(2.7.toBigDecimal(), MathContext.DECIMAL128).toPlainString(),// priorityFeeFast*2
    ).let {

        list.add(it)
    }

    Gas(
        id = Gas.GAS_ID_STANDARD,
        baseFee = baseFeeStandard.toPlainString(),
        gasPrice = gasPriceStandard.toPlainString(),
        priorityFee = priorityFeeStandard.toPlainString()
    ).let {

        list.add(it)
    }

    return list
}

