package com.simple.wallet.presentation.wallet.select.adapters

import android.view.View
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.emptyImage
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.image.Image
import com.simple.coreapp.utils.extentions.setImage
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.wallet.databinding.ItemWalletBinding
import com.simple.wallet.domain.entities.Wallet
import com.simple.wallet.utils.exts.descriptionDisplay
import com.simple.wallet.utils.exts.imageDisplay
import com.simple.wallet.utils.exts.nameDisplay

internal class SelectWalletAdapter(onItemClick: (View, SelectWalletViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<SelectWalletViewItem, ItemWalletBinding>(onItemClick) {

    override fun bind(binding: ItemWalletBinding, viewType: Int, position: Int, item: SelectWalletViewItem) {
        super.bind(binding, viewType, position, item)

        binding.ivWallet.setImage(item.image)

        binding.tvWalletName.setText(item.name)
        binding.tvWalletType.setText(item.type)
    }
}

class SelectWalletViewItem(
    val data: Wallet,

    var name: Text = emptyText(),
    var type: Text = emptyText(),

    var image: Image = emptyImage(),

    var isSelected: Boolean = false
) : ViewItemCloneable {

    fun refresh(walletIdSelected: String) = apply {

        isSelected = data.id.equals(walletIdSelected, true)

        name = data.nameDisplay

        type = data.descriptionDisplay


        image = data.imageDisplay
    }

    override fun areItemsTheSame(): List<Any> = listOf(
        data.id ?: ""
    )
}