package kr.beimsupicures.mycomment.api

import android.net.Uri
import android.os.Build
import android.util.Log
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.getRealPathFromURI
import java.io.File
import java.lang.Exception
import java.util.*

class AmazonS3Loader {
    companion object {
        val shared = AmazonS3Loader()
    }

    fun uploadImage(directory: String, uri: Uri, completionHandler: (String) -> Unit) {
        BaseApplication.shared.context()?.let { context ->
            val credentialsProvider =
                CognitoCachingCredentialsProvider(
                    context,
                    "ap-northeast-2:383e3291-5f42-4f02-8d42-804c76c8d6aa",
                    Regions.AP_NORTHEAST_2
                )

            TransferNetworkLossHandler.getInstance(context)

            val bucket = "kr.beimsupicures.mycomment"
            val transferUtility = TransferUtility.builder()
                .context(context)
                .defaultBucket("${bucket}/${directory}")
                .s3Client(
                    AmazonS3Client(credentialsProvider, Region.getRegion(
                        Regions.AP_NORTHEAST_2))
                )
                .build()

            val name = "${UUID.randomUUID()}"
            val image = File(if (Build.VERSION.SDK_INT > 28) uri.getRealPathFromURI(context) else uri.path)
            val observer = transferUtility.upload(name, image)
            observer.setTransferListener(object : TransferListener {
                override fun onProgressChanged(id: Int, current: Long, total: Long) {
                    val done = (((current.toDouble() / total) * 100.0).toInt())
                    Log.e("S3", "UPLOAD - - ID: $id, percent done = $done")
                }

                override fun onStateChanged(id: Int, state: TransferState?) {
                    when (state) {
                        TransferState.COMPLETED -> {
                            Log.e("S3", "Done")
                            val url = "https://s3.ap-northeast-2.amazonaws.com/${bucket}/${directory}/${name}"
                            completionHandler(url)
                        }
                    }
                }

                override fun onError(id: Int, ex: Exception?) {

                }
            })
        }
    }
    fun uploadImage2(directory: String, uri: Uri, completionHandler: (String) -> Unit) {
        BaseApplication.shared.context()?.let { context ->
            val credentialsProvider =
                CognitoCachingCredentialsProvider(
                    context,
                    "ap-northeast-2:383e3291-5f42-4f02-8d42-804c76c8d6aa",
                    Regions.AP_NORTHEAST_2
                )

            TransferNetworkLossHandler.getInstance(context)

            val bucket = "myco-img"
            val transferUtility = TransferUtility.builder()
                .context(context)
                .defaultBucket("${bucket}/${directory}")
                .s3Client(
                    AmazonS3Client(credentialsProvider, Region.getRegion(
                        Regions.AP_NORTHEAST_2))
                )
                .build()

            val name = "${UUID.randomUUID()}"
            val image = File(if (Build.VERSION.SDK_INT > 28) uri.getRealPathFromURI(context) else uri.path)
            val observer = transferUtility.upload(name, image)
            observer.setTransferListener(object : TransferListener {
                override fun onProgressChanged(id: Int, current: Long, total: Long) {
                    val done = (((current.toDouble() / total) * 100.0).toInt())
                    Log.e("S3", "UPLOAD - - ID: $id, percent done = $done")
                }

                override fun onStateChanged(id: Int, state: TransferState?) {
                    when (state) {
                        TransferState.COMPLETED -> {
                            Log.e("S3", "Done")
                            val url = "https://s3.ap-northeast-2.amazonaws.com/${bucket}/${directory}/${name}"
                            completionHandler(url)
                        }
                    }
                }

                override fun onError(id: Int, ex: Exception?) {
                    Log.e("S3", "Error = $ex")
                }
            })
        }
    }
}
