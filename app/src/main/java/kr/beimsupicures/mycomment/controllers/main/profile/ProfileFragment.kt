package kr.beimsupicures.mycomment.controllers.main.profile

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import gun0912.tedimagepicker.builder.TedImagePicker
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.AmazonS3Loader
import kr.beimsupicures.mycomment.api.loaders.UserLoader
import kr.beimsupicures.mycomment.api.models.TermModel
import kr.beimsupicures.mycomment.api.models.UserModel
import kr.beimsupicures.mycomment.api.models.isMe
import kr.beimsupicures.mycomment.api.models.nameOnly
import kr.beimsupicures.mycomment.components.application.BaseApplication
import kr.beimsupicures.mycomment.components.dialogs.IntroDialog
import kr.beimsupicures.mycomment.components.dialogs.NicknameDialog
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.components.fragments.startLoadingUI
import kr.beimsupicures.mycomment.components.fragments.stopLoadingUI
import kr.beimsupicures.mycomment.controllers.main.profile.password.ChangePasswordFragment
import kr.beimsupicures.mycomment.extensions.getRealPathFromURI
import kr.beimsupicures.mycomment.extensions.getSharedPreferences
import kr.beimsupicures.mycomment.extensions.popup
import kr.beimsupicures.mycomment.extensions.reset
import java.io.File


class ProfileFragment : BaseFragment() {

    lateinit var user: UserModel

    lateinit var profileBackgroundView: ImageView
    lateinit var profileView: ImageView

    lateinit var btnProfileBackground: ImageView
    lateinit var btnProfile: TextView
    lateinit var btnHistory: TextView

    lateinit var likeCountLabel: TextView
    lateinit var nameLabel: TextView

    lateinit var nicknameWrapperView: LinearLayout
    lateinit var nicknameLabel: TextView
    lateinit var introLabel: TextView

    lateinit var optionWrapperView: LinearLayout
    lateinit var btnSignOut: TextView
    lateinit var btnChangePassword: TextView
    lateinit var btnGuide: TextView
    lateinit var btnTerm: TextView
    lateinit var btnPrivacy: TextView
    lateinit var btnSecession: TextView

    lateinit var tvAlarmSetting: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onResume() {
        super.onResume()

        fetchModel()
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->

            profileBackgroundView = view.findViewById(R.id.profileBackgroundView)
            profileView = view.findViewById(R.id.profileView)
            btnProfileBackground = view.findViewById(R.id.btnProfileBackground)
            btnProfileBackground.setOnClickListener {

                context?.let { context ->
                    TedImagePicker.with(context).start { uri ->

                        startLoadingUI()
                        AmazonS3Loader.shared.uploadImage("badge", uri) { url ->

                            UserLoader.shared.updateProfileBadgeImage(url) { user ->
                                this.user = user

                                stopLoadingUI()
                                user.profile_badge_image_url?.let { profile_badge_image_url ->
                                    Glide.with(this).load(profile_badge_image_url)
                                        .transform(CenterCrop())
                                        .into(profileBackgroundView)

                                } ?: run {
                                    profileBackgroundView.setImageResource(android.R.color.transparent)
                                }
                            }
                        }
                    }
                }
            }
            btnProfile = view.findViewById(R.id.btnProfile)
            btnProfile.setOnClickListener {

                context?.let { context ->
                    TedImagePicker.with(context).start { uri ->

                        startLoadingUI()
                        AmazonS3Loader.shared.uploadImage("profile", uri) { url ->

                            UserLoader.shared.updateProfileImage(url) { user ->
                                this.user = user

                                stopLoadingUI()
                                user.profile_image_url?.let { profile_image_url ->
                                    Glide.with(this).load(profile_image_url)
                                        .transform(CenterCrop(), CircleCrop())
                                        .into(profileView)

                                } ?: run {
                                    profileView.setImageResource(android.R.color.transparent)
                                }
                            }
                        }
                    }
                }
            }
            btnHistory = view.findViewById(R.id.btnHistory)
            btnHistory.setOnClickListener {
                val action = NavigationDirections.actionGlobalHistoryFragment().setUserId(user.id)
                view.findNavController().navigate(action)
            }
            likeCountLabel = view.findViewById(R.id.bookmarkCountLabel)
            nameLabel = view.findViewById(R.id.nameLabel)
            nicknameWrapperView = view.findViewById(R.id.nicknameWrapperView)
            nicknameLabel = view.findViewById(R.id.nicknameLabel)
            introLabel = view.findViewById(R.id.introLabel)

            introLabel.setOnClickListener { view ->

                when (user.isMe()) {
                    true -> {
                        context?.let { context ->
                            IntroDialog(view.context) { user ->
                                this.user = user
                                introLabel.text = "${user.intro}"

                            }.show()
                        }
                    }
                }
            }

            optionWrapperView = view.findViewById(R.id.optionWrapperView)
            btnSignOut = view.findViewById(R.id.btnSignOut)
            btnSignOut.setOnClickListener { view ->
                UserLoader.shared.signout()
                BaseApplication.shared.getSharedPreferences().reset()
                view.findNavController().navigate(R.id.action_global_splashFragment)
            }
            btnChangePassword = view.findViewById(R.id.btnChangePassword)
            btnChangePassword.setOnClickListener {
                val action = NavigationDirections.actionGlobalChangePasswordFragment()
                    .setCategory(ChangePasswordFragment.Category.change)
                view.findNavController().navigate(action)
            }
            btnGuide = view.findViewById(R.id.btnGuide)
            btnGuide.setOnClickListener {
                val action =
                    NavigationDirections.actionGlobalTermFragment()
                        .setCategory(TermModel.Category.guide)
                view.findNavController().navigate(action)
            }
            btnTerm = view.findViewById(R.id.btnTerm)
            btnTerm.setOnClickListener {
                val action =
                    NavigationDirections.actionGlobalTermFragment()
                        .setCategory(TermModel.Category.service)
                view.findNavController().navigate(action)
            }
            btnPrivacy = view.findViewById(R.id.btnPrivacy)
            btnPrivacy.setOnClickListener {
                val action =
                    NavigationDirections.actionGlobalTermFragment()
                        .setCategory(TermModel.Category.privacy)
                view.findNavController().navigate(action)
            }
            btnSecession = view.findViewById(R.id.btnSecession)
            btnSecession.setOnClickListener {
                activity?.popup("탈퇴하시겠습니까?", "탈퇴하기") {
                    UserLoader.shared.secessionUser {
                        BaseApplication.shared.getSharedPreferences().reset()
                        view.findNavController().navigate(R.id.action_global_splashFragment)
                    }
                }
            }
            tvAlarmSetting = view.findViewById(R.id.tv_alarm_setting)
            var spannableString = SpannableString(tvAlarmSetting.text.toString())
            spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
            tvAlarmSetting.text = spannableString
            tvAlarmSetting.setOnClickListener {
                openNotificationSettings()
            }
        }
    }

    override fun fetchModel() {
        super.fetchModel()

        val userId = ProfileFragmentArgs.fromBundle(requireArguments()).userId

        UserLoader.shared.getUser(userId) { user ->
            this.user = user

            user.profile_badge_image_url?.let { profile_badge_image_url ->
                Glide.with(this).load(profile_badge_image_url)
                    .transform(CenterCrop())
                    .into(profileBackgroundView)

            } ?: run {
                profileBackgroundView.setImageResource(android.R.color.transparent)
            }

            user.profile_image_url?.let { profile_image_url ->
                Glide.with(this).load(profile_image_url)
                    .transform(CenterCrop(), CircleCrop())
                    .into(profileView)

            } ?: run {
                profileView.setImageResource(android.R.color.transparent)
            }

            nameLabel.text = user.nameOnly()
            nicknameLabel.text = user.nickname
            nicknameLabel.setOnClickListener {
                when (user.isMe()) {
                    true -> {
                        context?.let { context ->
                            NicknameDialog(context) { user ->
                                this.user = user
                                nicknameLabel.text = "${user.nickname}"

                            }.show()
                        }
                    }
                }
            }

            user.intro?.let { intro ->
                introLabel.text = intro
            } ?: run {
                introLabel.text = "소개글이 없습니다"
            }

            when (user.isMe()) {

                false -> {
                    btnProfile.visibility = View.GONE
                    btnProfileBackground.visibility = View.GONE
                    optionWrapperView.visibility = View.GONE
                    tvAlarmSetting.visibility = View.GONE
                }

                true -> {
                    btnProfile.visibility = View.VISIBLE
                }
            }
        }

        UserLoader.shared.getUserPickedCommentCount(userId) { count ->
            likeCountLabel.text = "$count"
            likeCountLabel.visibility = if (count == 0) View.GONE else View.VISIBLE
        }
    }

    fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + context?.packageName))
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}