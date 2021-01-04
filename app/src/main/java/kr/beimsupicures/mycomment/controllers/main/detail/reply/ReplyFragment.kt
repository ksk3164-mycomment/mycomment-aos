package kr.beimsupicures.mycomment.controllers.main.detail.reply

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.OpinionLoader
import kr.beimsupicures.mycomment.api.models.OpinionModel
import kr.beimsupicures.mycomment.api.models.isMe
import kr.beimsupicures.mycomment.components.adapters.OpinionAdapter
import kr.beimsupicures.mycomment.components.adapters.ReplyAdapter
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser
import kr.beimsupicures.mycomment.extensions.hideKeyboard
import kr.beimsupicures.mycomment.extensions.popup

class ReplyFragment : BaseFragment() {

    var validation: Boolean = false
        get() = when {
            messageField.text.isEmpty() -> false
            else -> true
        }

    var opinion: MutableList<OpinionModel> = mutableListOf()
    var reply: MutableList<OpinionModel> = mutableListOf()

    lateinit var opinionView: RecyclerView
    lateinit var opinionAdapter: OpinionAdapter

    lateinit var replyView: RecyclerView
    lateinit var replyAdapter: ReplyAdapter

    lateinit var messageField: EditText
    lateinit var btnSend: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reply, container, false)
    }

    override fun onResume() {
        super.onResume()

        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            opinionAdapter = OpinionAdapter(activity, opinion, true)
            opinionView = view.findViewById(R.id.opinionView)
            opinionView.layoutManager = LinearLayoutManager(context)
            opinionView.adapter = opinionAdapter

            replyAdapter = ReplyAdapter(activity, reply)
            replyView = view.findViewById(R.id.replyView)
            replyView.layoutManager = LinearLayoutManager(context)
            replyView.adapter = replyAdapter

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
                            opinion.firstOrNull()?.let { opinion ->

                                OpinionLoader.shared.addReply(opinion.agenda_id, opinion.id, messageField.text.toString()) { reply ->
                                    this.reply.add(reply)
                                    replyAdapter.notifyDataSetChanged()
                                    messageField.setText("")
                                    hideKeyboard()
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

        val opinionId = ReplyFragmentArgs.fromBundle(requireArguments()).opinionId
        OpinionLoader.shared.getOpinion(opinionId) { opinion ->
            this.opinion = mutableListOf(opinion)
            opinionAdapter.items = this.opinion
            opinionAdapter.notifyDataSetChanged()
        }

        OpinionLoader.shared.getReplyList(opinionId) { reply ->
            this.reply = reply.toMutableList()

            if (opinion.firstOrNull()?.isMe() == true) {
                for (item in this.reply) {
                    OpinionLoader.shared.markAsRead(item.id) {

                    }
                }
            }

            replyAdapter.items = this.reply
            replyAdapter.notifyDataSetChanged()
        }
    }
}