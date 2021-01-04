package kr.beimsupicures.mycomment.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat
import java.util.*

fun isPushEnabledAtOSLevel(context: Context): Boolean {
    return NotificationManagerCompat.from(context).areNotificationsEnabled()
}

fun moveOSSetting(activity: Activity) {
    val intent = Intent()
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity?.packageName)
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP -> {
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", activity?.packageName)
            intent.putExtra("app_uid", activity?.applicationInfo?.uid)
        }

        else -> {
            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("package:" + activity?.packageName)
        }
    }

    activity?.startActivity(intent)
}

fun diffSec(sec: Long): Int {
    val currentSec = System.currentTimeMillis() / 1000
    val result = currentSec - sec;
    return result.toInt()
}

fun convertLongToTime(time: Long): String {
    val date = Date(time*1000)
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return format.format(date)
}

fun convertDateToLong(date: String, pattern: String): Long { // pattern ex. "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    val df = SimpleDateFormat(pattern)
    return df.parse(date).time / 1000
}