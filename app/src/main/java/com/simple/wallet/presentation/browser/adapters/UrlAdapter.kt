package com.simple.wallet.presentation.browser.adapters

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
import com.simple.coreapp.utils.extentions.toImage
import com.simple.coreapp.utils.extentions.toText
import com.simple.wallet.databinding.ItemUrlBinding
import com.simple.wallet.domain.entities.Url

class UrlAdapter(onItemClick: (View, UrlViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<UrlViewItem, ItemUrlBinding>(onItemClick) {

    override fun bind(binding: ItemUrlBinding, viewType: Int, position: Int, item: UrlViewItem) {
        super.bind(binding, viewType, position, item)

        binding.ivUrl.setImage(item.image)

        binding.tvUrlName.setText(item.name)
        binding.tvUrlCaption.setText(item.caption)

        binding.root.updatePadding(left = item.paddingLeft, right = item.paddingRight)
    }
}

data class UrlViewItem(
    val data: Url,

    var name: Text = emptyText(),
    var caption: Text = emptyText(),

    var image: Image = emptyImage(),

    var paddingLeft: Int = 0,
    var paddingRight: Int = 0
) : ViewItemCloneable {

    fun refresh() = apply {

        name = data.name.toText()
        caption = data.url.toText()

        image = data.image.toImage()
    }

    override fun areItemsTheSame(): List<Any> = listOf(
        data.url
    )
}