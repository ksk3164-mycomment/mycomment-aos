package kr.beimsupicures.mycomment.common.keyboard

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

fun showKeyboard(activity: Activity, editText: EditText) {
    editText.requestFocus()
    val imm: InputMethodManager? = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.let {
        it.showSoftInput(editText, 0);
    }
    editText.setSelection(editText.text.length);
}