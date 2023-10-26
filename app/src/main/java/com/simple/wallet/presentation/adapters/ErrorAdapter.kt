package com.simple.wallet.presentation.adapters

import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.wallet.databinding.ItemErrorBinding

class ErrorAdapter : ViewItemAdapter<ErrorHeaderViewItem, ItemErrorBinding>() {

    override fun bind(binding: ItemErrorBinding, viewType: Int, position: Int, item: ErrorHeaderViewItem) {
        super.bind(binding, viewType, position, item)

        binding.lottieAnimationView.setAnimation(item.logo)

        binding.tvTitle.setText(item.title)
        binding.tvCaption.setText(item.caption)
    }
}

class ErrorHeaderViewItem(
    val id: String = "",

    var logo: Int = 0,

    var title: Text = emptyText(),

    var caption: Text = emptyText(),
) : ViewItemCloneable {

    fun refresh() = apply {

    }

    override fun areItemsTheSame(): List<Any> = listOf(
        key, id
    )
}

private val key by lazy {
    "ERROR_HEADER_VIEW_ITEM"
}