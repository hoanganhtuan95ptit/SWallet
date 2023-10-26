package com.simple.wallet.presentation.adapters

import android.graphics.Typeface
import android.text.style.StyleSpan
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.emptyImage
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.image.Image
import com.simple.coreapp.utils.extentions.setImage
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.coreapp.utils.extentions.text.TextImage
import com.simple.coreapp.utils.extentions.text.TextSpan
import com.simple.coreapp.utils.extentions.toImage
import com.simple.coreapp.utils.extentions.toPx
import com.simple.coreapp.utils.extentions.toText
import com.simple.wallet.R
import com.simple.wallet.UNLIMITED
import com.simple.wallet.databinding.ItemTransactionInfoTokenApproveBinding
import com.simple.wallet.domain.entities.extra.ApproveExtra
import com.simple.wallet.utils.exts.FormatNumberType
import com.simple.wallet.utils.exts.divideToPowerTen
import com.simple.wallet.utils.exts.toDisplay

class TokenApproveAdapter : ViewItemAdapter<TokenApproveViewItem, ItemTransactionInfoTokenApproveBinding>() {

    override fun bind(binding: ItemTransactionInfoTokenApproveBinding, viewType: Int, position: Int, item: TokenApproveViewItem) {
        super.bind(binding, viewType, position, item)

        binding.tvToken.setText(item.tokenName)
        binding.ivToken.setImage(item.tokenLogo)

        binding.tvAmountApprove.setText(item.amountApprove)
    }
}

class TokenApproveViewItem(
    val extra: ApproveExtra,

    var tokenName: Text = emptyText(),
    var tokenLogo: Image = emptyImage(),

    var amountApprove: Text = emptyText(),
) : ViewItemCloneable {

    fun refresh() = apply {

        tokenName = TextSpan(extra.tokenApprove.symbol.uppercase().toText(), StyleSpan(Typeface.BOLD))
        tokenLogo = extra.tokenApprove.logo.takeIf { it.isNotBlank() }?.toImage() ?: R.drawable.img_all_network.toImage()

        amountApprove = if (extra.amountApprove == UNLIMITED) {

            listOf(
                R.string.unlimited.toText(),
                TextImage(R.drawable.img_edit_accent_24dp, 18.toPx())
            ).toText(" ").let {

                TextSpan(it, StyleSpan(Typeface.BOLD))
            }
        } else if (extra.tokenApprove.balance == extra.amountApprove) {

            emptyText()
        } else {

            listOf(
                extra.amountApprove.toBigDecimal().divideToPowerTen(extra.tokenApprove.decimals).toDisplay(FormatNumberType.BALANCE),
                TextImage(R.drawable.img_edit_accent_24dp, 18.toPx())
            ).toText(" ").let {

                TextSpan(it, StyleSpan(Typeface.BOLD))
            }
        }
    }

    override fun areItemsTheSame(): List<Any> = listOf(
        key
    )
}

private val key by lazy {
    "TOKEN_APPROVE_TRANSACTION_INFO_VIEW_ITEM"
}

