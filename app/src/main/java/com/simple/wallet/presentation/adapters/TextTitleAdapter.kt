package com.simple.wallet.presentation.adapters

import android.view.Gravity
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.wallet.databinding.ItemTextCaptionBinding

class TextCaptionAdapter : ViewItemAdapter<TextCaptionViewItem, ItemTextCaptionBinding>() {

    override fun bind(binding: ItemTextCaptionBinding, viewType: Int, position: Int, item: TextCaptionViewItem) {
        super.bind(binding, viewType, position, item)

        binding.tvTitle.setText(item.text)
        binding.tvTitle.gravity = item.textGravity
    }
}

data class TextCaptionViewItem(
    val id: String = "",

    var text: Text = emptyText(),

    val textGravity: Int = Gravity.CENTER_HORIZONTAL
) : ViewItemCloneable {

    fun refresh() = apply {
    }

    override fun areItemsTheSame(): List<Any> = listOf(
        KEY, id
    )
}

private const val KEY = "TITLE_VIEW_ITEM"