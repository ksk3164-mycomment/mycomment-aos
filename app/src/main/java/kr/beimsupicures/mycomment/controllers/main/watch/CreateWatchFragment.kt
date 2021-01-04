package kr.beimsupicures.mycomment.controllers.main.watch

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import gun0912.tedimagepicker.builder.TedImagePicker
import kr.beimsupicures.mycomment.NavigationDirections
import kr.beimsupicures.mycomment.R
import kr.beimsupicures.mycomment.api.AmazonS3Loader
import kr.beimsupicures.mycomment.api.loaders.WatchLoader
import kr.beimsupicures.mycomment.api.models.ProviderModel
import kr.beimsupicures.mycomment.api.models.WatchModel
import kr.beimsupicures.mycomment.components.fragments.BaseFragment
import kr.beimsupicures.mycomment.components.fragments.startLoadingUI
import kr.beimsupicures.mycomment.components.fragments.stopLoadingUI
import kr.beimsupicures.mycomment.extensions.afterTextChanged
import kr.beimsupicures.mycomment.extensions.alert
import stfalcon.universalpickerdialog.UniversalPickerDialog
import java.io.File


class CreateWatchFragment : BaseFragment() {

    enum class ViewType {
        create, edit
    }

    val validation: Boolean
        get() {
            if (titleField.text.isNullOrEmpty() || titleField.text.isNullOrBlank()) {
                return false
            }
            if (contentField.text.isNullOrEmpty() || titleField.text.isNullOrBlank()) {
                return false
            }
            if (categoryField.text.isNullOrEmpty() || titleField.text.isNullOrBlank()) {
                return false
            }
            if (!selected) {
                return false
            }

            return true
        }

    var viewType: ViewType = ViewType.create
    var selected: Boolean = false
    var provider: MutableList<ProviderModel> = mutableListOf()

    var data: ArrayList<String> = arrayListOf()
    var uri: Uri? = null
    var watch: WatchModel? = null

    lateinit var titleField: EditText
    lateinit var contentField: EditText
    lateinit var categoryField: EditText
    lateinit var imageView: ImageView
    lateinit var btnConfirm: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_watch, container, false)
    }

    override fun loadModel() {
        super.loadModel()

        viewType = CreateWatchFragmentArgs.fromBundle(requireArguments()).viewType
    }

    override fun loadUI() {
        super.loadUI()

        view?.let { view ->
            titleField = view.findViewById(R.id.titleField)
            titleField.afterTextChanged {
                validateUI()
            }
            contentField = view.findViewById(R.id.contentField)
            contentField.afterTextChanged {
                validateUI()
            }
            categoryField = view.findViewById(R.id.categoryField)
            categoryField.setOnClickListener {
                UniversalPickerDialog.Builder(context)
                    .setTitle("카테고리 선택")
                    .setListener { selectedValues, key ->
                        categoryField.setText("${provider[selectedValues[0]].name}")
                        validateUI()
                    }
                    .setInputs(UniversalPickerDialog.Input(0, provider.map { it.name }.toCollection(ArrayList())))
                    .show()
            }
            categoryField.afterTextChanged {
                validateUI()
            }
            imageView = view.findViewById(R.id.imageView)
            imageView.setOnClickListener {

                context?.let { context ->
                    TedImagePicker.with(context).start { uri ->

                        this.uri = uri
                        selected = true
                        Glide.with(context).load(uri).into(imageView)

                        validateUI()
                    }
                }
            }
            btnConfirm = view.findViewById(R.id.btnConfirm)
            btnConfirm.setOnClickListener {
                if (validation) {
                    startLoadingUI()

                    when (viewType) {

                        ViewType.create -> {

                            uri?.let { uri ->
                                AmazonS3Loader.shared.uploadImage("watch", uri) { title_image_url ->
                                    provider.filter { it.name == categoryField.text.toString() }.firstOrNull()?.id?.let { provider_id ->

                                        WatchLoader.shared.createWatch(provider_id, titleField.text.toString(), title_image_url, contentField.text.toString()) { watch ->
                                            stopLoadingUI()
                                            activity?.alert("방이 생성되었습니다.", "생성완료", {

                                                val action = NavigationDirections.actionGlobalWatchDetailFragment(watch)
                                                view.findNavController().navigate(action)
                                            })
                                        }
                                    }
                                }

                            } ?: run {
                                provider.filter { it.name == categoryField.text.toString() }.firstOrNull()?.id?.let { provider_id ->
                                    watch?.let { watch ->

                                        WatchLoader.shared.updateWatch(watch.id, provider_id, WatchModel.Status.standby, titleField.text.toString(), watch.title_image_url, contentField.text.toString()) { watch ->
                                            stopLoadingUI()
                                            activity?.alert("방이 생성되었습니다.", "생성완료", {

                                                val action = NavigationDirections.actionGlobalWatchDetailFragment(watch)
                                                view.findNavController().navigate(action)
                                            })
                                        }
                                    }
                                }
                            }
                        }
                        ViewType.edit -> {

                            uri?.let { uri ->
                                watch?.let { watch ->

                                    AmazonS3Loader.shared.uploadImage("watch", uri) { title_image_url ->
                                        provider.filter { it.name == categoryField.text.toString() }.firstOrNull()?.id?.let { provider_id ->

                                            WatchLoader.shared.updateWatch(watch.id, provider_id, watch.status, titleField.text.toString(), title_image_url, contentField.text.toString()) { watch ->
                                                stopLoadingUI()
                                                activity?.alert("방이 수정되었습니다.", "수정완료", {

                                                    findNavController().previousBackStackEntry?.savedStateHandle?.set("watch", watch)
                                                    findNavController().popBackStack(R.id.watchDetailFragment, false)
                                                })
                                            }
                                        }
                                    }
                                }

                            } ?: run {
                                provider.filter { it.name == categoryField.text.toString() }.firstOrNull()?.id?.let { provider_id ->
                                    watch?.let { watch ->

                                        WatchLoader.shared.updateWatch(watch.id, provider_id, watch.status, titleField.text.toString(), watch.title_image_url, contentField.text.toString()) { watch ->
                                            stopLoadingUI()
                                            activity?.alert("방이 수정되었습니다.", "수정완료", {

                                                findNavController().previousBackStackEntry?.savedStateHandle?.set("watch", watch)
                                                findNavController().popBackStack(R.id.watchDetailFragment, false)
                                            })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            when (viewType) {
                ViewType.edit -> {
                    btnConfirm.text = "수정하기"
                }
            }

            fetchModel()
            validateUI()
        }
    }

    fun validateUI() {
        context?.let { context ->
            when (validation) {
                true -> {
                    btnConfirm.background =
                        ContextCompat.getDrawable(context, R.drawable.bg_dialog_button_enable)
                }

                false -> {
                    btnConfirm.background =
                        ContextCompat.getDrawable(context, R.drawable.bg_dialog_button_disable)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun fetchModel() {
        super.fetchModel()

        WatchLoader.shared.getProviderList { provider ->
            this.provider = provider

            WatchLoader.shared.getWatch { watch ->
                watch?.let { watch ->
                    this.watch = watch

                    titleField.setText(watch.title)
                    contentField.setText(watch.content)

                    provider.filter { it.id == watch.provider_id }.firstOrNull()?.let { category ->
                        categoryField.setText(category.name)
                    }

                    context?.let { context ->
                        Glide.with(context).load(watch.title_image_url).into(imageView)
                        selected = true

                        validateUI()
                    }
                }
            }
        }
    }
}
