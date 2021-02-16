package kr.beimsupicures.mycomment.extensions

import android.app.Activity
import android.content.DialogInterface
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Activity.popup(message: String, title: String, cancel: (() -> Unit)? = null, confirm: (() -> Unit)? = null) {
    val builder = AlertDialog.Builder(this)

    with(builder)
    {
        setTitle(title)
        setMessage(message)
        setPositiveButton("확인") { _: DialogInterface, _: Int ->
            confirm?.invoke()
        }
        setNegativeButton("취소") { _: DialogInterface, _: Int ->
            cancel?.invoke()
        }

        show()
    }
}

fun Activity.alert(message: String, title: String, confirm: () -> Unit) {
    val builder = AlertDialog.Builder(this)

    with(builder)
    {
        setTitle(title)
        setMessage(message)
        setPositiveButton("확인") { _: DialogInterface, _: Int ->
            confirm()
        }

        show()
    }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}
