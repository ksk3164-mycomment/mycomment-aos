package kr.beimsupicures.mycomment.controllers.main.talk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.components.fragments.BaseFragment

class DramaFeedFragment : BaseFragment() {

    lateinit var floatingButton : FloatingActionButton


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_drama_feed, container, false)
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            floatingButton = view.findViewById(R.id.floating_button)

        }
    }
}