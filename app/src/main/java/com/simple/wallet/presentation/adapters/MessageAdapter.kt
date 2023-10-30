package com.simple.wallet.presentation.adapters

import android.view.View
import android.view.ViewGroup
import com.simple.adapter.BaseBindingViewHolder
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.emptyImage
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.image.Image
import com.simple.coreapp.utils.extentions.setDebouncedClickListener
import com.simple.coreapp.utils.extentions.setImage
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.wallet.databinding.ItemMessageBinding

class MessageAdapter(
    private val onItemClick: (View, MessageViewItem) -> Unit = { _, _ -> }
) : ViewItemAdapter<MessageViewItem, ItemMessageBinding>() {

    override fun createViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder<ItemMessageBinding> {

        val viewHolder = super.createViewHolder(parent, viewType)

        val binding = viewHolder!!.binding

        binding.ivMessage.setDebouncedClickListener { view ->

            getViewItem(viewHolder.bindingAdapterPosition)?.let { onItemClick.invoke(view, it) }
        }

        return viewHolder
    }

    override fun bind(binding: ItemMessageBinding, viewType: Int, position: Int, item: MessageViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(MESSAGE_ICON)) refreshMessageIcon(binding, item)
    }

    override fun bind(binding: ItemMessageBinding, viewType: Int, position: Int, item: MessageViewItem) {
        super.bind(binding, viewType, position, item)

        binding.tvMessage.setText(item.message)

        refreshMessageIcon(binding, item)

        if (item.background > 0) binding.root.setBackgroundResource(item.background)
    }

    private fun refreshMessageIcon(binding: ItemMessageBinding, item: MessageViewItem) {

        binding.ivMessage.setImage(item.messageIcon)
    }
}

class MessageViewItem(
    val id: String = "",

    var message: Text = emptyText(),
    var messageIcon: Image = emptyImage(),

    var background: Int = 0,

    var needConfirm: Boolean = false
) : ViewItemCloneable {

    override fun areItemsTheSame(): List<Any> = listOf(
        keyViewItem, id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        messageIcon to MESSAGE_ICON
    )
}

private val MESSAGE by lazy { "message" }
private val MESSAGE_ICON by lazy { "messageIcon" }

private val keyViewItem by lazy { "FEE_TRANSACTION_INFO_VIEW_ITEM" }