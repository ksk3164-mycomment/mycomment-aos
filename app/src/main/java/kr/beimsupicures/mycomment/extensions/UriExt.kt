package kr.beimsupicures.mycomment.extensions

import android.content.Context
import android.net.Uri
import android.provider.MediaStore

fun Uri.getRealPathFromURI(context: Context): String? {

    return try {
        context.getContentResolver().query(this, arrayOf(MediaStore.Images.Media.DATA), null, null, null)?.let { cursor ->
            cursor.moveToFirst()
            cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
        }

    } finally {

    }
}
