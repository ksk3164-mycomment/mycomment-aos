package kr.beimsupicures.mycomment.controllers.main.bookmark

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.components.adapters.*
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser

class BookmarkFragment : BaseFragment() {

    var talk: MutableList<TalkModel> = mutableListOf()

    lateinit var talkView: RecyclerView
    lateinit var talkAdapter: TalkGridAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookmark, container, false)
    }

    override fun onResume() {
        super.onResume()
        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            talkAdapter = TalkGridAdapter(activity, talk)
            talkView = view.findViewById(R.id.talkView)
            talkView.layoutManager = GridLayoutManager(context,3)
            talkView.adapter = talkAdapter
            talkView.visibility = View.VISIBLE

        }
    }

    override fun fetchModel() {
        super.fetchModel()

        BaseApplication.shared.getSharedPreferences().getUser()?.let { user ->
            UserLoader.shared.getUserBookmarkTalk { talk ->
                this.talk = talk.toMutableList()
                talkAdapter.items = this.talk
                talkAdapter.notifyDataSetChanged()
            }
        }
    }
}