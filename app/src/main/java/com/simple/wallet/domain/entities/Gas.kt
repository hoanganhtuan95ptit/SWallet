package com.simple.wallet.domain.entities

import java.math.BigDecimal

data class Gas(
    var id: String = "",

    var baseFeeWei: BigDecimal = BigDecimal.ZERO,
    var gasPriceWei: BigDecimal = BigDecimal.ZERO,
    var priorityFeeWei: BigDecimal = BigDecimal.ZERO,
) : Entity {

    var isDefault: Boolean = false

    val isCustom: Boolean
        get() = id == GAS_ID_CUSTOM

//
//    val estimatedGasPrice: String
//        get() {
//            return (baseFee.toBigDecimalOrDefaultZero() + priorityFee.toBigDecimalOrDefaultZero()).toString()
//        }
//
//    val gasPriceDisplay: String
//        get() = gasPrice.toBigDecimalOrDefaultZero().toDisplay(FormatNumberType.GAS_FEE)
//
//    val baseFeeDisplay: String
//        get() = baseFee.toBigDecimalOrDefaultZero().toDisplay(FormatNumberType.GAS_FEE)
//
//    val priorityFeeDisplay: String
//        get() = priorityFee.toBigDecimalOrDefaultZero().toDisplay(FormatNumberType.GAS_FEE)
//
//    fun getGasFormula(gasLimit: BigDecimal, is1559: Boolean): Text<*> {
//
//        val gasType = TextRes(if (is1559) R.string.formula_max_gas else R.string.formula_gas_price)
//
//        return TextRes(
//            R.string.gas_formula,
//            TextStr(gasPrice.toDisplay(FormatNumberType.BALANCE)),
//            gasType,
//            TextStr(gasLimit.toString()),
//            TextRes(R.string.formula_gas_limit)
//        )
//    }


    companion object {

        const val GAS_ID_SLOW = "slow"
        const val GAS_ID_FAST = "fast"
        const val GAS_ID_CUSTOM = "custom"
        const val GAS_ID_STANDARD = "standard"
        const val GAS_ID_SUPER_FAST = "super_fast"
    }
}

//fun Gas.gas(gasLimit: BigDecimal, bonusFee: BigDecimal = BigDecimal.ZERO): BigDecimal {
//
//    return gasPrice.toBigDecimalOrDefaultZero().gas(gasLimit, bonusFee)
//}
//
//fun BigDecimal.gas(gasLimit: BigDecimal, bonusFee: BigDecimal = BigDecimal.ZERO): BigDecimal {
//
//    return multiply(gasLimit).toWei()
//        .plus(bonusFee)
//        .fromWei(Convert.Unit.ETHER)
//}

