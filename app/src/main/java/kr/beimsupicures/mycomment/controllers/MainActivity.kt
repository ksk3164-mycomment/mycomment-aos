package kr.beimsupicures.mycomment.controllers

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.iid.FirebaseInstanceId
import com.jakewharton.rxbinding3.widget.textChanges
import com.kakao.auth.Session
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.SearchLoader
import kr.beimsupicures.mycomment.api.models.TermModel
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.components.activities.BaseActivity
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.controllers.main.search.SearchTalkFragment
import kr.beimsupicures.mycomment.controllers.main.talk.DramaFeedDetailFragment
import kr.beimsupicures.mycomment.controllers.main.talk.DramaFeedWriteFragment
import kr.beimsupicures.mycomment.controllers.signs.SignInFragment
import kr.beimsupicures.mycomment.controllers.signs.sign
import kr.beimsupicures.mycomment.extensions.alert
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.getUser
import kr.beimsupicures.mycomment.extensions.popup
import java.util.concurrent.TimeUnit

class MainActivity : BaseActivity() {

    val navController: NavController by lazy {
        Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )
    }
    val toolbar: Toolbar by lazy {
        findViewById<Toolbar>(R.id.toolbar)
    }

//    val searchViewConst: ConstraintLayout by lazy {
//        findViewById<ConstraintLayout>(R.id.searchViewConst)
//    }

//    val navView: BottomNavigationView by lazy {
//        findViewById<BottomNavigationView>(R.id.nav_view)
//    }

    var isSearchFragment = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Main", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                task.result?.let { result ->
                    val token = result.token
                    Log.e("fcm", token)
                }
            })
    }

    @SuppressLint("CheckResult")
    override fun loadUI() {
        super.loadUI()
        setContentView(R.layout.activity_main)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            toolbar.titleLabel.text = ""
            toolbar.searchView.visibility = View.GONE
            toolbar.btnBack.visibility = View.VISIBLE
            toolbar.visibility = View.VISIBLE
//            navView.visibility = View.GONE
            toolbar.circleView.visibility = View.GONE
            toolbar.btnExit.visibility = View.GONE
            toolbar.btnSetting.visibility = View.GONE

            when (destination.id) {
                R.id.splashFragment -> {
                    toolbar.visibility = View.GONE
                    toolbar.btnBack.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                    toolbar.searchView.visibility = View.GONE
                }
                R.id.profileFragment -> {
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.signInFragment -> {
                    toolbar.btnBack.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.VISIBLE
                }
                R.id.changePasswordFragment -> {
                    toolbar.titleLabel.text = "비밀번호 변경"
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.termFragment -> {
                    arguments?.let { arguments ->
                        var title = ""

                        (arguments.get("category") as? TermModel.Category)?.let { category ->

                            when (category) {
                                TermModel.Category.guide -> title = "커뮤니티 가이드라인"
                                TermModel.Category.service -> title = "서비스 이용약관"
                                TermModel.Category.privacy -> title = "개인정보 처리방침"
                            }
                            toolbar.titleLabel.text = title
                        }
                    }
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.certificationFragment -> {
                    toolbar.titleLabel.text = "본인인증"
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.forgotNicknameFragment -> {
                    toolbar.titleLabel.text = "아이디 찾기"
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.forgotPasswordFragment -> {
                    toolbar.titleLabel.text = "비밀번호 찾기"
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.signUpFragment -> {
                    toolbar.titleLabel.text = "회원가입"
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                //메인 화면
                R.id.talkFragment -> {
                    isSearchFragment = false
                    toolbar.visibility = View.VISIBLE
                    toolbar.btnBack.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.VISIBLE
                    toolbar.btnClose.visibility = View.GONE
                    toolbar.searchText.visibility = View.VISIBLE
                    toolbar.searchView.visibility = View.VISIBLE
                    toolbar.searchCancel.visibility = View.GONE
                    toolbar.searchField.visibility = View.GONE
                }
                R.id.talkDetailFragment -> {
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.VISIBLE
                    toolbar.btnClose.visibility = View.GONE
                    toolbar.searchText.visibility = View.GONE
                    toolbar.searchView.visibility = View.GONE
                    toolbar.searchCancel.visibility = View.GONE
                    toolbar.searchField.visibility = View.GONE
                    toolbar.btnWrite.visibility = View.GONE
                    toolbar.ivMore.visibility = View.GONE

                }
                //검색하기
                R.id.searchTalkFragment -> {
                    isSearchFragment = true
                    searchText.visibility = View.GONE
                    searchField.visibility = View.VISIBLE
                    toolbar.btnBack.visibility = View.GONE
                    toolbar.btnBookmark.visibility = View.GONE
//                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                    toolbar.searchView.visibility = View.VISIBLE
                    searchCancel.visibility = View.VISIBLE
                }
                R.id.signStep1Fragment -> {
                    toolbar.titleLabel.text = "프로필 입력"
                    toolbar.btnBack.visibility = View.VISIBLE
                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.dramaFeedWriteFragment -> {
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnWrite.visibility = View.VISIBLE
                }
                R.id.dramaFeedDetailFragment -> {
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.ivMore.visibility = View.VISIBLE
                }
            }
        }

        searchCancel.setOnClickListener {
            onBackPressed()
        }


        //드라마 검색하기
        toolbar.searchView.setOnClickListener {
            if (!isSearchFragment) {
                Navigation.findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.action_talkFragment_to_searchTalkFragment)
            }
        }
        toolbar.btnBookmark.setOnClickListener {

            BaseApplication.shared.getSharedPreferences().getUser()?.let {
                Navigation.findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.action_global_bookmarkFragment)

            } ?: run {

                popup("로그인하시겠습니까?", "로그인") {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_signInFragment)
                }
            }
        }
        toolbar.btnProfile.setOnClickListener {

            BaseApplication.shared.getSharedPreferences().getUser()?.let { user ->
                Navigation.findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.action_global_profileFragment, bundleOf(Pair("userId", user.id)))

            } ?: run {

                popup("로그인하시겠습니까?", "로그인") {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_signInFragment)
                }
            }

        }
        toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
        toolbar.btnClose.setOnClickListener {
            onBackPressed()
        }
        toolbar.searchView.searchField.textChanges()
            .map(CharSequence::toString)
            .debounce(500, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { keyword ->
                Log.e("keyword", "${keyword}")

                supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    ?.childFragmentManager?.fragments?.get(0)?.let { fragment ->
                        when (fragment) {
                            is SearchTalkFragment -> {
                                SearchLoader.shared.searchTalk(keyword) { talk ->
                                    fragment.talk = talk
                                    fragment.resultAdapter.items = fragment.talk
                                    fragment.resultAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
            }

        toolbar.btnWrite.setOnClickListener {
            (supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.firstOrNull() as DramaFeedWriteFragment).let { fragment ->
                //제목 미입력
                if (!fragment.title.text.isNotEmpty()) {
                    alert("제목을 입력하세요", "") {}
                } else {
                    //제목입력 , 내용 미입력
                    if (!fragment.editorEmpty) {
                        alert("내용을 입력하세요", "") {}
                    } else {
                        //제목입력, 내용입력
                        popup("", "해당 글을 등록하시겠어요?") {
                            Log.e(
                                "tjdrnr",
                                "제목 = " + fragment.title.text + "내용 = " + fragment.editorText
                            )
                        }
                    }

                }
            }
        }
        toolbar.ivMore.setOnClickListener {
            (supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.firstOrNull() as DramaFeedDetailFragment).let { fragment ->
                val bottomSheet = kr.beimsupicures.mycomment.components.dialogs.BottomSheetDialog()
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
            }
        }

    }


    override fun onBackPressed() {
        navController.currentDestination?.id.let { id ->
            when (id) {
                R.id.splashFragment, R.id.talkFragment-> {
                    moveTaskToBack(true)
                    finishAffinity()
                }
                else -> {
                    super.onBackPressed()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        (supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.firstOrNull() as SignInFragment).let { fragment ->
            fragment.callbackManager.onActivityResult(requestCode, resultCode, data)

            // kakao
            if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
                return
            }

            // google
            if (requestCode == 99) {
                try {
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                        .getResult(ApiException::class.java)?.let { account ->

                            account.email?.let { email ->
                                account.displayName?.let { nickname ->
                                    fragment.sign(email, nickname, UserModel.SocialProvider.google)
                                }
                            }
                        }

                } catch (e: ApiException) {
                    Log.e("TAG", "Google sign in failed", e)
                }
            }
        }
    }
}



