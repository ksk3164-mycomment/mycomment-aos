package kr.beimsupicures.mycomment.common

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kr.beimsupicures.mycomment.api.loaders.UserLoader

fun accessUser() {
    FirebaseInstanceId.getInstance().instanceId
        .addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("AccessUser", "getInstanceId failed", task.exception)
                return@OnCompleteListener
            }

            // Get new Instance ID token
            task.result?.let { result ->
                val token = result.token
                Log.e("fcm", token)
                UserLoader.shared.accessUser(token)
            }
        })
}