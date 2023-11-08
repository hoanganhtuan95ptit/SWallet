@file:Suppress("IfThenToSafeAccess")

package com.simple.wallet.presentation.home.asset.token.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.emptyImage
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.getColorFromAttr
import com.simple.coreapp.utils.extentions.image.Image
import com.simple.coreapp.utils.extentions.setImage
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.setVisible
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.coreapp.utils.extentions.toImage
import com.simple.coreapp.utils.extentions.toPx
import com.simple.coreapp.utils.extentions.toText
import com.simple.coreapp.utils.extentions.withTextColor
import com.simple.wallet.R
import com.simple.wallet.databinding.ItemTokenAssetBinding
import com.simple.wallet.domain.entities.Currency
import com.simple.wallet.domain.entities.Token
import com.simple.wallet.utils.exts.FormatNumberType
import com.simple.wallet.utils.exts.decimal
import com.simple.wallet.utils.exts.format
import com.simple.wallet.utils.exts.toDisplay
import java.math.BigDecimal

internal class TokenAssetAdapter(
    onItemClick: (View, TokenAssetViewItem) -> Unit = { _, _ -> }
) : ViewItemAdapter<TokenAssetViewItem, ItemTokenAssetBinding>(onItemClick) {

    private var colorUp: Int? = null
    private var colorDown: Int? = null
    private var colorNone: Int? = null

    private val round = 4.toPx()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        colorUp = colorUp ?: recyclerView.context.getColorFromAttr(R.attr.colorUp)
        colorDown = colorDown ?: recyclerView.context.getColorFromAttr(R.attr.colorDown)
        colorNone = colorNone ?: recyclerView.context.getColorFromAttr(com.simple.coreapp.R.attr.colorSurfaceVariant)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        colorUp = null
        colorDown = null
        colorNone = null
    }

    override fun bind(binding: ItemTokenAssetBinding, viewType: Int, position: Int, item: TokenAssetViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(PAYLOAD_TAG)) {
            refreshTag(binding, item)
        }

        if (payloads.contains(PAYLOAD_NAME)) {
            refreshName(binding, item)
        }

        if (payloads.contains(PAYLOAD_IMAGE)) {
            refreshImage(binding, item)
        }

        if (payloads.contains(PAYLOAD_CHAIN)) {
            refreshChain(binding, item)
        }

        if (payloads.contains(PAYLOAD_BALANCE)) {
            refreshBalance(binding, item)
        }

        if (payloads.contains(PAYLOAD_VALUE)) {
            refreshValue(binding, item)
        }

        if (payloads.contains(PAYLOAD_BALANCE_BY_CURRENCY)) {
            refreshPrice(binding, item)
        }

        if (payloads.contains(PAYLOAD_CHAIN_VISIBLE)) {
            refreshChainVisibility(binding, item)
        }

//        binding.root.doOnPreDraw { binding.tvToken.maxWidth = binding.tvBalanceCurrency.left - binding.tvToken.left }
    }

    override fun bind(binding: ItemTokenAssetBinding, viewType: Int, position: Int, item: TokenAssetViewItem) {
        super.bind(binding, viewType, position, item)

        refreshTag(binding, item)
        refreshName(binding, item)
        refreshImage(binding, item)
        refreshValue(binding, item)
        refreshChain(binding, item)
        refreshBalance(binding, item)
        refreshChainVisibility(binding, item)
        refreshPrice(binding, item)

//        binding.root.doOnPreDraw { binding.tvToken.maxWidth = binding.tvBalanceCurrency.left - binding.tvToken.left }
    }

    private fun refreshTag(binding: ItemTokenAssetBinding, item: TokenAssetViewItem) {

//        binding.ivTag.setImage(item.tag)
    }

    private fun refreshName(binding: ItemTokenAssetBinding, item: TokenAssetViewItem) {

        binding.tvToken.setText(item.name)
    }

    private fun refreshImage(binding: ItemTokenAssetBinding, item: TokenAssetViewItem) {

        binding.ivToken.setImage(item.image, CircleCrop())
    }

    private fun refreshChain(binding: ItemTokenAssetBinding, item: TokenAssetViewItem) {

        binding.ivChain.setVisible(item.chainImage != emptyImage())
        binding.ivChain.setImage(item.chainImage, RoundedCorners(round))
    }

    private fun refreshChainVisibility(binding: ItemTokenAssetBinding, item: TokenAssetViewItem) {

//        binding.ivChain.setVisible(item.chainVisible)
    }

    private fun refreshBalance(binding: ItemTokenAssetBinding, item: TokenAssetViewItem) {

        binding.tvBalance.setText(item.balance)
    }

    private fun refreshValue(binding: ItemTokenAssetBinding, item: TokenAssetViewItem) {

        binding.tvValue.setText(item.value)
    }

    private fun refreshPrice(binding: ItemTokenAssetBinding, item: TokenAssetViewItem) {

        binding.tvPrice.setText(item.price)
    }
}

internal data class TokenAssetViewItem(
    val data: Token,

    var tag: Image = emptyImage(),
    var name: Text = emptyText(),
    var image: Image = emptyImage(),

    var price: Text = emptyText(),
    var value: Text = emptyText(),
    var balance: Text = emptyText(),

    var chainImage: Image = emptyImage(),
) : ViewItemCloneable {

    var balanceByUsd: BigDecimal = BigDecimal.ZERO

    fun refresh(currency: Currency) = apply {

        this.tag = when (data.tag) {
            Token.Tag.SCAM -> imgScam
            Token.Tag.VERIFIED -> imgVersified
            Token.Tag.PROMOTION -> imgPromotion
            else -> imgUnknown
        }

        this.name = data.symbol.uppercase().toText()

        this.image = data.logo.toImage()


        val price = data.price?.price

        val priceChange = data.price?.priceChange?.get(Token.Price.Change.CHANGE_1H)

        val priceChangeDisplay = if (priceChange != null && priceChange > BigDecimal.ZERO) {

            listOf(textUp, priceChange.toDisplay(FormatNumberType.PERCENTAGE), textPercent).toText("").withTextColor(R.attr.colorUp)
        } else if (priceChange != null && priceChange < BigDecimal.ZERO) {

            listOf(priceChange.toDisplay(FormatNumberType.PERCENTAGE), textPercent).toText("").withTextColor(R.attr.colorDown)
        } else if (priceChange != null) {

            priceChange.toDisplay(FormatNumberType.PERCENTAGE).withTextColor(com.simple.coreapp.R.attr.colorSurfaceVariant)
        } else {

            null
        }

        this.price = if (price != null && price > BigDecimal.ZERO && priceChangeDisplay != null) {

            listOf(price.toText(), priceChangeDisplay).toText(" ")
        } else if (price != null && price > BigDecimal.ZERO) {

            price.toText()
        } else {

            textDefault
        }


        val balance = data.balance.toBigDecimal()

        val balanceNumber = balance.decimal(data.decimals)

        this.balance = balanceNumber.toDisplay(FormatNumberType.BALANCE)

        this.balanceByUsd = balanceNumber.multiply(price)

        this.value = if (price != null && price > BigDecimal.ZERO) {

            this.balanceByUsd.toDisplay(FormatNumberType.VALUE_2).format(currency)
        } else {

            textDefault
        }


        this.chainImage = data.chain?.image?.toImage() ?: emptyImage()
//        chainVisible = chainIdAndIcon.size > 1
    }

    override fun areItemsTheSame(): List<Any> = listOf(
        data.chainId, data.address
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        tag to PAYLOAD_TAG,
        name to PAYLOAD_NAME,
        image to PAYLOAD_IMAGE,

        value to PAYLOAD_VALUE,
        price to PAYLOAD_BALANCE_BY_CURRENCY,
        balance to PAYLOAD_BALANCE,

        chainImage to PAYLOAD_CHAIN,
    )

    internal enum class Price24hState {
        UP, DOWN, NONE
    }


}

private val textUp by lazy {
    "+".toText()
}

private val textDown by lazy {
    "-".toText()
}

private val textPercent by lazy {
    "%".toText()
}

private val textDefault by lazy {
    "-".toText()
}

private val imgScam by lazy {
    R.drawable.ic_token_tag_scam_24dp.toImage()
}

private val imgVersified by lazy {
    R.drawable.ic_token_tag_verified_24dp.toImage()
}

private val imgPromotion by lazy {
    R.drawable.ic_token_tag_promotion_24dp.toImage()
}

private val imgUnknown by lazy {
    R.drawable.ic_token_tag_unknown_24dp.toImage()
}

private const val PAYLOAD_TAG = "PAYLOAD_TAG"
private const val PAYLOAD_NAME = "PAYLOAD_NAME"
private const val PAYLOAD_IMAGE = "PAYLOAD_IMAGE"

private const val PAYLOAD_VALUE = "PAYLOAD_PRICE"

private const val PAYLOAD_CHAIN = "PAYLOAD_CHAIN"
private const val PAYLOAD_CHAIN_VISIBLE = "PAYLOAD_CHAIN_VISIBLE"

private const val PAYLOAD_BALANCE = "PAYLOAD_NUMBER"
private const val PAYLOAD_BALANCE_BY_CURRENCY = "PAYLOAD_PRICE_COLOR"
