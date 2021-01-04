package kr.beimsupicures.mycomment.controllers.main.watch

import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.observe
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.CommentLoader
import kr.beimsupicures.mycomment.api.loaders.WatchLoader
import kr.beimsupicures.mycomment.api.models.KommentModel
import kr.beimsupicures.mycomment.api.models.ReplayInfo
import kr.beimsupicures.mycomment.api.models.ReplayModel
import kr.beimsupicures.mycomment.api.models.WatchModel
import kr.beimsupicures.mycomment.common.convertDateToLong
import kr.beimsupicures.mycomment.common.convertLongToTime
import kr.beimsupicures.mycomment.common.diffSec
import kr.beimsupicures.mycomment.common.keyboard.KeyboardVisibilityUtils
import kr.beimsupicures.mycomment.common.keyboard.showKeyboard
import kr.beimsupicures.mycomment.components.adapters.WatchDetailAdapter
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.*
import java.lang.NumberFormatException
import kotlin.collections.ArrayList

class WatchDetailFragment : BaseFragment() {

    var time: Int = 0

    var handler: Handler? = null
    var running: Boolean = false

    var isLoaded: Boolean = false
    var validation: Boolean = false
        get() = when {
            messageField.text.isEmpty() -> false
            else -> true
        }
    var items: MutableList<KommentModel> = mutableListOf()
    var replayItem: ReplayModel? = null

    var total: Int = 0
    var count: Int = 0
    var watch: WatchModel? = null
    var kicker: ArrayList<Long> = arrayListOf()

    lateinit var detailAdapter: WatchDetailAdapter
    lateinit var detailView: RecyclerView
    lateinit var messageField: EditText
    lateinit var btnSend: ImageView

    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    val totalEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.

            (dataSnapshot.value as? Long)?.let { value ->
                Log.e(TAG, "snapshot: $value")


                watch?.let { watch ->
                    val latest_id = items.firstOrNull()?.id ?: 0

                    if (!isLoaded) {
                        isLoaded = true
                        Log.e(TAG, "latest_id: $latest_id")
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

    val kickerEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.

            (dataSnapshot.value as? ArrayList<Long>)?.let { value ->
                kicker = value

                for (id in value) {
                    if (id.toInt() == BaseApplication.shared.getSharedPreferences().getUser()?.id) {
                        activity?.alert("방장에 의해 퇴장되었습니다.", "알림", {
                            activity?.onBackPressed()
                        })
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException())
        }
    }

    val titleEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.

            (dataSnapshot.value as? String)?.let { value ->
                watch?.let { watch ->
                    var newValue = watch
                    newValue.title = value
                    detailAdapter.watch = newValue
                    detailAdapter.notifyDataSetChanged()
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException())
        }
    }

    val contentEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.

            (dataSnapshot.value as? String)?.let { value ->
                watch?.let { watch ->
                    var newValue = watch
                    newValue.content = value
                    detailAdapter.watch = newValue
                    detailAdapter.notifyDataSetChanged()
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            // Failed to read value
            Log.w(TAG, "Failed to read value.", error.toException())
        }
    }

    val titleimageurlEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // This method is called once with the initial value and again
            // whenever data at this location is updated.

            (dataSnapshot.value as? String)?.let { value ->
                watch?.let { watch ->
                    var newValue = watch
                    newValue.title_image_url = value
                    detailAdapter.watch = newValue
                    detailAdapter.notifyDataSetChanged()
                }
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
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        keyboardVisibilityUtils = KeyboardVisibilityUtils(requireActivity().window,
            onShowKeyboard = { keyboardHeight ->
                detailView.run {
                    smoothScrollToPosition(5)
                }
            })
        return inflater.inflate(R.layout.fragment_watch_detail, container, false)
    }

    override fun onResume() {
        super.onResume()
        fetchModel()

        watch?.let { watch ->
            when (watch.owner?.id == BaseApplication.shared.getSharedPreferences().getUser()?.id) {
                true -> {
                    val database = FirebaseDatabase.getInstance()
                    database.getReference("watch").child("${watch.id}").child("title").setValue(watch.title)
                    database.getReference("watch").child("${watch.id}").child("title_image_url").setValue(watch.title_image_url)
                    database.getReference("watch").child("${watch.id}").child("content").setValue(watch.content)
                }
                false -> {
                    val database = FirebaseDatabase.getInstance()
                    database.getReference("watch").child("${watch.id}").child("title")
                        .addValueEventListener(titleEventListener)
                    database.getReference("watch").child("${watch.id}").child("content")
                        .addValueEventListener(contentEventListener)
                    database.getReference("watch").child("${watch.id}").child("title_image_url")
                        .addValueEventListener(titleimageurlEventListener)
                }
            }
        }

        watch?.let { watch ->
            val database = FirebaseDatabase.getInstance()
            database.getReference("watch").child("${watch.id}").child("total")
                .addValueEventListener(totalEventListener)
            database.getReference("watch").child("${watch.id}").child("kicker")
                .addValueEventListener(kickerEventListener)
            database.getReference("watch").child("${watch.id}").child("like")
                .addValueEventListener(likeEventListener)
        }
    }

    override fun onPause() {
        watch?.let { watch ->
            BaseApplication.shared.getSharedPreferences().getWatchTime()?.let { time ->
                WatchLoader.shared.exitWatch(watch.id, diffSec(time))
            }
        }

        watch?.let { watch ->
            val database = FirebaseDatabase.getInstance()
            database.getReference("watch").child("${watch.id}").child("total")
                .removeEventListener(totalEventListener)
            database.getReference("watch").child("${watch.id}").child("kicker")
                .removeEventListener(kickerEventListener)
            database.getReference("watch").child("${watch.id}").child("like")
                .removeEventListener(likeEventListener)
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

        watch = WatchDetailFragmentArgs.fromBundle(requireArguments()).watch
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<WatchModel>("watch")?.observe(viewLifecycleOwner) { watch ->
            Log.e("watch", "${watch.title}")
            Log.e("watch", "${watch.content}")
            this@WatchDetailFragment.watch = watch
            detailAdapter.watch = watch
            detailAdapter.notifyDataSetChanged()

            val database = FirebaseDatabase.getInstance()
            database.getReference("watch").child("${watch.id}").child("title").setValue(watch.title)
            database.getReference("watch").child("${watch.id}").child("title_image_url").setValue(watch.title_image_url)
            database.getReference("watch").child("${watch.id}").child("content").setValue(watch.content)
        }
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            detailAdapter = WatchDetailAdapter(
                activity, WatchDetailFragmentArgs.fromBundle(
                    requireArguments()
                ).watch, items, time, count,
                // onStart
                {
                    running = true

                    if (handler == null) {
                        handler = Handler(Looper.getMainLooper())
                        handler?.post(object : Runnable {
                            override fun run() {
                                if (running) {
                                    time += 1
                                }
                                detailAdapter.playtime = time

                                replayItem?.let { replayItem ->
                                    replayItem.apply {
                                        val convertedPlayTime = convertDateToLong(replayInfo.start_time, "yyyy-MM-dd HH:mm:ss") + time
                                        val convertedEndTime = convertDateToLong(replayInfo.end_time, "yyyy-MM-dd HH:mm:ss")

                                        if (convertedEndTime < convertedPlayTime) {
                                            running = false
                                            handler = null
                                            return
                                        }

                                        val indexList = getNewReplayCommentIndex(convertedPlayTime, comments, items.size)
                                        for (index in indexList) {
                                            if (comments.size > index) {
                                                items.add(0, comments[index])
                                                count = items.size
                                                detailAdapter.count = count
                                            }
                                        }
                                    }
                                }
                                detailAdapter.notifyDataSetChanged()

                                handler?.postDelayed(this, 1000)
                            }
                        })
                    }
                },
                // onPause
                {
                    running = false
                    handler = null
                },
                // onStop
                {
                    running = false
                    handler = null
                    time = 0
                    detailAdapter.playtime = time
                    detailAdapter.notifyDataSetChanged()
                },
                // onUpdated
                { newValue ->
                    this.watch = newValue

                    watch?.let { watch ->
                        when (watch.status) {
                            WatchModel.Status.onair -> {
                                updateOnair(true)
                            }
                            WatchModel.Status.standby -> {
                                activity?.let { activity ->
                                    activity.popup("정말 종료하시겠습니까?", "종료하기", {
                                        var newValue = newValue
                                        newValue.status = WatchModel.Status.onair

                                        this.watch = newValue
                                        detailAdapter.watch = newValue
                                        detailAdapter.notifyDataSetChanged()

                                    }, {
                                        updateOnair(false)
                                    })
                                }
                            }
                        }
                    }
                },
                // onKicked
                { id ->
                    kicker.add(id.toLong())

                    watch?.let { watch ->
                        val database = FirebaseDatabase.getInstance()
                        database.getReference("watch").child("${watch.id}").child("kicker")
                            .setValue(kicker)
                    }
                },
                // onReplied
                { message ->
                    messageField.setText(message)
                    showKeyboard(requireActivity(), messageField)
                }
            )
            detailView = view.findViewById(R.id.detailView)
            detailView.layoutManager = LinearLayoutManager(context)
            detailView.adapter = detailAdapter
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

                            watch?.let { watch ->
                                val message = messageField.text.toString()
                                messageField.setText("")
                                hideKeyboard()
                                isLoaded = true
                                CommentLoader.shared.addWatchComment(watch.id, message) { newValue ->

                                    WatchLoader.shared.getCommentCountTotal(watch.id) { total ->
                                        WatchLoader.shared.getCommentCount(watch.id) { count ->

                                            this.total = total
                                            this.count = count
                                            detailAdapter.count = this.count
                                            detailAdapter.notifyDataSetChanged()

                                            isLoaded = false

                                            val database = FirebaseDatabase.getInstance()
                                            database.getReference("watch").child("${watch.id}")
                                                .child("total").setValue(total)
                                            database.getReference("watch").child("${watch.id}")
                                                .child("count").setValue(count)
                                        }
                                    }
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

    override fun fetchModel() {
        super.fetchModel()

        BaseApplication.shared.getSharedPreferences().setWatchTime()

        WatchLoader.shared.getReplayCommentList(
            WatchDetailFragmentArgs.fromBundle(
                requireArguments()
            ).watch.id
        ) { item ->
            this.replayItem = item
        }
    }
}

fun WatchDetailFragment.invalidate() {
    val database = FirebaseDatabase.getInstance()

    watch?.let { watch ->
        running = false
        handler = null
        WatchLoader.shared.updateWatch(watch.id, watch.provider_id, WatchModel.Status.standby, watch.title, watch.title_image_url, watch.content) { newValue ->
            database.getReference("watch").child("${watch.id}").child("onair")
                .setValue(false)

            Handler().postDelayed({
                database.getReference("watch").child("${watch.id}").child("total")
                    .removeEventListener(totalEventListener)
                database.getReference("watch").child("${watch.id}").child("kicker")
                    .removeEventListener(kickerEventListener)
            }, 1000)
        }
    }
}

fun WatchDetailFragment.updateOnair(onair: Boolean) {
    watch?.let { watch ->
        WatchLoader.shared.updateWatch(watch.id, watch.provider_id, if (onair) WatchModel.Status.onair else WatchModel.Status.standby, watch.title, watch.title_image_url, watch.content) { newValue ->
            val database = FirebaseDatabase.getInstance()
            database.getReference("watch").child("${watch.id}").child("onair")
                .setValue(watch.status == WatchModel.Status.onair)
        }
    }
}

fun WatchDetailFragment.getNewReplayCommentIndex(convertedPlayTime: Long, items: MutableList<KommentModel>, lastIndex: Int): MutableList<Int> {
    var resultList = mutableListOf<Int>()

    val maxCount = 10
    var count = 0
    for (i in lastIndex until items.size) {
        val createdTime = convertDateToLong(items[i].created_at, "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        if (convertedPlayTime >= createdTime) {
            resultList.add(i)
        }
        if (++count > maxCount) break
    }
    return resultList
}