package io.golos.cyber_android.ui.screens.profile.edit.cover

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.golos.cyber4j.model.CyberName
import io.golos.cyber_android.R
import io.golos.cyber_android.serviceLocator
import io.golos.cyber_android.ui.Tags
import io.golos.cyber_android.ui.screens.profile.edit.BaseProfileImageFragment
import io.golos.cyber_android.utils.asEvent
import io.golos.domain.interactors.model.UserMetadataModel
import io.golos.domain.requestmodel.QueryResult
import kotlinx.android.synthetic.main.edit_profile_cover_fragment.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class EditProfileCoverFragment : BaseProfileImageFragment() {

    enum class ImageSource {
        CAMERA, GALLERY
    }

    data class Args(
        val user: CyberName,
        val source: ImageSource
    )

    private lateinit var viewModel: EditProfileCoverViewModel

    private var selectedUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.edit_profile_cover_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupViewModel()
        observeViewModel()

        close.setOnClickListener { requireActivity().finish() }
        post.setOnClickListener { confirm() }
    }

    private fun observeViewModel() {
        viewModel.getFileUploadingStateLiveData.observe(this, Observer {
            when (it) {
                is QueryResult.Error -> onError()
                is QueryResult.Loading -> showLoading()
                is QueryResult.Success -> viewModel.updateCover(it.originalQuery.url)
            }
        })

        viewModel.getMetadataUpdateStateLiveData.asEvent().observe(this, Observer { event ->
            event?.getIfNotHandled()?.let {
                when (it) {
                    is QueryResult.Loading -> showLoading()
                    is QueryResult.Error -> onError()
                    is QueryResult.Success -> onSuccess()
                }
            }
        })

        viewModel.getMetadataLiveData.observe(this, Observer { result ->
            when (result) {
                is QueryResult.Success -> bindProfile(result.originalQuery)
            }
        })
    }

    private fun bindProfile(profile: UserMetadataModel) {
        avatarText.text = profile.username

        if (profile.personal.avatarUrl.isNullOrBlank()) {
            avatar.setImageDrawable(null)
            avatarText.visibility = View.VISIBLE
        } else {
            Glide.with(requireContext())
                .load(profile.personal.avatarUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(avatar)
            avatarText.visibility = View.GONE
        }

        username.text = profile.username

        joined.text = String.format(
            getString(R.string.profile_creation_date_caption_format),
            SimpleDateFormat(
                getString(R.string.profile_creation_date_format),
                Locale.US
            ).format(profile.createdAt)
        )
    }

    private fun onSuccess() {
        hideLoading()
        requireActivity().finish()
    }

    private fun onError() {
        hideLoading()
        Toast.makeText(requireContext(), "uploading image error", Toast.LENGTH_SHORT).show()
    }

    private fun confirm() {
        showLoading()
        val zoomedRect = coverImage.zoomedRect
        selectedUri?.let { uri ->
            viewModel.uploadFile(File(uri.path), zoomedRect.left, zoomedRect.top, zoomedRect.width(), zoomedRect.height())
        }
    }

    override fun onImagePicked(uri: Uri) {
        selectedUri = uri
        Glide.with(requireContext())
            .load(uri)
            .into(coverImage)
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            requireActivity()
                .serviceLocator
                .getViewModelFactoryByCyberName(getArgs().user)
        ).get(EditProfileCoverViewModel::class.java)
    }

    override fun getImageSource() = getArgs().source

    private fun getArgs() = requireContext()
        .serviceLocator
        .moshi
        .adapter(Args::class.java)
        .fromJson(arguments!!.getString(Tags.ARGS)!!)!!

    companion object {
        fun newInstance(serializedArgs: String) =
            EditProfileCoverFragment().apply {
                arguments = Bundle().apply {
                    putString(Tags.ARGS, serializedArgs)
                }
            }
    }
}
