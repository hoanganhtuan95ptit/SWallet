package com.simple.wallet.utils.exts

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment


fun Fragment.hideKeyboard(editText: EditText) {

    val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(editText.windowToken, 0)
}

fun Fragment.showKeyboard(editText: EditText) {

    val inputMethodManager = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    inputMethodManager?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}