package com.simple.wallet.utils.exts

import android.graphics.Typeface
import android.text.style.StyleSpan
import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.text.TextSpan
import com.simple.coreapp.utils.extentions.toPx
import com.simple.coreapp.utils.extentions.toText
import com.simple.wallet.presentation.adapters.KeyValueViewItemV3
import java.util.UUID


fun Map<*, *>.toViewItem(level: Int = 0): List<ViewItemCloneable> {

    val leftMargin = level * 16.toPx()


    val list = arrayListOf<ViewItemCloneable>()


    if (level == 4) {

        return list
    }


    for (mutableEntry in this) {

        val key = mutableEntry.key.toString().uppercaseFirst() + " :"

        val value = mutableEntry.value

        when (value) {
            is Int -> {

                value.toString()
            }

            is Long -> {

                value.toString()
            }

            is String -> {

                value
            }

            else -> {

                ""
            }
        }.let {

            list.add(KeyValueViewItemV3(UUID.randomUUID().toString(), TextSpan(key.toText(), StyleSpan(Typeface.BOLD)), it.toText(), paddingLeft = leftMargin).refresh())
        }


        val map: Map<*, *> = when (value) {

            is Map<*, *> -> {

                value
            }

            is List<*> -> {

                value.toMap()
            }

            else -> {

                emptyMap<Any, Any>()
            }
        }

        list.addAll(map.toViewItem(level + 1))
    }

    return list
}

private fun List<*>.toMap(): Map<*, *> {

    val map: HashMap<Int, Any> = hashMapOf()

    forEachIndexed { index, any ->

        map[index] = any ?: return@forEachIndexed
    }

    return map
}