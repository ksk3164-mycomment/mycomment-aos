package kr.beimsupicures.mycomment.components.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import kr.beimsupicures.mycomment.api.models.TalkModel
import kr.beimsupicures.mycomment.controllers.main.talk.DramaFeedFragment
import kr.beimsupicures.mycomment.controllers.main.talk.RealTimeTalkFragment
import kr.beimsupicures.mycomment.controllers.main.talk.TimeLineTalkFragment


class DetailPagerAdapter(manager: FragmentManager,var viewModel:TalkModel) : FragmentStatePagerAdapter(manager) {

    var fragmentList: MutableList<Fragment> = arrayListOf()
    var titleList: MutableList<String> = arrayListOf()

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return RealTimeTalkFragment(viewModel)
            1 -> return DramaFeedFragment()
            2 -> return TimeLineTalkFragment()
        }
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return fragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titleList[position]
    }


    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        titleList.add(title)
    }
}