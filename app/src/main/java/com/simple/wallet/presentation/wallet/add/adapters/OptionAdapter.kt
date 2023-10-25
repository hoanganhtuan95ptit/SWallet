package com.simple.wallet.presentation.wallet.add.adapters

import android.view.View
import androidx.core.view.updatePadding
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.emptyImage
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.image.Image
import com.simple.coreapp.utils.extentions.setImage
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.wallet.databinding.ItemOptionBinding

class OptionAdapter(onItemClick: (View, OptionViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<OptionViewItem, ItemOptionBinding>(onItemClick) {

    override fun bind(binding: ItemOptionBinding, viewType: Int, position: Int, item: OptionViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)
    }

    override fun bind(binding: ItemOptionBinding, viewType: Int, position: Int, item: OptionViewItem) {
        super.bind(binding, viewType, position, item)

        binding.ivOption.setImage(item.image)

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

data class OptionViewItem(
    val id: String,

    var image: Image = emptyImage(),


    var title: Text = emptyText(),
    var caption: Text = emptyText(),

    var background: Int? = null,

    var paddingVertical: Int = 0,
    var paddingHorizontal: Int = 0
) : ViewItemCloneable {

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )
}