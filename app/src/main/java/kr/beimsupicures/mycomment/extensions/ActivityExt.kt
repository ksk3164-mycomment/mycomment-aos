package kr.beimsupicures.mycomment.extensions

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.Point
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.material.bottomsheet.BottomSheetDialog
import kr.beimsupicures.mycomment.components.dialogs.CustomDialog

//fun Activity.popup(message: String, title: String, cancel: (() -> Unit)? = null, confirm: (() -> Unit)? = null) {
//    val builder = AlertDialog.Builder(this)
//
//    with(builder)
//    {
//        setTitle(title)
//        setMessage(message)
//        setPositiveButton("확인") { _: DialogInterface, _: Int ->
//            confirm?.invoke()
//        }
//        setNegativeButton("취소") { _: DialogInterface, _: Int ->
//            cancel?.invoke()
//        }
//
//        show()
//    }
//}
fun Activity.popup(message: String, title: String, cancel: (() -> Unit)? = null, confirm: (() -> Unit)? = null) {
    val builder = CustomDialog(this)

    with(builder)
    {
        setTitle(title)
        setMessage(message)
        setPositiveButton("확인") {
            confirm?.invoke()
        }
        setNegativeButton("취소") {
            cancel?.invoke()
        }
        show()
        setBackground()
    }

}

fun Activity.alert(message: String, title: String, confirm: () -> Unit) {
    val builder = CustomDialog(this)

    with(builder)
    {
        setTitle(title)
        setMessage(message)
        setPositiveButton("확인") {
            confirm()
        }

        show()
        setBackground()
    }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}
