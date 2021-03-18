package kr.beimsupicures.mycomment.controllers.main.talk

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_drama_feed_detail.*
import kotlinx.android.synthetic.main.fragment_real_time_talk.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.AnalyticsLoader
import kr.beimsupicures.mycomment.api.loaders.CommentLoader
import kr.beimsupicures.mycomment.api.loaders.TalkLoader
import kr.beimsupicures.mycomment.api.models.CommentModel
import kr.beimsupicures.mycomment.api.models.EventObserver
import kr.beimsupicures.mycomment.api.models.MyViewModel
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.common.diffSec
import kr.beimsupicures.mycomment.common.keyboard.KeyboardVisibilityUtils
import kr.beimsupicures.mycomment.components.adapters.TalkDetailAdapter
import kr.beimsupicures.mycomment.components.adapters.list_onClick_interface
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.*
import java.util.*


class RealTimeTalkFragment(val viewModel: TalkModel) : BaseFragment(),
    list_onClick_interface {

    var talk: TalkModel? = null
    var count = 0

    lateinit var countLabel: TextView
    lateinit var ivScrollTop: ImageView
    lateinit var constraintLayout2: ConstraintLayout
    lateinit var floatingProfile: ImageView
    lateinit var floatingUserId: TextView
    lateinit var floatingMessage: TextView

    //    lateinit var messageField: EditText
//    lateinit var btnSend: ImageView
    lateinit var detailAdapter: TalkDetailAdapter
    lateinit var rvRealtimeTalk: RecyclerView
    lateinit var viewModel2: MyViewModel

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    var selectedCommentId = -1

    private val scrollMover = ScrollMover()

    var isLoaded: Boolean = false
    var isFirstVisiblePosition: Boolean = false

    //    var validation: Boolean = false
//        get() = when {
//            messageField.text.isEmpty() -> false
//            else -> true
//        }
    var items: MutableList<CommentModel> = mutableListOf()

    companion object {

        fun newInstance(
            viewModel: TalkModel
        ): RealTimeTalkFragment {
            return RealTimeTalkFragment(viewModel)
        }
    }


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
                                this@RealTimeTalkFragment.items.add(0, item)
                            }

                            isLoaded = false
                            if (newValue.size != 0) {
                                Log.e("tjdrnr", "newValue")
                                count += newValue.size
                                countLabel.text = "${count}개의 톡"

                                if (isFirstVisiblePosition) {
                                    detailAdapter.notifyDataSetChanged()
                                } else {
                                    constraintLayout2.visibility = View.VISIBLE
                                    detailAdapter.notifyItemInserted(0)
                                    Glide.with(this@RealTimeTalkFragment).load(newValue[0].owner.profile_image_url)
                                        .transform(CenterCrop(), CircleCrop())
                                        .override(200, 200)
                                        .thumbnail(0.1f)
                                        .fallback(R.drawable.bg_drama_thumbnail)
                                        .into(floatingProfile)
                                    floatingUserId.text = newValue[0].owner.nickname
                                    floatingMessage.text = newValue[0].content
                                }

                            }

                        }

                }

            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
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
                    } catch (e: NumberFormatException) {
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
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
                } catch (e: NumberFormatException) {
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        keyboardVisibilityUtils.detachKeyboardListeners()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
            onShowKeyboard = {
//                scrollMover.moveFirstCommentIfTopPosition(rvRealtimeTalk)
            })
        return inflater.inflate(R.layout.fragment_real_time_talk, container, false)

    }

    override fun onResume() {
        super.onResume()
        fetchModel()

        talk?.let { talk ->

            TalkLoader.shared.increaseViewCount(talk.id) { }

            val database = FirebaseDatabase.getInstance()
            database.getReference("talk").child("${talk.id}").child("total")
                .addValueEventListener(totalEventListener)
            database.getReference("talk").child("${talk.id}").child("like")
                .addValueEventListener(likeEventListener)
            database.getReference("talk").child("${talk.id}").child("event")
                .addValueEventListener(eventEventListener)
        }

    }

    override fun onPause() {
        super.onPause()

        talk?.let { talk ->
            val database = FirebaseDatabase.getInstance()
            database.getReference("talk").child("${talk.id}").child("total")
                .removeEventListener(totalEventListener)
            database.getReference("talk").child("${talk.id}").child("like")
                .removeEventListener(likeEventListener)
            database.getReference("talk").child("${talk.id}").child("event")
                .removeEventListener(eventEventListener)
            BaseApplication.shared.getSharedPreferences().getTalkTime()?.let { time ->
                AnalyticsLoader.shared.exitTalk(talk.id, diffSec(time))
            }
            BaseApplication.shared.getSharedPreferences().setCurrentTalkId(-1)
        }
        isLoaded = false


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel2 = activity?.let { ViewModelProviders.of(it).get(MyViewModel::class.java) }!!
        viewModel2.getMessage.observe(viewLifecycleOwner, EventObserver { t ->

            Log.e("tjdrnr", "datachange")

            sendMessage(t)
        })

    }


    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            countLabel = view.findViewById(R.id.countLabel)
            ivScrollTop = view.findViewById(R.id.iv_scroll_top)
            constraintLayout2 = view.findViewById(R.id.constraintLayout2)
            floatingProfile = view.findViewById(R.id.floating_profile)
            floatingUserId = view.findViewById(R.id.floating_user_id)
            floatingMessage = view.findViewById(R.id.floating_message)

            talk?.let { values ->

                CommentLoader.shared.getCommentCount(values.id) { count ->
                    this.count = count
                    countLabel.text = "${count}개의 톡"
                }

                //
                detailAdapter = TalkDetailAdapter(activity, values, items, { message ->
                    viewModel2.setReply(message)
                }, this)
                rvRealtimeTalk = view.findViewById(R.id.rvRealtimeTalk)
                rvRealtimeTalk.setHasFixedSize(true)
                rvRealtimeTalk.layoutManager = LinearLayoutManager(context)
                rvRealtimeTalk.adapter = detailAdapter
                rvRealtimeTalk.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        rvRealtimeTalk.layoutManager?.let {
                            (it as? LinearLayoutManager)?.let { layoutManager ->

                                recyclerView.adapter?.let { adapter ->
                                    if (layoutManager.findLastVisibleItemPosition() == adapter.itemCount - 1) {
                                        talk?.let { talk ->
                                            CommentLoader.shared.getCommentList(
                                                talk_id = talk.id,
                                                reset = false
                                            ) { talk ->
                                                this@RealTimeTalkFragment.items =
                                                    talk.toMutableList()
                                                detailAdapter.items =
                                                    this@RealTimeTalkFragment.items
                                                detailAdapter.notifyDataSetChanged()
                                            }
                                        }
                                    }
                                    isFirstVisiblePosition =
                                        layoutManager.findFirstVisibleItemPosition() == 0
                                    if (isFirstVisiblePosition) {
                                        ivScrollTop.visibility = View.GONE
                                        constraintLayout2.visibility = View.GONE
                                    } else {
                                        ivScrollTop.visibility = View.VISIBLE
                                    }
                                }
                            }
                        }
                    }
                })
                ivScrollTop.setOnClickListener {
                    rvRealtimeTalk.scrollToPosition(0)
                }
            }
        }
    }

    override fun loadModel() {
        super.loadModel()
        talk = viewModel
    }

    override fun fetchModel() {
        super.fetchModel()

        talk?.let { talk ->
            BaseApplication.shared.getSharedPreferences().setTalkTime()
            BaseApplication.shared.getSharedPreferences().setCurrentTalkId(talk.id)

                CommentLoader.shared.getCommentList(
                    talk.id, true
                ) { items ->
                    this.items = items
                    detailAdapter.items = this.items
                    detailAdapter.notifyDataSetChanged()
//                    scrollMover.moveSelectedComment(rvRealtimeTalk, items, selectedCommentId)
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
                    (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                        position + headCount,
                        0
                    )
                }
            }
        }
    }

    override fun onCheckBox(friend_data: Int) {
        countLabel.text = "${friend_data}개의 톡"
    }

    fun sendMessage(message: String) {
        talk?.let { talk ->

            hideKeyboard()
//            isLoaded = true
            CommentLoader.shared.addTalkComment(
                talk.id, message
            ) { newValue ->
                val latest_id = items.firstOrNull()?.id ?: 0
//                CommentLoader.shared.getNewComment(
//                    talk.id,
//                    latest_id
//                ) { newValue ->
//                    Log.e("tjdrnr", "newvalue" + newValue)
//                    Log.e("TALK", "new snapshot: ${newValue.size}")
//                    for (item in newValue.reversed()) {
//                        this@RealTimeTalkFragment.items.add(0, item)
//                    }
//                    detailAdapter.notifyDataSetChanged()
//                    isLoaded = false

//                    CommentLoader.shared.getCommentCount(talk.id) { count ->
//                                        Write a message to the database

//                    count+=1

                val milis = System.currentTimeMillis()

//                    val name = "${UUID.randomUUID()}"
                        talk?.let { talk ->
                            val database = FirebaseDatabase.getInstance()
                            database.getReference("talk")
                                .child("${talk.id}")
                                .child("total").setValue(milis)
                            database.getReference("talk")
                                .child("${talk.id}")
                                .child("count").setValue(milis)
//                        }
                        countLabel.text = "${count}개의 톡"
//                    }
                }
            }
            rvRealtimeTalk.scrollToPosition(0)
        }
    }
}