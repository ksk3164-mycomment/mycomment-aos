package kr.beimsupicures.mycomment.components.dialogs

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.FeedLoader
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.extensions.*

class BottomSheetDialog : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        view?.let {

        }
        val user = BaseApplication.shared.getSharedPreferences().getUser()
        val feedUserId = BaseApplication.shared.getSharedPreferences().getFeedDetailUserId()
        var feedId = BaseApplication.shared.getSharedPreferences().getFeedId()

        if (user?.id == feedUserId) {

            view?.findViewById<TextView>(R.id.tvReport)?.visibility = View.GONE
            view?.findViewById<LinearLayout>(R.id.linearLayout)?.visibility = View.VISIBLE

        } else {

            view?.findViewById<TextView>(R.id.tvReport)?.visibility = View.VISIBLE
            view?.findViewById<LinearLayout>(R.id.linearLayout)?.visibility = View.GONE

        }


        view?.findViewById<TextView>(R.id.tvReport)?.setOnClickListener {
            dismiss()
        }
        view?.findViewById<TextView>(R.id.tvCancel)?.setOnClickListener {
            dismiss()
        }
        view?.findViewById<TextView>(R.id.tvModify)?.setOnClickListener {
            dismiss()
        }
        view?.findViewById<TextView>(R.id.tvDelete)?.setOnClickListener {
            activity?.popup("해당 글을 삭제하시겠어요?", "삭제 후에는 글을 복구할 수 없습니다.") {
                FeedLoader.shared.deleteFeedDetail(feedId){
                    dismiss()
                    activity?.supportFragmentManager?.popBackStack()
                }
            }
        }
    }
}