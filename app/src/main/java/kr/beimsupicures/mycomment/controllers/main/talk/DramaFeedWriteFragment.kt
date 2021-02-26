package kr.beimsupicures.mycomment.controllers.main.talk

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import gun0912.tedimagepicker.builder.TedImagePicker
import jp.wasabeef.richeditor.RichEditor
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.components.fragments.startLoadingUI
import kr.beimsupicures.mycomment.components.fragments.stopLoadingUI


class DramaFeedWriteFragment : BaseFragment() {

    lateinit var editor: RichEditor
    lateinit var insertImageLayout: LinearLayout
    lateinit var title: EditText
    var editorEmpty : Boolean= false
    var editorText :String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drama_feed_write, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            title = view.findViewById(R.id.title)
            insertImageLayout = view.findViewById(R.id.InsertImageLayout)
            editor = view.findViewById(R.id.editor)
            editor.setEditorFontSize(15)
            editor.setEditorFontColor(Color.BLACK)
            editor.setPlaceholder("내용 (드라마와 관련된 내용이 있어야하며, 비방, 욕설, 음란물 등을 등록할 시에는 삭제 및 차단 조치됩니다. 사진은 8장까지 등록 가능합니다.)")

            title.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                override fun afterTextChanged(s: Editable?) {

                }

            })

            insertImageLayout.setOnClickListener {
                context?.let { context ->
                    TedImagePicker.with(context).start { uri ->
                        startLoadingUI()
//                        AmazonS3Loader.shared.uploadImage("feed", uri) { url ->
                            Log.e("tjdrnr", "" + uri)
                            stopLoadingUI()
                            editor.insertImage(uri.toString(), "", 320)
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
}

