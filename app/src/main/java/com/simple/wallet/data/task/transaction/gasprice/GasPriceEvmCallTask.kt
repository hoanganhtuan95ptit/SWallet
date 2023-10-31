package com.simple.wallet.data.task.transaction.gasprice

import android.util.Log
import com.one.web3.task.EvmCall
import com.simple.wallet.domain.entities.Gas
import org.web3j.utils.Numeric
import java.math.BigDecimal
import java.math.MathContext

class GasPriceEvmCallTask : GasPriceTask, EvmCall {

    override suspend fun executeTask(param: com.one.web3.Param): List<Gas> {

        val estimatedGasPrice = call("eth_gasPrice", emptyList(), param.rpcUrls, param.sync).textValue()!!.let { Numeric.decodeQuantity(it).toBigDecimal() }

        val priorityFee = kotlin.runCatching { call("eth_maxPriorityFeePerGas", emptyList(), param.rpcUrls, param.sync) }.getOrNull()?.textValue()?.let { Numeric.decodeQuantity(it).toBigDecimal() } ?: BigDecimal.ZERO

        return estimatedGasPrice.toGas(baseFeeWei = estimatedGasPrice.minus(priorityFee), priorityFeeWei = priorityFee)
    }

    private fun BigDecimal.toGas(baseFeeWei: BigDecimal, priorityFeeWei: BigDecimal): List<Gas> {


        val gasPriceWeiStandard = this

        val baseFeeWeiStandard = baseFeeWei

        val priorityFeeWeiStandard = priorityFeeWei


        val list = arrayListOf<Gas>()

        Gas(
            id = Gas.GAS_ID_SLOW,
            baseFeeWei = baseFeeWeiStandard,
            gasPriceWei = gasPriceWeiStandard.divide(1.2.toBigDecimal(), MathContext.DECIMAL128),
            priorityFeeWei = priorityFeeWeiStandard.divide(1.4.toBigDecimal(), MathContext.DECIMAL128),
        ).let {

            list.add(it)
        }

        Gas(
            id = Gas.GAS_ID_FAST,
            baseFeeWei = baseFeeWeiStandard,
            gasPriceWei = gasPriceWeiStandard.multiply(1.4.toBigDecimal(), MathContext.DECIMAL128),
            priorityFeeWei = priorityFeeWeiStandard.multiply(1.3.toBigDecimal(), MathContext.DECIMAL128),
        ).let {

            list.add(it)
        }

        Gas(
            id = Gas.GAS_ID_SUPER_FAST,
            baseFeeWei = baseFeeWeiStandard,
            gasPriceWei = gasPriceWeiStandard.multiply(2.8.toBigDecimal(), MathContext.DECIMAL128),
            priorityFeeWei = priorityFeeWeiStandard.multiply(2.7.toBigDecimal(), MathContext.DECIMAL128),
        ).let {

            list.add(it)
        }

        Gas(
            id = Gas.GAS_ID_STANDARD,
            baseFeeWei = baseFeeWeiStandard,
            gasPriceWei = gasPriceWeiStandard,
            priorityFeeWei = priorityFeeWeiStandard
        ).let {

            list.add(it)
        }

        return list
    }
}
