package com.simple.wallet.presentation.home.adapters

import android.view.View
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
import com.simple.wallet.R
import com.simple.wallet.databinding.ItemCategoryBinding
import com.simple.wallet.domain.entities.Category
import java.util.UUID

class CategoryAdapter(onItemClick: (View, CategoryViewItem) -> Unit = { _, _ -> }) : ViewItemAdapter<CategoryViewItem, ItemCategoryBinding>(onItemClick) {

    override fun bind(binding: ItemCategoryBinding, viewType: Int, position: Int, item: CategoryViewItem, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

    }

    override fun bind(binding: ItemCategoryBinding, viewType: Int, position: Int, item: CategoryViewItem) {
        super.bind(binding, viewType, position, item)

        binding.ivCategory.setImage(item.image)

        binding.tvCategory.setText(item.title, true)
    }
}

data class CategoryViewItem(
    val data: Category,

    var title: Text = emptyText(),
    var image: Image = emptyImage()
) : ViewItemCloneable {

    fun refresh() = apply {

        title = when (data.id) {
            Category.Id.SWAP.value -> R.string.title_swap.toText()
            Category.Id.D_APP.value -> R.string.title_d_app.toText()
            Category.Id.TRANSFER.value -> R.string.title_transfer.toText()
            Category.Id.CROSS_SWAP.value -> R.string.title_bridge.toText()
            else -> R.string.title_swap.toText()
        }

        image = when (data.id) {
            Category.Id.SWAP.value -> R.drawable.ic_swap_on_surface_24dp.toImage()
            Category.Id.D_APP.value -> R.drawable.ic_dapp_on_surface_24dp.toImage()
            Category.Id.TRANSFER.value -> R.drawable.ic_transfer_on_surface_24dp.toImage()
            Category.Id.CROSS_SWAP.value -> R.drawable.ic_cross_swap_on_surface_24dp.toImage()
            else -> R.drawable.ic_swap_on_surface_24dp.toImage()
        }
    }

    override fun areItemsTheSame(): List<Any> = listOf(
        data.id, UUID.randomUUID().toString()
    )
}