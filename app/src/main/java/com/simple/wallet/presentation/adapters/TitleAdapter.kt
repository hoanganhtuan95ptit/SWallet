package com.simple.wallet.presentation.adapters

import android.view.Gravity
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.wallet.databinding.ItemTitleCenterBinding

class TitleAdapter : ViewItemAdapter<TitleViewItem, ItemTitleCenterBinding>() {

    override fun bind(binding: ItemTitleCenterBinding, viewType: Int, position: Int, item: TitleViewItem) {
        super.bind(binding, viewType, position, item)

        binding.tvTitle.setText(item.name)
        binding.tvTitle.gravity = item.textGravity
    }
}

data class TitleViewItem(
    val id: String = "",

    var name: Text = emptyText(),

    val textGravity: Int = Gravity.CENTER_HORIZONTAL
) : ViewItemCloneable {

    fun refresh() = apply {
    }

    override fun areItemsTheSame(): List<Any> = listOf(
        KEY, id
    )
}

private const val KEY = "TITLE_VIEW_ITEM"