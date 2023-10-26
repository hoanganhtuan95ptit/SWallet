package com.simple.wallet.presentation.adapters

import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.updatePadding
import com.simple.adapter.BaseBindingViewHolder
import com.simple.adapter.ViewItemAdapter
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.setDebouncedClickListener
import com.simple.coreapp.utils.extentions.setText
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.wallet.databinding.ItemKeyValueV3Binding

class KeyValueAdapter(
    private val onKeyItemClick: (KeyValueViewItemV3) -> Unit = {},
    private val onValueItemClick: (KeyValueViewItemV3) -> Unit = {},
) : ViewItemAdapter<KeyValueViewItemV3, ItemKeyValueV3Binding>() {

    override fun createViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder<ItemKeyValueV3Binding> {

        val viewHolder = super.createViewHolder(parent, viewType)

        val binding = viewHolder!!.binding

        binding.tvKey.setDebouncedClickListener {

            getViewItem(viewHolder.adapterPosition)?.let { onKeyItemClick.invoke(it) }
        }

        binding.tvValue.setDebouncedClickListener {

            getViewItem(viewHolder.adapterPosition)?.let { onValueItemClick.invoke(it) }
        }

        return viewHolder
    }

    override fun bind(binding: ItemKeyValueV3Binding, viewType: Int, position: Int, item: KeyValueViewItemV3, payloads: MutableList<Any>) {
        super.bind(binding, viewType, position, item, payloads)

        if (payloads.contains(PAYLOAD_VALUE)) {
            refreshValue(binding, item)
        }

        binding.root.doOnPreDraw { binding.tvValue.maxWidth = binding.tvValue.right - (binding.tvValue.height + binding.tvKey.right) }
    }

    override fun bind(binding: ItemKeyValueV3Binding, viewType: Int, position: Int, item: KeyValueViewItemV3) {
        super.bind(binding, viewType, position, item)

        refreshKey(binding, item)
        refreshValue(binding, item)

        binding.root.setBackgroundResource(item.background)

        binding.root.updatePadding(left = item.paddingLeft, right = item.paddingRight)

        binding.root.doOnPreDraw { binding.tvValue.maxWidth = binding.tvValue.right - (binding.tvValue.height + binding.tvKey.right) }
    }

    private fun refreshKey(binding: ItemKeyValueV3Binding, item: KeyValueViewItemV3) {

        binding.tvKey.setText(item.key)
    }

    private fun refreshValue(binding: ItemKeyValueV3Binding, item: KeyValueViewItemV3) {

        binding.tvValue.setText(item.value)
    }
}

data class KeyValueViewItemV3(
    val id: String = "",

    val key: Text = emptyText(),
    var value: Text = emptyText(),

    var background: Int = 0,

    var paddingLeft: Int = 0,
    var paddingRight: Int = 0
) : ViewItemCloneable {

    override fun clone() = copy()

    fun refresh() = apply {

    }

    override fun areItemsTheSame(): List<Any> = listOf(
        id
    )

    override fun getContentsCompare(): List<Pair<Any, String>> = listOf(
        value to PAYLOAD_VALUE
    )
}

private const val PAYLOAD_VALUE = "PAYLOAD_VALUE"