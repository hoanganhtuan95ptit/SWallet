package com.simple.wallet.presentation.chain.adapters

import android.view.View
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.emptyImage
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.image.Image
import com.simple.coreapp.utils.extentions.setImage
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.wallet.databinding.ItemChainBinding
import com.simple.wallet.domain.entities.Chain
import com.simple.wallet.utils.exts.imageDisplay
import com.simple.wallet.utils.exts.nameDisplay

internal class SelectChainAdapter(onItemClick: (View, SelectChainViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<SelectChainViewItem, ItemChainBinding>(onItemClick) {

    override fun bind(binding: ItemChainBinding, viewType: Int, position: Int, item: SelectChainViewItem) {
        super.bind(binding, viewType, position, item)

        binding.ivChain.setImage(item.image)

        binding.tvChainName.setText(item.name)
    }
}

class SelectChainViewItem(
    val data: Chain,

    var name: Text = emptyText(),

    var image: Image = emptyImage(),

    var isSelected: Boolean = false
) : ViewItemCloneable {

    fun refresh(chainIdSelected: Long) = apply {

        isSelected = data.id == chainIdSelected


        name = data.nameDisplay

        image = data.imageDisplay
    }

    override fun areItemsTheSame(): List<Any> = listOf(
        data.id
    )
}