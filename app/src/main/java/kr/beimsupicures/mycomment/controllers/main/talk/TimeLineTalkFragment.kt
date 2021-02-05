package kr.beimsupicures.mycomment.controllers.main.talk

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.components.fragments.BaseFragment

class TimeLineTalkFragment : BaseFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_time_line_talk, container, false)
    }

}