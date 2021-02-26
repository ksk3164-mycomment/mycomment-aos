package kr.beimsupicures.mycomment.controllers.main.talk


import android.content.ContentValues
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_real_time_talk.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.AnalyticsLoader
import kr.beimsupicures.mycomment.api.loaders.CommentLoader
import kr.beimsupicures.mycomment.api.loaders.FeedCommentLoader
import kr.beimsupicures.mycomment.api.models.CommentModel
import kr.beimsupicures.mycomment.api.models.FeedModel
import kr.beimsupicures.mycomment.common.diffSec
import kr.beimsupicures.mycomment.common.keyboard.KeyboardVisibilityUtils
import kr.beimsupicures.mycomment.common.keyboard.showKeyboard
import kr.beimsupicures.mycomment.components.adapters.FeedDetailAdapter
import kr.beimsupicures.mycomment.components.adapters.onClickInterface
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.*


class FeedCommentFragment(val viewModel: FeedModel) : BaseFragment(),onClickInterface {

    var feed: FeedModel? = null

    lateinit var countLabel: TextView
    lateinit var messageField: EditText
    lateinit var btnSend: ImageView
    lateinit var detailAdapter: FeedDetailAdapter
    lateinit var rvRealtimeTalk: RecyclerView

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    var selectedCommentId = -1

    private val scrollMover = ScrollMover()

    var isLoaded: Boolean = false
    var validation: Boolean = false
        get() = when {
            messageField.text.isEmpty() -> false
            else -> true
        }
    var items: MutableList<CommentModel> = mutableListOf()

    companion object {

        fun newInstance(
            viewModel: FeedModel
        ): FeedCommentFragment {
            return FeedCommentFragment(viewModel)
        }
    }

    private val totalEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.

            (dataSnapshot.value as? Long)?.let { value ->
                Log.e("FEED", "Long snapshot: $value")
            }
            (dataSnapshot.value as? String)?.let { value ->
                Log.e("FEED", "String snapshot: $value")
            }

            feed?.let { feed ->
                val latest_id = items.firstOrNull()?.id ?: 0

                Log.e("isLoaded", "${isLoaded}")

                if (!isLoaded) {
                    isLoaded = true
                    Log.e("FEED", "latest_id: $latest_id")
                    Handler().postDelayed({
                        FeedCommentLoader.shared.getNewFeedComment(
                            feed.feed_seq,
                            latest_id
                        ) { newValue ->
                            Log.e("FEED", "new snapshot: ${newValue.size}")
                            for (item in newValue.reversed()) {
                                this@FeedCommentFragment.items.add(0, item)
                            }
                            detailAdapter.notifyDataSetChanged()
                            isLoaded = false
                        }
                    },1000)
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
                Log.e("FEED", "String snapshot: $value")
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

//    private val eventEventListener = object : ValueEventListener {
//        override fun onDataChange(dataSnapshot: DataSnapshot) {
//            // This method is called once with the initial value and again
//            // whenever data at this location is updated.
//
//            dataSnapshot.value?.let { value ->
//                try {
//                    val eventNum = value.toString().toInt()
//                    if (eventNum > 0) {
//                        feed?.let {
//                            TalkLoader.shared.getTalk(it.id) { newTalk ->
//                                talk = newTalk
//                                detailAdapter.talk = newTalk
//                                detailAdapter.notifyDataSetChanged()
//                            }
//                        }
//                    }
//                } catch (e: NumberFormatException) {
//                }
//            }
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//            // Failed to read value
//            Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
//        }
//    }

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
                scrollMover.moveFirstCommentIfTopPosition(rvRealtimeTalk)
            })
        return inflater.inflate(R.layout.fragment_real_time_talk, container, false)

    }

    override fun onResume() {
        super.onResume()
        fetchModel()

        feed?.let { feed ->

            val database = FirebaseDatabase.getInstance()
            database.getReference("feed").child("${feed.feed_seq}").child("total")
                .addValueEventListener(totalEventListener)
            database.getReference("feed").child("${feed.feed_seq}").child("like")
                .addValueEventListener(likeEventListener)
//            database.getReference("talk").child("${talk.id}").child("event")
//                .addValueEventListener(eventEventListener)
        }

    }

    override fun onPause() {
        super.onPause()

        feed?.let { feed ->
            val database = FirebaseDatabase.getInstance()
            database.getReference("feed").child("${feed.feed_seq}").child("total")
                .removeEventListener(totalEventListener)
            database.getReference("feed").child("${feed.feed_seq}").child("like")
                .removeEventListener(likeEventListener)
//            database.getReference("feed").child("${feed.feed_seq}").child("event")
//                .removeEventListener(eventEventListener)
            BaseApplication.shared.getSharedPreferences().getTalkTime()?.let { time ->
                AnalyticsLoader.shared.exitTalk(feed.feed_seq, diffSec(time))
            }
//            BaseApplication.shared.getSharedPreferences().setCurrentTalkId(-1)
        }

        isLoaded = false
    }


    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            countLabel = view.findViewById(R.id.countLabel)


            feed?.let { feed ->

                FeedCommentLoader.shared.getFeedCommentCount(feed.feed_seq) { count ->
                    countLabel.text = "${count}개의 댓글"
                }

                detailAdapter = FeedDetailAdapter(activity, feed, items, { message ->
                    messageField.setText(message)
                    showKeyboard(requireActivity(), messageField)
                },this)
                rvRealtimeTalk = view.findViewById(R.id.rvRealtimeTalk)
                rvRealtimeTalk.layoutManager = LinearLayoutManager(context)
                rvRealtimeTalk.adapter = detailAdapter
                rvRealtimeTalk.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        rvRealtimeTalk.layoutManager?.let {
                            (it as? LinearLayoutManager)?.let { layoutManager ->
                                recyclerView.adapter?.let { adapter ->
                                    if (layoutManager.findLastVisibleItemPosition() == adapter.itemCount - 1) {
                                        feed?.let { feed ->
                                            FeedCommentLoader.shared.getFeedCommentList(
                                                feed.feed_seq,
                                                reset = false
                                            ) { feed ->
                                                this@FeedCommentFragment.items =
                                                    feed.toMutableList()
                                                detailAdapter.items =
                                                    this@FeedCommentFragment.items
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
                                    if (validation) R.drawable.send else R.drawable.send_g
                                )
                            )
                        }
                    }
                })
                btnSend = view.findViewById(R.id.btnSend)
                btnSend.setOnClickListener {

                    BaseApplication.shared.getSharedPreferences().getUser()?.let {

                        when (validation) {
                            true -> {

                                feed?.let { feed ->
                                    val message = messageField.text.toString()
                                    messageField.setText("")
                                    hideKeyboard()
                                    isLoaded = true
                                    FeedCommentLoader.shared.addFeedComment(
                                        feed.feed_seq, message
                                    ) { newValue ->
                                        val latest_id = items.firstOrNull()?.id ?: 0
                                        FeedCommentLoader.shared.getNewFeedComment(
                                            feed.feed_seq,
                                            latest_id
                                        ) { newValue ->
                                            Log.e("TALK", "new snapshot: ${newValue.size}")
                                            for (item in newValue.reversed()) {
                                                this@FeedCommentFragment.items.add(0, item)
                                            }
                                            detailAdapter.notifyDataSetChanged()
                                            isLoaded = false
                                        }


                                    }

                                    FeedCommentLoader.shared.getFeedCommentCount(feed.feed_seq) { count ->
//                                        Write a message to the database
                                        feed.let { feed ->
                                            val database = FirebaseDatabase.getInstance()
                                            database.getReference("feed")
                                                .child("${feed.feed_seq}")
                                                .child("total").setValue(count)
                                            database.getReference("feed")
                                                .child("${feed.feed_seq}")
                                                .child("count").setValue(count)
                                        }
                                        countLabel.text = "${count}개의 댓글"
                                    }
                                }
                            }

                            false -> {
                            }
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
    }

    override fun loadModel() {
        super.loadModel()
//        talk = arguments?.getParcelable("amount")
        feed = viewModel
//        selectedCommentId = viewModel.selectedCommentId
    }

    override fun fetchModel() {
        super.fetchModel()

        feed?.let { feed ->
            BaseApplication.shared.getSharedPreferences().setTalkTime()
            BaseApplication.shared.getSharedPreferences().setCurrentTalkId(feed.feed_seq)

            FeedCommentLoader.shared.getFeedCommentList(
                feed.feed_seq, true
            ) { items ->
                this.items = items
                detailAdapter.items = this.items
                detailAdapter.notifyDataSetChanged()
                scrollMover.moveSelectedComment(rvRealtimeTalk, items, selectedCommentId)
            }
//            CommentLoader.shared.getCommentCount(talk.id) { count ->
//                this.count = count
//                detailAdapter.count = this.count
//                detailAdapter.notifyDataSetChanged()
//            }
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

    override fun onClick(count: Int) {
        var deletecount = count-1
        countLabel.text = "${deletecount}개의 댓글"
    }

}