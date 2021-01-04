package kr.beimsupicures.mycomment.components.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.components.adapters.LikeUserAdapter
import kr.beimsupicures.mycomment.components.adapters.TalkDetailAdapter
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser

class BubbleUserListDialog(context: Context, val items: MutableList<UserModel>) : Dialog(context) {

    lateinit var likeUserAdapter: LikeUserAdapter
    lateinit var likeUserView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        setCancelable(true)
        setContentView(R.layout.dialog_bubble_user_list)

        likeUserAdapter = LikeUserAdapter(items)
        likeUserView = findViewById(R.id.rv_like_user)
        likeUserView.layoutManager = LinearLayoutManager(context)
        likeUserView.adapter = likeUserAdapter

        likeUserAdapter.items = items
        likeUserAdapter.notifyDataSetChanged()
    }
}