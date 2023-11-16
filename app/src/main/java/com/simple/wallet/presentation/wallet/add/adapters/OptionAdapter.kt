package com.simple.wallet.presentation.wallet.add.adapters

import android.view.View
import androidx.compose.runtime.Immutable
import androidx.core.view.updatePadding
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.wallet.databinding.ItemOptionBinding

class OptionAdapter(onItemClick: (View, OptionViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<OptionViewItem, ItemOptionBinding>(onItemClick) {

    override fun bind(binding: ItemOptionBinding, viewType: Int, position: Int, item: OptionViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)
    }

    override fun bind(binding: ItemOptionBinding, viewType: Int, position: Int, item: OptionViewItem) {
        super.bind(binding, viewType, position, item)

//        binding.ivOption.setImage(item.image)

        binding.tvOptionTitle.setText(item.title)
        binding.tvOptionCaption.setText(item.caption)


        val background = item.background

        if (background != null) {

            binding.root.setBackgroundResource(background)
        }


        val paddingVertical = item.paddingVertical
        val paddingHorizontal = item.paddingHorizontal

        binding.root.updatePadding(left = paddingHorizontal, top = paddingVertical, right = paddingHorizontal, bottom = paddingVertical)
    }
}

@Immutable
data class OptionViewItem(
    val id: String,

    var image: Int = -1,


    var title: Int = -1,
    var caption: Int = -1,

    var background: Int? = null,

    var paddingVertical: Int = 0,
    var paddingHorizontal: Int = 0
) : ViewItemCloneable {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )
}