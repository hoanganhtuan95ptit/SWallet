package com.simple.wallet.presentation.adapters

import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.emptyImage
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.image.Image
import com.simple.coreapp.utils.extentions.setImage
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.wallet.databinding.ItemHeaderBinding

class HeaderAdapter : ViewItemAdapter<HeaderViewItem, ItemHeaderBinding>() {

    override fun bind(binding: ItemHeaderBinding, viewType: Int, position: Int, item: HeaderViewItem) {
        super.bind(binding, viewType, position, item)

        binding.ivHeader.setImage(item.logo)

        binding.tvHeaderTitle.setText(item.title)
        binding.tvHeaderCaption.setText(item.caption)

        if (item.captionBackground > 0) binding.tvHeaderCaption.setBackgroundResource(item.captionBackground)
    }
}

class HeaderViewItem(
    val id: String,

    var logo: Image = emptyImage(),

    var title: Text = emptyText(),

    var caption: Text = emptyText(),
    var captionBackground: Int = 0,
) : ViewItemCloneable {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )
}