package kr.beimsupicures.mycomment.controllers.main.feed

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import gun0912.tedimagepicker.builder.TedImagePicker
import jp.wasabeef.richeditor.RichEditor
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.FeedLoader
import kr.beimsupicures.mycomment.api.models.FeedDetailModel
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.getFeedId
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.setFeedId

class DramaFeedModifyFragment : BaseFragment() {

    lateinit var editor: RichEditor
    lateinit var insertImageLayout: LinearLayout
    lateinit var title: EditText
    var editorEmpty : Boolean= false
    var editorText :String?=null
    var feed_seq = 0

    var feedDetail: FeedDetailModel? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drama_feed_modify, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            title = view.findViewById(R.id.title)
            insertImageLayout = view.findViewById(R.id.InsertImageLayout)
            editor = view.findViewById(R.id.editor)
            editor.setEditorFontSize(15)
            editor.setEditorFontColor(Color.BLACK)

            val displayMetrics = context?.resources?.displayMetrics
//            val dpHeight = displayMetrics!!.heightPixels / displayMetrics.density
            val dpWidth = displayMetrics!!.widthPixels / displayMetrics.density

            insertImageLayout.setOnClickListener {
                context?.let { context ->
                    TedImagePicker.with(context).start { uri ->
//                        startLoadingUI()
//                        AmazonS3Loader.shared.uploadImage("feed", uri) { url ->
//                            Log.e("tjdrnr", "" + uri)
//                            stopLoadingUI()
                        editor.insertImage(uri.toString(), "", dpWidth.toInt()-32)
//                        }
                    }
                }
            }

            editor.setOnTextChangeListener { text -> // Do Something
                Log.e("tjdrnr", "Editor = $text")

                editorEmpty = text.isNotEmpty()
                editorText = text

            }
        }
    }

    override fun loadModel() {
        super.loadModel()

        feed_seq = BaseApplication.shared.getSharedPreferences().getFeedId()
        FeedLoader.shared.getFeedDetail(feed_seq) { values ->

            val displayMetrics = this.resources.displayMetrics
            val dpWidth =
                displayMetrics!!.widthPixels / displayMetrics.density

            title.setText(values.title)
            editor.html = values.content?.replace(
                """" alt=""""",
                """" alt="" width="${dpWidth.toInt() - 32}""""
            )
        }
    }


}