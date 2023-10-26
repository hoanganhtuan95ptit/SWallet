package com.simple.wallet.presentation.adapters

import android.view.View
import android.view.ViewGroup
import com.simple.adapter.BaseBindingViewHolder
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.core.utils.extentions.asObject
import com.simple.coreapp.utils.extentions.emptyImage
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.image.Image
import com.simple.coreapp.utils.extentions.setDebouncedClickListener
import com.simple.coreapp.utils.extentions.setImage
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.coreapp.utils.extentions.text.TextImage
import com.simple.coreapp.utils.extentions.toText
import com.simple.wallet.DP_12
import com.simple.wallet.DP_24
import com.simple.wallet.R
import com.simple.wallet.databinding.ItemBottomBinding
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.utils.exts.imageDisplay
import com.simple.wallet.utils.exts.nameDisplay
import com.simple.wallet.utils.exts.shortenValue
import com.simple.wallet.utils.exts.toDrawable

class BottomAdapter(
    private val onChainClicked: (View, BottomViewItem) -> Unit = { _, _ -> },
    private val onWalletClicked: (View, BottomViewItem) -> Unit = { _, _ -> },
) : ViewItemAdapter<BottomViewItem, ItemBottomBinding>() {

    override fun createViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder<ItemBottomBinding>? {

        val holder = super.createViewHolder(parent, viewType)

        val binding = holder!!.binding

        binding.vChain.setDebouncedClickListener {

            onChainClicked(it, getViewItem(holder.bindingAdapterPosition).asObject())
        }

        binding.vWallet.setDebouncedClickListener {

            onWalletClicked(it, getViewItem(holder.bindingAdapterPosition).asObject())
        }

        binding.root.post {

            binding.tvChainName.maxWidth = (binding.root.width * 1.3 / 3 - binding.ivChain.width).toInt()
        }

        return holder
    }

    override fun bind(binding: ItemBottomBinding, viewType: Int, position: Int, item: BottomViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(PAYLOAD_WALLET_NAME)) refreshWalletName(binding, item)
        if (payloads.contains(PAYLOAD_WALLET_IMAGE)) refreshWalletImage(binding, item)
        if (payloads.contains(PAYLOAD_WALLET_ADDRESS)) refreshWalletAddress(binding, item)

        if (payloads.contains(PAYLOAD_CHAIN_NAME)) refreshChainName(binding, item)
        if (payloads.contains(PAYLOAD_CHAIN_IMAGE)) refreshChainImage(binding, item)
        if (payloads.contains(PAYLOAD_CHAIN_TITLE)) refreshChainTitle(binding, item)
    }

    override fun bind(binding: ItemBottomBinding, viewType: Int, position: Int, item: BottomViewItem) {
        super.bind(binding, viewType, position, item)

        refreshWalletName(binding, item)
        refreshWalletImage(binding, item)
        refreshWalletAddress(binding, item)

        refreshChainName(binding, item)
        refreshChainImage(binding, item)
        refreshChainTitle(binding, item)
    }

    private fun refreshWalletName(binding: ItemBottomBinding, item: BottomViewItem) {

        binding.tvWalletName.setText(item.walletName)
    }

    private fun refreshWalletImage(binding: ItemBottomBinding, item: BottomViewItem) {

        binding.ivWallet.setImage(item.walletImage)
    }

    private fun refreshWalletAddress(binding: ItemBottomBinding, item: BottomViewItem) {

        binding.tvWalletAddress.setText(item.walletAddress)
    }

    private fun refreshChainName(binding: ItemBottomBinding, item: BottomViewItem) {

        binding.tvChainName.setText(item.chainName)
    }

    private fun refreshChainImage(binding: ItemBottomBinding, item: BottomViewItem) {

        binding.ivChain.setImage(item.chainImage)
    }

    private fun refreshChainTitle(binding: ItemBottomBinding, item: BottomViewItem) {

        binding.tvChainTitle.setText(item.chainTitle)
    }
}

class BottomViewItem(
    val chain: Chain,
    val wallet: Wallet,

    var walletName: Text = emptyText(),
    var walletImage: Image = emptyImage(),
    var walletAddress: Text = emptyText(),

    var chainName: Text = emptyText(),
    var chainTitle: Text = emptyText(),
    var chainImage: Image = emptyImage()
) : ViewItemCloneable {

    fun refresh(supportSelectWallet: Boolean = false) = apply {

        val address = wallet.addressMap.filterValues { it == chain.type }.toList().first().first

        walletName = wallet.name.toText()
        walletImage = address.toDrawable(DP_24)

        walletAddress = if (supportSelectWallet) listOf(

            address.shortenValue().toText(),
            TextImage(R.drawable.img_down_on_background_24dp, DP_12)
        ).run {

            toText("  ")
        } else {

            address.shortenValue().toText()
        }

        chainName = chain.nameDisplay
        chainImage = chain.imageDisplay
        chainTitle = R.string.all_network.toText()
    }

    override fun areItemsTheSame(): List<Any> = listOf(
        key
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        walletName to PAYLOAD_WALLET_NAME,
        walletImage to PAYLOAD_WALLET_IMAGE,
        walletAddress to PAYLOAD_WALLET_ADDRESS,

        chainName to PAYLOAD_CHAIN_NAME,
        chainImage to PAYLOAD_CHAIN_IMAGE,
        chainTitle to PAYLOAD_CHAIN_TITLE,
    )
}

private const val PAYLOAD_WALLET_NAME = "PAYLOAD_WALLET_NAME"
private const val PAYLOAD_WALLET_IMAGE = "PAYLOAD_WALLET_NAME"
private const val PAYLOAD_WALLET_ADDRESS = "PAYLOAD_WALLET_NAME"

private const val PAYLOAD_CHAIN_NAME = "PAYLOAD_WALLET_NAME"
private const val PAYLOAD_CHAIN_IMAGE = "PAYLOAD_WALLET_NAME"
private const val PAYLOAD_CHAIN_TITLE = "PAYLOAD_WALLET_NAME"

private val key by lazy { "BOTTOM_TRANSACTION_INFO_VIEW_ITEM" }