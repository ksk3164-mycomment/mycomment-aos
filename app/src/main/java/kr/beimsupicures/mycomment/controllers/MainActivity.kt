package kr.beimsupicures.mycomment.controllers

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.jakewharton.rxbinding3.widget.textChanges
import com.kakao.auth.Session
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_drama_feed_detail.*
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.AmazonS3Loader
import kr.beimsupicures.mycomment.api.loaders.FeedLoader
import kr.beimsupicures.mycomment.api.loaders.SearchLoader
import kr.beimsupicures.mycomment.api.models.FeedModel
import kr.beimsupicures.mycomment.api.models.TermModel
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.components.activities.BaseActivity
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.dialogs.LoadingDialog
import kr.beimsupicures.mycomment.controllers.main.feed.DramaFeedDetailFragment
import kr.beimsupicures.mycomment.controllers.main.feed.DramaFeedWriteFragment
import kr.beimsupicures.mycomment.controllers.main.search.SearchTalkFragment
import kr.beimsupicures.mycomment.controllers.main.talk.*
import kr.beimsupicures.mycomment.controllers.signs.SignInFragment
import kr.beimsupicures.mycomment.controllers.signs.sign
import kr.beimsupicures.mycomment.extensions.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : BaseActivity() {

    val navController: NavController by lazy {
        Navigation.findNavController(
            this,
            R.id.nav_host_fragment
        )
    }

    lateinit var loadingDialog: LoadingDialog

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
            toolbar.circleView.visibility = View.GONE
            toolbar.btnExit.visibility = View.GONE
            toolbar.btnSetting.visibility = View.GONE



            when (destination.id) {
                R.id.splashFragment -> {

                    toolbar.visibility = View.GONE
                    toolbar.btnBack.visibility = View.GONE
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
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.certificationFragment -> {
                    toolbar.titleLabel.text = "본인인증"
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.forgotNicknameFragment -> {
                    toolbar.titleLabel.text = "아이디 찾기"
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.forgotPasswordFragment -> {
                    toolbar.titleLabel.text = "비밀번호 찾기"
                    toolbar.btnProfile.visibility = View.GONE
                    toolbar.btnClose.visibility = View.GONE
                }
                R.id.signUpFragment -> {
                    toolbar.titleLabel.text = "회원가입"
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
                    searchField.setText("")
                    toolbar.btnBack.visibility = View.GONE
                    toolbar.btnBookmark.visibility = View.GONE
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
                    toolbar.btnWrite.visibility = View.GONE
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

//                                if (!keyword.isNullOrBlank()) {

                                SearchLoader.shared.searchTalk(
                                    keyword.replace(
                                        " ",
                                        ""
                                    )
                                ) { talk ->
                                    fragment.talk = talk
                                    fragment.resultAdapter.items = fragment.talk
                                    fragment.resultAdapter.notifyDataSetChanged()
                                }

//                                }

                            }
                        }
                    }
            }

        toolbar.btnWrite.setOnClickListener {
            BaseApplication.shared.getSharedPreferences().getUser()?.let { user ->
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
                                BaseApplication.shared.getSharedPreferences().getPostTalkId()
                                    ?.let { talk_id ->
                                        var editor = fragment.editorText.toString()

                                        val displayMetrics = this.resources?.displayMetrics
                                        val dpWidth =
                                            displayMetrics!!.widthPixels / displayMetrics.density

                                        var delimiter1 = """<img src=""""
                                        var delimiter2 =
                                            """" alt="" width="${dpWidth.toInt() - 32}">"""

                                        val parts = editor.split(
                                            delimiter1,
                                            delimiter2,
                                            ignoreCase = true
                                        ).toMutableList()

                                        val subparts = parts.filter {
                                            it.startsWith("file") || it.startsWith(
                                                "content"
                                            )
                                        }
                                        Log.e("tjdrnr", "subparts size= " + subparts.size)
                                        //이미지가 없다
                                        if (subparts.isEmpty()) {
                                            FeedLoader.shared.putFeed(
                                                talk_id,
                                                fragment.title.text.toString(),
                                                parts.joinToString("")
                                            ) { feedModel ->
                                                FeedLoader.shared.getFeedList(
                                                    talk_id,
                                                    true,
                                                    0
                                                ) { feedlist ->
                                                    val feedModel: FeedModel? =
                                                        feedlist.find { it.feed_seq == feedModel.feed_seq }

                                                    feedModel?.let { feedModel ->
                                                        BaseApplication.shared.getSharedPreferences()
                                                            .setFeed(
                                                                feedModel
                                                            )
                                                        BaseApplication.shared.getSharedPreferences()
                                                            .setFeedId(
                                                                feedModel.feed_seq
                                                            )

                                                        Navigation.findNavController(
                                                            fragment.requireView()
                                                        )
                                                            .popBackStack(
                                                                R.id.dramaFeedWriteFragment,
                                                                false
                                                            )
                                                        Navigation.findNavController(
                                                            fragment.requireView()
                                                        )
                                                            .popBackStack()
                                                        val action =
                                                            NavigationDirections.actionGlobalDramaFeedDetailFragment()
                                                        Navigation.findNavController(
                                                            this,
                                                            R.id.nav_host_fragment
                                                        )
                                                            .navigate(action)
                                                    }
                                                }
                                            }
                                        } else {
                                            //이미지가 있다
                                            if (subparts.size <= 8) {
                                                //이미지 개수 8개 이하일때
                                                Log.e("tjdrnr", "Substring parts= " + parts)

                                                var urlList = mutableListOf<String>()
                                                for (item in parts.indices) {
                                                    if (parts[item].startsWith("file://")) {
                                                        val name = "${UUID.randomUUID()}"
                                                        parts[item] =
                                                            """<img src ="""" + "https://s3.ap-northeast-2.amazonaws.com/kr.beimsupicures.mycomment/feed/${name}" + """" alt="">"""
                                                        urlList.add("https://s3.ap-northeast-2.amazonaws.com/kr.beimsupicures.mycomment/feed/${name}")
                                                    }
                                                }
                                                Log.e(
                                                    "tjdrnr",
                                                    "parts.toString = " + parts.joinToString("")
                                                )
                                                Log.e("tjdrnr", "urlList= " + urlList)

                                                loadingDialog = LoadingDialog()
                                                loadingDialog.show(supportFragmentManager, "")
                                                for (item in urlList.indices) {
                                                    AmazonS3Loader.shared.uploadImage3(
                                                        "feed",
                                                        subparts[item].toUri(),
                                                        urlList[item].substringAfter("https://s3.ap-northeast-2.amazonaws.com/kr.beimsupicures.mycomment/feed/")
                                                    ) {
                                                        Log.e("tjdrnr", "s3 url = " + it)
                                                        if (item == urlList.size - 1) {
                                                            Log.e("tjdrnr", "마지막")
//                                                        var images =
//                                                            ArrayList<MultipartBody.Part>()

//                                                            for (element in subparts) {
//                                                                val file =
//                                                                    File(element.toUri().path)
//
//                                                                var newfile =
//                                                                    getStreamByteFromImage(file)
//
//                                                                val surveyBody =
//                                                                    RequestBody.create(
//                                                                        MediaType.parse("image/*"),
//                                                                        newfile
//                                                                    )
//
//                                                                images.add(
//                                                                    MultipartBody.Part.createFormData(
//                                                                        "imgs",
//                                                                        URLEncoder.encode(
//                                                                            file.name,
//                                                                            "utf-8"
//                                                                        ),
//                                                                        surveyBody
//                                                                    )
//                                                                )
//                                                            }
//
//                                                            val title =
//                                                                RequestBody.create(
//                                                                    MediaType.parse("text/plain"),
//                                                                    fragment.title.text.toString()
//                                                                )
//                                                            val content =
//                                                                RequestBody.create(
//                                                                    MediaType.parse("text/plain"),
//                                                                    parts.joinToString("")
//                                                                )
//                                                            FeedLoader.shared.postFeed(
//                                                                talk_id, title, content, images
//                                                            ) { feedModel ->
//                                                                FeedLoader.shared.getFeedList(
//                                                                    talk_id,
//                                                                    true,
//                                                                    0
//                                                                ) { feedlist ->
//
//                                                                    val feedModel: FeedModel? =
//                                                                        feedlist.find { it.feed_seq == feedModel.feed_seq }
//
//                                                                    feedModel?.let { feedModel ->
//                                                                        BaseApplication.shared.getSharedPreferences()
//                                                                            .setFeed(
//                                                                                feedModel
//                                                                            )
//                                                                        BaseApplication.shared.getSharedPreferences()
//                                                                            .setFeedId(
//                                                                                feedModel.feed_seq
//                                                                            )
//
//                                                                        Handler().postDelayed({
//
//                                                                            Navigation.findNavController(
//                                                                                fragment.requireView()
//                                                                            )
//                                                                                .popBackStack(
//                                                                                    R.id.dramaFeedWriteFragment,
//                                                                                    false
//                                                                                )
//                                                                            Navigation.findNavController(
//                                                                                fragment.requireView()
//                                                                            )
//                                                                                .popBackStack()
//                                                                            val action =
//                                                                                NavigationDirections.actionGlobalDramaFeedDetailFragment()
//                                                                            Navigation.findNavController(
//                                                                                this,
//                                                                                R.id.nav_host_fragment
//                                                                            )
//                                                                                .navigate(action)
//                                                                            loadingDialog.dismiss()
//                                                                        }, 4000)
//
//                                                                    }
//                                                                }
//                                                            }
                                                            FeedLoader.shared.putFeed(
                                                                talk_id,
                                                                fragment.title.text.toString(),
                                                                parts.joinToString("")
                                                            ) { feedModel ->
                                                                FeedLoader.shared.getFeedList(
                                                                    talk_id,
                                                                    true,
                                                                    0
                                                                ) { feedlist ->
                                                                    val feedModel: FeedModel? =
                                                                        feedlist.find { it.feed_seq == feedModel.feed_seq }

                                                                    feedModel?.let { feedModel ->
                                                                        BaseApplication.shared.getSharedPreferences()
                                                                            .setFeed(
                                                                                feedModel
                                                                            )
                                                                        BaseApplication.shared.getSharedPreferences()
                                                                            .setFeedId(
                                                                                feedModel.feed_seq
                                                                            )
                                                                        Handler().postDelayed({
                                                                            Navigation.findNavController(
                                                                                fragment.requireView()
                                                                            )
                                                                                .popBackStack(
                                                                                    R.id.dramaFeedWriteFragment,
                                                                                    false
                                                                                )
                                                                            Navigation.findNavController(
                                                                                fragment.requireView()
                                                                            )
                                                                                .popBackStack()
                                                                            val action =
                                                                                NavigationDirections.actionGlobalDramaFeedDetailFragment()
                                                                            Navigation.findNavController(
                                                                                this,
                                                                                R.id.nav_host_fragment
                                                                            )
                                                                                .navigate(action)

                                                                            loadingDialog.dismiss()
                                                                        }, 4000)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            } else {
                                                //이미지 개수 9장 이상일때
                                                alert("사진 등록은 최대 8장까지 가능합니다", "") {}
                                            }
                                        }
                                    }
                            }
                        }
                    }
                }
            } ?: run {
                popup("로그인하시겠습니까?", "로그인") {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_signInFragment)
                }
            }

        }
        toolbar.ivMore.setOnClickListener {
            BaseApplication.shared.getSharedPreferences().getUser()?.let { user ->
                (supportFragmentManager.primaryNavigationFragment?.childFragmentManager?.fragments?.firstOrNull() as DramaFeedDetailFragment).let { fragment ->
                    val bottomSheet =
                        kr.beimsupicures.mycomment.components.dialogs.BottomSheetDialog()
                    bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                }

            } ?: run {
                popup("로그인하시겠습니까?", "로그인") {
                    Navigation.findNavController(this, R.id.nav_host_fragment)
                        .navigate(R.id.action_global_signInFragment)
                }
            }

        }
    }

    override fun onBackPressed() {
        navController.currentDestination?.id.let { id ->
            when (id) {
                R.id.splashFragment, R.id.talkFragment -> {
                    moveTaskToBack(true)
                    finishAffinity()
                }
                R.id.dramaFeedWriteFragment -> {
                    popup("페이지를 벗어나면 작성하신 내용이\n저장되지 않습니다.", "작성을 그만하시겠어요?") {
                        super.onBackPressed()
                    }
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
            if (Session.getCurrentSession()
                    .handleActivityResult(requestCode, resultCode, data)
            ) {
                return
            }

            // google
            if (requestCode == 99) {
                try {
                    GoogleSignIn.getSignedInAccountFromIntent(data)
                        .getResult(ApiException::class.java)?.let { account ->

                            account.email?.let { email ->
                                account.displayName?.let { nickname ->
                                    fragment.sign(
                                        email,
                                        nickname,
                                        UserModel.SocialProvider.google
                                    )
                                }
                            }
                        }

                } catch (e: ApiException) {
                    Log.e("TAG", "Google sign in failed", e)
                }
            }
        }
    }

    //this is the byte stream that I upload.
    fun getStreamByteFromImage(imageFile: File): ByteArray? {
        var photoBitmap = BitmapFactory.decodeFile(imageFile.path)
        val stream = ByteArrayOutputStream()
        val imageRotation = getImageRotation(imageFile)
        if (imageRotation != 0) photoBitmap =
            getBitmapRotatedByDegree(photoBitmap, imageRotation)
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream)
        return stream.toByteArray()
    }


    private fun getImageRotation(imageFile: File): Int {
        var exif: ExifInterface? = null
        var exifRotation = 0
        try {
            exif = ExifInterface(imageFile.path)
            exifRotation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return if (exif == null) 0 else exifToDegrees(exifRotation)
    }

    private fun exifToDegrees(rotation: Int): Int {
        if (rotation == ExifInterface.ORIENTATION_ROTATE_90) return 90 else if (rotation == ExifInterface.ORIENTATION_ROTATE_180) return 180 else if (rotation == ExifInterface.ORIENTATION_ROTATE_270) return 270
        return 0
    }

    private fun getBitmapRotatedByDegree(bitmap: Bitmap, rotationDegree: Int): Bitmap {
        val matrix = Matrix()
        matrix.preRotate(rotationDegree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}