package com.simple.wallet.data.task

import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.toImage
import com.simple.coreapp.utils.extentions.toText
import com.simple.coreapp.utils.extentions.withTextColor
import com.simple.task.LowException
import com.simple.wallet.R
import com.simple.wallet.domain.tasks.CameraInfoTask
import com.simple.wallet.presentation.adapters.MessageViewItem


class CameraInfoScanTask : CameraInfoTask {

    override suspend fun executeTask(param: String): List<ViewItemCloneable> {

        if (!param.equals("scan", true)) {

            throw LowException("")
        }

        val list = arrayListOf<ViewItemCloneable>()

        MessageViewItem(
            id = "123",
            message = R.string.scan_info.toText().withTextColor(com.google.android.material.R.attr.colorAccent),
            messageIcon = R.drawable.ic_info_primary_24dp.toImage(),
            background = R.drawable.bg_corners_16dp_solid_accent_10
        ).let {

            list.add(it)
        }

        return list
    }
}