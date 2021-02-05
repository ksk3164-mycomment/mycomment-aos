package kr.beimsupicures.mycomment.controllers.main.talk

import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.AnalyticsLoader
import kr.beimsupicures.mycomment.api.loaders.CommentLoader
import kr.beimsupicures.mycomment.api.loaders.TalkLoader
import kr.beimsupicures.mycomment.api.models.CommentModel
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.common.diffSec
import kr.beimsupicures.mycomment.common.keyboard.KeyboardVisibilityUtils
import kr.beimsupicures.mycomment.common.keyboard.showKeyboard
import kr.beimsupicures.mycomment.components.adapters.TalkDetailAdapter
import kr.beimsupicures.mycomment.components.adapters.TalkDetailAdapter2
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.*
import java.lang.NumberFormatException


class TalkDetailFragment2 : BaseFragment() {

    var isLoaded: Boolean = false
    var validation: Boolean = false
        get() = when {
            messageField.text.isEmpty() -> false
            else -> true
        }
    var items: MutableList<CommentModel> = mutableListOf()
    var talk: TalkModel? = null
    var selectedCommentId = -1
    var count: Int = 0

    lateinit var detailAdapter: TalkDetailAdapter2
    lateinit var detailView: RecyclerView
    lateinit var messageField: EditText
    lateinit var btnSend: ImageView

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    private val scrollMover = ScrollMover()

    private val totalEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.

            (dataSnapshot.value as? Long)?.let { value ->
                Log.e("TALK", "Long snapshot: $value")
            }
            (dataSnapshot.value as? String)?.let { value ->
                Log.e("TALK", "String snapshot: $value")
            }

            talk?.let { talk ->
                val latest_id = items.firstOrNull()?.id ?: 0

                Log.e("isLoaded", "${isLoaded}")

                if (!isLoaded) {
                    isLoaded = true
                    Log.e("TALK", "latest_id: $latest_id")
                    CommentLoader.shared.getNewComment(talk.id, latest_id) { newValue ->
                        Log.e("TALK", "new snapshot: ${newValue.size}")
                        for (item in newValue.reversed()) {
                            this@TalkDetailFragment2.items.add(0, item)
                        }

                        CommentLoader.shared.getCommentCount(talk.id) { count ->
                            CommentLoader.shared.items = this@TalkDetailFragment2.items
                            detailAdapter.items = this@TalkDetailFragment2.items
                            this@TalkDetailFragment2.count = count
                            detailAdapter.count = this@TalkDetailFragment2.count
                            detailAdapter.notifyDataSetChanged()
                            isLoaded = false
                        }
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException())
        }
    }

    val likeEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.

            (dataSnapshot.value as? String)?.let { value ->
                Log.e("TALK", "String snapshot: $value")
                val splits = value.split("/")
                if (splits.size == 2) {
                    try {
                        val commentId = splits[0].toInt()
                        val pickCount = splits[1].toInt()
                        var index = items.indexOfFirst { item -> item.id == commentId }
                        if (index < 0) return
                        items[index].pick_count = pickCount
                        detailAdapter.notifyDataSetChanged()
                    } catch (e: NumberFormatException) { }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException())
        }
    }

    private val eventEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.

            dataSnapshot.value?.let { value ->
                try {
                    val eventNum = value.toString().toInt()
                    if (eventNum > 0) {
                        talk?.let {
                            TalkLoader.shared.getTalk(it.id) { newTalk ->
                                talk = newTalk
                                detailAdapter.talk = newTalk
                                detailAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                } catch (e: NumberFormatException) { }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
            onShowKeyboard = {
                scrollMover.moveFirstCommentIfTopPosition(detailView)
            })

        return inflater.inflate(R.layout.fragment_talk_detail, container, false)
    }

    override fun onResume() {
        super.onResume()
        fetchModel()

        talk?.let { talk ->

            TalkLoader.shared.increaseViewCount(talk.id) { }

            val database = FirebaseDatabase.getInstance()
            database.getReference("talk").child("${talk.id}").child("total").addValueEventListener(totalEventListener)
            database.getReference("talk").child("${talk.id}").child("like").addValueEventListener(likeEventListener)
            database.getReference("talk").child("${talk.id}").child("event").addValueEventListener(eventEventListener)
        }
    }

    override fun onPause() {
        talk?.let { talk ->
            val database = FirebaseDatabase.getInstance()
            database.getReference("talk").child("${talk.id}").child("total").removeEventListener(totalEventListener)
            database.getReference("talk").child("${talk.id}").child("like").removeEventListener(likeEventListener)
            database.getReference("talk").child("${talk.id}").child("event").removeEventListener(eventEventListener)
            BaseApplication.shared.getSharedPreferences().getTalkTime()?.let { time ->
                AnalyticsLoader.shared.exitTalk(talk.id, diffSec(time))
            }
            BaseApplication.shared.getSharedPreferences().setCurrentTalkId(-1)
        }

        isLoaded = false
        super.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        keyboardVisibilityUtils.detachKeyboardListeners()
    }

    override fun loadModel() {
        super.loadModel()
        talk = TalkDetailFragmentArgs.fromBundle(requireArguments()).talk
        selectedCommentId = TalkDetailFragmentArgs.fromBundle(requireArguments()).selectedCommentId
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            detailAdapter = TalkDetailAdapter2(
                activity, TalkDetailFragmentArgs.fromBundle(
                    requireArguments()
                ).talk, items, count , real = "fewa"
            ) { message ->
                messageField.setText(message)
                showKeyboard(requireActivity(), messageField)
            }
            detailView = view.findViewById(R.id.detailView)
            detailView.layoutManager = LinearLayoutManager(context)
            detailView.adapter = detailAdapter
            detailView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    detailView.layoutManager?.let {
                        (it as? LinearLayoutManager)?.let { layoutManager ->
                            recyclerView.adapter?.let { adapter ->
                                if (layoutManager.findLastVisibleItemPosition() == adapter.itemCount - 1) {
                                    talk?.let { talk ->
                                        CommentLoader.shared.getCommentList(talk_id = talk.id, reset = false) { talk ->
                                            this@TalkDetailFragment2.items = talk.toMutableList()
                                            detailAdapter.items = this@TalkDetailFragment2.items
                                            detailAdapter.notifyDataSetChanged()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            })
            messageField = view.findViewById(R.id.messageField)
            messageField.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) {

                }

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    context?.let { context ->
                        btnSend.setImageDrawable(
                            ContextCompat.getDrawable(
                                context,
                                if (validation) R.drawable.send else R.drawable.send_g)
                        )
                    }
                }
            })
            btnSend = view.findViewById(R.id.btnSend)
            btnSend.setOnClickListener {

                BaseApplication.shared.getSharedPreferences().getUser()?.let {

                    when (validation) {
                        true -> {

                            talk?.let { talk ->
                                val message = messageField.text.toString()
                                messageField.setText("")
                                hideKeyboard()
                                isLoaded = true
                                CommentLoader.shared.addTalkComment(
                                    talk.id, message
                                ) { newValue ->

                                    CommentLoader.shared.getCommentCountTotal(
                                        TalkDetailFragmentArgs.fromBundle(
                                            requireArguments()
                                        ).talk.id

                                    ) { total ->
                                        CommentLoader.shared.getCommentCount(
                                            TalkDetailFragmentArgs.fromBundle(
                                                requireArguments()
                                            ).talk.id

                                        ) { count ->

                                            isLoaded = false

                                            // Write a message to the database
                                            talk?.let { talk ->
                                                val database = FirebaseDatabase.getInstance()
                                                database.getReference("talk").child("${talk.id}").child("total").setValue(total)
                                                database.getReference("talk").child("${talk.id}").child("count").setValue(count)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        false -> { }
                    }

                } ?: run {

                    activity?.let { activity ->
                        activity.popup("로그인하시겠습니까?", "로그인") {
                            Navigation.findNavController(activity, R.id.nav_host_fragment)
                                .navigate(R.id.action_global_signInFragment)
                        }
                    }
                }
            }
        }
    }

    override fun fetchModel() {
        super.fetchModel()

        talk?.let { talk ->
            BaseApplication.shared.getSharedPreferences().setTalkTime()
            BaseApplication.shared.getSharedPreferences().setCurrentTalkId(talk.id)

            CommentLoader.shared.getCommentList(talk.id, true
            ) { items ->
                this.items = items
                detailAdapter.items = this.items
                detailAdapter.notifyDataSetChanged()
                scrollMover.moveSelectedComment(detailView, items, selectedCommentId)
            }

            CommentLoader.shared.getCommentCount(talk.id) { count ->
                this.count = count
                detailAdapter.count = this.count
                detailAdapter.notifyDataSetChanged()
            }
        }
    }

    inner class ScrollMover {

        fun moveFirstCommentIfTopPosition(view: RecyclerView) {
            view.run {
                if ((layoutManager as LinearLayoutManager).findFirstVisibleItemPosition() == 0)
                    (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(3, 0)
            }
        }

        fun moveSelectedComment(
            view: RecyclerView,
            items: MutableList<CommentModel>,
            selectedCommentId: Int
        ) {
            if (selectedCommentId >= 0) {
                val position = items.indexOfFirst { it.id == selectedCommentId }
                val headCount = 3
                view.run {
                    (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position+headCount, 0)
                }
            }
        }
    }
}

