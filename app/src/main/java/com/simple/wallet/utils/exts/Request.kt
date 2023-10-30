package com.simple.wallet.utils.exts

import android.graphics.Typeface
import android.text.style.StyleSpan
import com.simple.adapter.ViewItemCloneable
import com.simple.core.utils.extentions.asObject
import com.simple.core.utils.extentions.asObjectOrNull
import com.simple.coreapp.utils.extentions.emptyImage
import com.simple.coreapp.utils.extentions.emptyText
import com.simple.coreapp.utils.extentions.text.Text
import com.simple.coreapp.utils.extentions.text.TextImage
import com.simple.coreapp.utils.extentions.text.TextRes
import com.simple.coreapp.utils.extentions.text.TextSpan
import com.simple.coreapp.utils.extentions.text.TextWithTextColorAttrColor
import com.simple.coreapp.utils.extentions.toImage
import com.simple.coreapp.utils.extentions.toPx
import com.simple.coreapp.utils.extentions.toText
import com.simple.coreapp.utils.extentions.withTextColor
import com.simple.wallet.R
import com.simple.wallet.domain.entities.Message
import com.simple.wallet.domain.entities.Request
import com.simple.wallet.domain.entities.Transaction
import com.simple.wallet.domain.entities.extra.ApproveExtra
import com.simple.wallet.domain.entities.extra.TransferExtra
import com.simple.wallet.presentation.adapters.HeaderViewItem
import com.simple.wallet.presentation.adapters.MessageViewItem
import java.math.BigInteger

fun Request.toConnectHeaderViewItem() = HeaderViewItem("").apply {

    val data = this@toConnectHeaderViewItem

    val sessionProposal = kotlin.runCatching { sessionProposal }.getOrNull()

    logo = data.power?.logo?.toImage() ?: emptyImage()

    title = if (sessionProposal != null) {

        TextRes(
            R.string.message_want_to_connect,
            TextSpan(data.power?.name?.toText() ?: emptyText(), StyleSpan(Typeface.BOLD)),
        )
    } else {

        emptyText()
    }

    updateHeaderViewItem(request = this@toConnectHeaderViewItem, headerViewItem = this)
}

fun Request.toTransactionHeaderViewItem() = HeaderViewItem("").apply {

    val data = this@toTransactionHeaderViewItem

    logo = data.power?.logo?.toImage() ?: emptyImage()


    val message = data.message

    val transaction = data.transaction

    title = if (transaction != null && transaction.type == Transaction.Type.SEND) {

        TextRes(
            R.string.message_transfer,
            TextSpan(data.power?.name?.toText() ?: emptyText(), StyleSpan(Typeface.BOLD)),
            TextSpan(data.transaction?.extra.asObjectOrNull<TransferExtra>()?.tokenTransfer?.symbol?.uppercase()?.toText() ?: emptyText(), StyleSpan(Typeface.BOLD))
        )
    } else if (transaction != null && transaction.type == Transaction.Type.APPROVAL && transaction.extra.asObject<ApproveExtra>().amountApprove > BigInteger.ZERO) {

        TextRes(
            R.string.message_approve,
            TextSpan(data.power?.name?.toText() ?: emptyText(), StyleSpan(Typeface.BOLD)),
            TextSpan(data.transaction?.extra.asObjectOrNull<ApproveExtra>()?.tokenApprove?.symbol?.uppercase()?.toText() ?: emptyText(), StyleSpan(Typeface.BOLD))
        )
    } else if (transaction != null && transaction.type == Transaction.Type.APPROVAL && transaction.extra.asObject<ApproveExtra>().amountApprove <= BigInteger.ZERO) {

        TextRes(
            R.string.message_revoke,
            TextSpan(data.power?.name?.toText() ?: emptyText(), StyleSpan(Typeface.BOLD)),
            TextSpan(data.transaction?.extra.asObjectOrNull<ApproveExtra>()?.tokenApprove?.symbol?.uppercase()?.toText() ?: emptyText(), StyleSpan(Typeface.BOLD))
        )
    } else if (transaction != null) {

        TextRes(
            R.string.message_send_transtion,
            TextSpan(data.power?.name?.toText() ?: emptyText(), StyleSpan(Typeface.BOLD)),
        )
    } else if (message != null && message.type == Message.Type.SIGN_PERMIT) {

        TextRes(
            R.string.message_sign_permit,
            TextSpan(data.power?.name?.toText() ?: emptyText(), StyleSpan(Typeface.BOLD)),
            TextSpan(data.message?.extra.asObjectOrNull<ApproveExtra>()?.tokenApprove?.symbol?.uppercase()?.toText() ?: emptyText(), StyleSpan(Typeface.BOLD))
        )
    } else if (message != null) {

        TextRes(
            R.string.message_sign_message,
            TextSpan(data.power?.name?.toText() ?: emptyText(), StyleSpan(Typeface.BOLD)),
        )
    } else {

        emptyText()
    }

    updateHeaderViewItem(request = this@toTransactionHeaderViewItem, headerViewItem = this)
}


private fun updateHeaderViewItem(request: Request, headerViewItem: HeaderViewItem) = headerViewItem.apply {

    val data = request

    when (data.power?.status) {

        Request.Power.Status.VERIFY -> {

            caption = listOf(
                TextImage(R.drawable.img_verified_24dp, 16.toPx()),
                TextWithTextColorAttrColor(data.power?.url?.toText() ?: emptyText(), R.attr.colorVerify),
            ).toText()

            captionBackground = R.drawable.bg_corners_8dp_solid_verify_10
        }

        Request.Power.Status.RISK -> {

            caption = listOf(
                TextImage(R.drawable.img_error_24dp, 14.toPx()),
                TextWithTextColorAttrColor(data.power?.url?.toText() ?: emptyText(), com.google.android.material.R.attr.colorError),
            ).toText()

            captionBackground = R.drawable.bg_corners_8dp_solid_error_10
        }

        else -> {

            caption = listOf(
                TextImage(R.drawable.img_warning_24dp, 14.toPx()),
                TextWithTextColorAttrColor(data.power?.url?.toText() ?: emptyText(), R.attr.colorWarning),
            ).toText()

            captionBackground = R.drawable.bg_corners_8dp_solid_warning_10
        }
    }
}


fun Request?.toMessageViewItem(isConfirm: Boolean): List<ViewItemCloneable> = this.run {

    if (this == null) {

        return@run emptyList()
    }


    val list = arrayListOf<Text>()

    if (power?.status in listOf(Request.Power.Status.RISK)) {

        list.add(TextRes(R.string.message_warning_url_risk, TextImage(R.drawable.ic_check_box_normal_accent_24dp, 16.toPx())))
    }

    if (list.isNotEmpty()) return@run MessageViewItem(id = "KEY").apply {

        list.add(0, TextSpan(R.string.message_warning.toText(), StyleSpan(Typeface.BOLD)))

        message = list.toText("\n").withTextColor(com.google.android.material.R.attr.colorError)

        messageIcon = if (isConfirm) {
            R.drawable.ic_check_box_select_accent_24dp.toImage()
        } else {
            R.drawable.ic_check_box_normal_accent_24dp.toImage()
        }

        background = R.drawable.bg_corners_16dp_solid_error_10

        needConfirm = true
    }.let {

        listOf(it)
    } else {

        return@run emptyList()
    }
}