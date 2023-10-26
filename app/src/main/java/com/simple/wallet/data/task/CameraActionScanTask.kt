package com.simple.wallet.data.task

import com.simple.adapter.ViewItemCloneable
import com.simple.coreapp.utils.extentions.toImage
import com.simple.coreapp.utils.extentions.toText
import com.simple.coreapp.utils.extentions.withTextColor
import com.simple.task.LowException
import com.simple.wallet.R
import com.simple.wallet.domain.tasks.CameraActionTask
import com.simple.wallet.presentation.adapters.MessageViewItem


class CameraActionScanTask : CameraActionTask {

    override suspend fun executeTask(param: CameraActionTask.Param): List<ViewItemCloneable> {

        if (!param.action.equals("scan", true)) {

            throw LowException("")
        }

        val list = arrayListOf<ViewItemCloneable>()

        MessageViewItem(
            id = "",
            message = R.string.scan_info_drag.toText().withTextColor( com.google.android.material.R.attr.colorOnBackground),
            messageIcon = R.drawable.ic_scan_drag_primary_24dp.toImage(),
        ).let {

            list.add(it)
        }

        MessageViewItem(
            id = "",
            message = R.string.scan_info_sub_normal.toText().withTextColor( com.google.android.material.R.attr.colorOnBackground),
            messageIcon = R.drawable.ic_scan_private_key_primary_24dp.toImage(),
        ).let {

            list.add(it)
        }

        return list
    }
}