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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.jakewharton.rxbinding3.widget.textChanges
import com.kakao.auth.Session
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.loaders.OpinionLoader
import kr.beimsupicures.mycomment.api.loaders.SearchLoader
import kr.beimsupicures.mycomment.api.loaders.WatchLoader
import kr.beimsupicures.mycomment.api.models.TermModel
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.api.models.WatchModel
import kr.beimsupicures.mycomment.components.activities.BaseActivity
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.controllers.main.search.SearchTalkFragment
import kr.beimsupicures.mycomment.controllers.main.search.SearchWatchFragment
import kr.beimsupicures.mycomment.controllers.main.watch.CreateWatchFragment
import kr.beimsupicures.mycomment.controllers.main.watch.WatchDetailFragment
import kr.beimsupicures.mycomment.controllers.main.watch.invalidate
import kr.beimsupicures.mycomment.controllers.signs.SignInFragment
import kr.beimsupicures.mycomment.controllers.signs.sign
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
            toolbar.btnTalkSearch.visibility = View.GONE
            toolbar.btnWatchSearch.visibility = View.GONE
            toolbar.circleView.visibility = View.GONE
            toolbar.btnExit.visibility = View.GONE
            toolbar.btnSetting.visibility = View.GONE

            when (destination.id) {
                R.id.splashFragment -> {
                    toolbar.btnBack.visibility = View.GONE
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.todayFragment -> {
                    toolbar.btnBack.visibility = View.GONE
                    toolbar.btnCalendarSearch.visibility = View.VISIBLE
//                    toolbar.btnBookmark.visibility = View.VISIBLE
                    toolbar.btnProfile.visibility = View.VISIBLE
                    toolbar.btnClose.visibility = View.GONE
//                    navView.visibility = View.VISIBLE

                    OpinionLoader.shared.getUnreadCount { opinion ->
                        toolbar.circleView.visibility =
                            if (opinion.isEmpty()) View.GONE else View.VISIBLE
                    }
                }
                R.id.searchCalendarFragment -> {
                    toolbar.titleLabel.text = "날짜 검색"
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
//                R.id.bookmarkFragment -> {
//                    toolbar.btnCalendarSearch.visibility = View.GONE
////                    toolbar.btnBookmark.visibility = View.GONE
//                    toolbar.btnProfile.visibility = View.GONE
//                    toolbar.btnClose.visibility = View.GONE
//                }
                R.id.profileFragment -> {
//                    toolbar.titleLabel.text = "프로필 보기"
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.detailFragment -> {
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.VISIBLE
                    toolbar.btnProfile.visibility = View.VISIBLE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.bestUserFragment -> {
                    toolbar.titleLabel.text = "Key Opinion Leader"
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.signInFragment -> {
                    toolbar.btnBack.visibility = View.GONE
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.VISIBLE
                }
                R.id.replyFragment -> {
                    toolbar.titleLabel.text = "댓글 보기"
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.historyFragment -> {
                    toolbar.titleLabel.text = "히스토리"
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.changePasswordFragment -> {
                    toolbar.titleLabel.text = "비밀번호 변경"
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
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
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.certificationFragment -> {
                    toolbar.titleLabel.text = "본인인증"
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.forgotNicknameFragment -> {
                    toolbar.titleLabel.text = "아이디 찾기"
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.forgotPasswordFragment -> {
                    toolbar.titleLabel.text = "비밀번호 찾기"
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.signUpFragment -> {
                    toolbar.titleLabel.text = "회원가입"
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                //메인 화면
                R.id.talkFragment -> {
                    isSearchFragment = false
                    toolbar.btnBack.visibility = View.GONE
                    toolbar.btnCalendarSearch.visibility = View.GONE
                    searchCancel.visibility = View.GONE
                    searchField.visibility = View.GONE
//                    toolbar.btnTalkSearch.visibility = View.VISIBLE
//                    toolbar.btnBookmark.visibility = View.VISIBLE
                    toolbar.btnProfile.visibility = View.VISIBLE
                    toolbar.btnClose.visibility = View.GONE
//                    navView.visibility = View.VISIBLE
                    searchText.visibility = View.VISIBLE
                    toolbar.searchView.visibility = View.VISIBLE
                }
                R.id.talkDetailFragment -> {
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.VISIBLE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.watchFragment -> {
                    toolbar.btnBack.visibility = View.GONE
                    toolbar.btnCalendarSearch.visibility = View.GONE
//                    toolbar.btnBookmark.visibility = View.VISIBLE
                    toolbar.btnProfile.visibility = View.VISIBLE
                    toolbar.btnClose.visibility = View.GONE
//                    navView.visibility = View.VISIBLE
                    toolbar.btnWatchSearch.visibility = View.VISIBLE
                }
                R.id.watchDetailFragment -> {

                    arguments?.let { arguments ->
                        (arguments.get("watch") as? WatchModel)?.let { watchModel ->
                            when ((watchModel.owner?.id == BaseApplication.shared.getSharedPreferences()
                                .getUser()?.id)) {
                                true -> {
                                    toolbar.btnExit.visibility = View.VISIBLE
                                    toolbar.btnBack.visibility = View.GONE
                                    toolbar.btnSetting.visibility = View.VISIBLE
                                }

                                false -> {
                                    toolbar.btnExit.visibility = View.GONE
                                    toolbar.btnBack.visibility = View.VISIBLE
                                    toolbar.btnSetting.visibility = View.GONE
                                }
                            }
                        }
                    }

                    toolbar.btnCalendarSearch.visibility = View.GONE
                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.createWatchFragment -> {

                    arguments?.let { arguments ->
                        (arguments.get("viewType") as? CreateWatchFragment.ViewType)?.let { viewType ->
                            when (viewType) {
                                CreateWatchFragment.ViewType.create -> {
                                    toolbar.titleLabel.text = "방만들기"
                                }
                                CreateWatchFragment.ViewType.edit -> {
                                    toolbar.titleLabel.text = "수정하기"
                                }
                            }
                        }
                    }
                    toolbar.btnBack.visibility = View.VISIBLE
                    toolbar.btnCalendarSearch.visibility = View.GONE
                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.suggestFragment -> {
                    toolbar.titleLabel.text = "주제 제안하기"
                    toolbar.btnBack.visibility = View.VISIBLE
                    toolbar.btnCalendarSearch.visibility = View.GONE
                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                //검색하기
                R.id.searchTalkFragment -> {
                    isSearchFragment = true
                    searchText.visibility = View.GONE
                    searchField.visibility = View.VISIBLE
                    toolbar.btnBack.visibility = View.GONE
                    toolbar.btnCalendarSearch.visibility = View.GONE
                    toolbar.btnBookmark.visibility = View.GONE
//                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                    toolbar.searchView.visibility = View.VISIBLE
                    searchCancel.visibility = View.VISIBLE
                }
//                R.id.searchWatchFragment -> {
//                    toolbar.searchView.visibility = View.VISIBLE
//                    toolbar.btnBack.visibility = View.VISIBLE
//                    toolbar.btnCalendarSearch.visibility = View.GONE
////                    toolbar.btnBookmark.visibility = View.GONE
//                    toolbar.btnProfile.visibility = View.GONE
//                    toolbar.btnClose.visibility = View.GONE
//                }
                R.id.signStep1Fragment -> {
                    toolbar.titleLabel.text = "프로필 입력"
                    toolbar.btnBack.visibility = View.VISIBLE
                    toolbar.btnCalendarSearch.visibility = View.GONE
                    toolbar.btnBookmark.visibility = View.GONE
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
            }
        }

        searchCancel.setOnClickListener {
            onBackPressed()
        }


        toolbar.btnCalendarSearch.setOnClickListener {
            Navigation.findNavController(this, R.id.nav_host_fragment)
                .navigate(R.id.action_todayFragment_to_searchCalendarFragment)
        }
        //드라마 검색하기
        toolbar.searchView.setOnClickListener {
            if (!isSearchFragment) {
                Navigation.findNavController(this, R.id.nav_host_fragment)
                    .navigate(R.id.action_talkFragment_to_searchTalkFragment)
            }
        }
        toolbar.btnWatchSearch.setOnClickListener {
            Navigation.findNavController(this, R.id.nav_host_fragment)
                .navigate(R.id.action_watchFragment_to_searchWatchFragment)
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
        toolbar.btnExit.setOnClickListener {
            (supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.firstOrNull() as WatchDetailFragment).let { fragment ->
                fragment.activity?.popup("정말 종료하시겠습니까?", "종료하기") {

                    fragment.watch?.let { watch ->

                        when (watch.owner?.id == BaseApplication.shared.getSharedPreferences()
                            .getUser()?.id) {
                            true -> {
                                WatchLoader.shared.updateWatch(
                                    watch.id,
                                    watch.provider_id,
                                    WatchModel.Status.standby,
                                    watch.title,
                                    watch.title_image_url,
                                    watch.content
                                ) { newValue ->

                                    WatchLoader.shared.resetViewerCount(watch.id) {
                                        val database = FirebaseDatabase.getInstance()
                                        database.getReference("watch").child("${watch.id}")
                                            .child("onair").setValue(false)
                                        database.getReference("watch").child("${watch.id}")
                                            .child("viewer").setValue(0)

                                        fragment.invalidate()
                                        navController.popBackStack(R.id.watchFragment, false)
                                    }
                                }
                            }
                            false -> {
                                fragment.invalidate()
                                navController.popBackStack(R.id.watchFragment, false)
                            }
                        }
                    }
                }
            }
        }
        toolbar.btnBack.setOnClickListener {
            onBackPressed()
        }
        toolbar.btnClose.setOnClickListener {
            onBackPressed()
        }
        toolbar.btnSetting.setOnClickListener {
            val action =
                NavigationDirections.actionGlobalCreateWatchFragment(CreateWatchFragment.ViewType.edit)
            navController.navigate(action)
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
                            is SearchWatchFragment -> {
                                SearchLoader.shared.searchWatch(keyword) { watch ->
                                    fragment.watch = watch
                                    fragment.resultAdapter.items = fragment.watch
                                    fragment.resultAdapter.notifyDataSetChanged()
                                }
                            }
                        }
                    }
            }

//        NavigationUI.setupWithNavController(navView, navController)
    }


    override fun onBackPressed() {
        navController.currentDestination?.id.let { id ->
            when (id) {
                R.id.splashFragment, R.id.todayFragment, R.id.talkFragment, R.id.watchFragment -> {
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
