package com.simple.wallet.presentation.transaction.send.adapter

import android.view.View
import android.view.ViewGroup
import com.one.web3.utils.fromWei
import com.simple.adapter.BaseBindingViewHolder
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.core.utils.extentions.asObjectOrNull
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.setDebouncedClickListener
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.coreapp.utils.extentions.text.TextImage
import com.simple.coreapp.utils.extentions.text.TextRes
import com.simple.coreapp.utils.extentions.text.TextStr
import com.simple.coreapp.utils.extentions.toPx
import com.simple.coreapp.utils.extentions.toText
import com.simple.coreapp.utils.extentions.withTextColor
import com.simple.wallet.DP_16
import com.simple.wallet.R
import com.simple.wallet.databinding.ItemTransactionInfoFeeBinding
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Gas
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.utils.exts.FormatNumberType
import com.simple.wallet.utils.exts.getSettingOption
import com.simple.wallet.utils.exts.toDisplay
import org.web3j.utils.Convert
import java.math.BigDecimal
import java.math.BigInteger

class FeeTransactionInfoAdapter(val onItemClick: (View, FeeTransactionInfoViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<FeeTransactionInfoViewItem, ItemTransactionInfoFeeBinding>() {

    override fun createViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder<ItemTransactionInfoFeeBinding> {

        val viewHolder = super.createViewHolder(parent, viewType)!!

        val binding = viewHolder.binding

        binding.tvFeeValue.setDebouncedClickListener {

            val item = getViewItem(viewHolder.bindingAdapterPosition).asObjectOrNull<FeeTransactionInfoViewItem>() ?: return@setDebouncedClickListener

            onItemClick(binding.root, item)
        }

        return viewHolder
    }

    override fun bind(binding: ItemTransactionInfoFeeBinding, viewType: Int, position: Int, item: FeeTransactionInfoViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(PARAM_TITLE)) refreshTitle(binding, item)
        if (payloads.contains(PARAM_FEE_VALUE)) refreshFeeValue(binding, item)
        if (payloads.contains(PARAM_FEE_DETAIL)) refreshFeeDetail(binding, item)
    }

    override fun bind(binding: ItemTransactionInfoFeeBinding, viewType: Int, position: Int, item: FeeTransactionInfoViewItem) {
        super.bind(binding, viewType, position, item)

        refreshTitle(binding, item)
        refreshFeeValue(binding, item)
        refreshFeeDetail(binding, item)
    }

    private fun refreshTitle(binding: ItemTransactionInfoFeeBinding, item: FeeTransactionInfoViewItem) {

        binding.tvTitle.setText(item.title)
    }

    private fun refreshFeeValue(binding: ItemTransactionInfoFeeBinding, item: FeeTransactionInfoViewItem) {

        binding.tvFeeValue.setText(item.feeValue)
    }

    private fun refreshFeeDetail(binding: ItemTransactionInfoFeeBinding, item: FeeTransactionInfoViewItem) {

        binding.tvFeeDetail.setText(item.feeDetail)
    }
}

class FeeTransactionInfoViewItem(
    var title: Text = emptyText(),

    var feeValue: Text = emptyText(),

    var feeDetail: Text = emptyText(),
) : ViewItemCloneable {

    fun refresh(gas: Gas, gasLimit: BigInteger, nativeToken: Token, currentChain: Chain, transactionFee: BigDecimal) = apply {

        val gasFee = gas.gasPriceWei.multiply(gasLimit.toBigDecimal())
            .plus(transactionFee)
            .fromWei(Convert.Unit.ETHER)

        title = listOf(
            R.string.message_max_gas_fee.toText(),
            TextImage(R.drawable.ic_question_on_surface_variant_14dp, DP_16)
        ).toText()

        feeValue = listOf(
            "≈ ".toText(),
            gasFee.toDisplay(FormatNumberType.GAS_FEE),
            nativeToken.symbol.toText(),
        ).toText(" ").let {

            listOf(
                TextRes(R.string.max_gas_detail, it, gas.getSettingOption()),
                TextImage(R.drawable.img_edit_accent_24dp, 12.toPx())
            )
        }.toText(" ")


        val feeValueCurrency = TextRes(R.string.transaction_estimate_formula, gasFee.multiply(nativeToken.price!!.price).toDisplay(FormatNumberType.GAS_FEE)).withTextColor(com.google.android.material.R.attr.colorOnBackground)

        val feeValueDetail = if (transactionFee <= BigDecimal.ZERO && currentChain.type == Chain.Type.EVM) TextRes(
            R.string.gas_formula,
            gas.gasPriceWei.fromWei().toDisplay(FormatNumberType.BALANCE),
            TextRes(if (gas.priorityFeeWei > BigDecimal.ZERO) R.string.formula_max_gas else R.string.formula_gas_price),
            TextStr(gasLimit.toBigDecimal().toString()),
            TextRes(R.string.formula_gas_limit)
        ) else {

            emptyText()
        }

        feeDetail = listOf(
            feeValueCurrency,
            feeValueDetail
        ).toText()
    }

    override fun areItemsTheSame(): List<Any> = listOf(
        FEE_TRANSACTION_INFO_VIEW_ITEM
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        title to PARAM_TITLE,
        feeValue to PARAM_FEE_VALUE,
        feeDetail to PARAM_FEE_DETAIL
    )
}

private const val PARAM_TITLE = "PARAM_TITLE"
private const val PARAM_FEE_VALUE = "PARAM_FEE_VALUE"
private const val PARAM_FEE_DETAIL = "PARAM_FEE_DETAIL"
private const val FEE_TRANSACTION_INFO_VIEW_ITEM = "FEE_TRANSACTION_INFO_VIEW_ITEM"