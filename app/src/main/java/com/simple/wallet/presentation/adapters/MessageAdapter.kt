package com.simple.wallet.presentation.adapters

import android.view.View
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.emptyImage
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.getColorFromAttr
import com.simple.coreapp.utils.extentions.image.Image
import com.simple.coreapp.utils.extentions.setImage
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.wallet.databinding.ItemMessageBinding

class MessageAdapter(val onItemClick: (View, MessageViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<MessageViewItem, ItemMessageBinding>() {

    override fun bind(binding: ItemMessageBinding, viewType: Int, position: Int, item: MessageViewItem) {
        super.bind(binding, viewType, position, item)

        binding.tvMessage.setText(item.message)

        binding.ivMessage.setImage(item.messageIcon)

        if (item.messageAttrColor > 0) binding.tvMessage.setTextColor(binding.root.context.getColorFromAttr(item.messageAttrColor))
        if (item.messageBackground > 0) binding.root.setBackgroundResource(item.messageBackground)
    }
}

class MessageViewItem(
    val id: String = "",

    var message: Text = emptyText(),
    var messageIcon: Image = emptyImage(),
    var messageAttrColor: Int = 0,
    var messageBackground: Int = 0,
) : ViewItemCloneable {

    override fun areItemsTheSame(): List<Any> = listOf(
        keyViewItem, id
    )
}

private val keyViewItem by lazy {
    "FEE_TRANSACTION_INFO_VIEW_ITEM"
}