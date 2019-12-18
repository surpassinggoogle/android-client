package io.golos.cyber_android.ui.common.glide

import android.content.Context
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import io.golos.cyber_android.R
import io.golos.cyber_android.ui.common.glide.transformations.GradientTransformation
import io.golos.cyber_android.ui.common.glide.transformations.PercentageRoundVectorFrameTransformation

fun ImageView.loadAvatar(avatarUrl: String?) = this.load(avatarUrl, R.drawable.ic_empty_user)

fun ImageView.loadCommunity(communityUrl: String?) = this.load(communityUrl, R.drawable.ic_group_temporary)

fun ImageView.loadLeader(url: String?, percentage: Float) =
    Glide
        .with(this)
        .load(if (url.isNullOrEmpty()) "file:///android_asset/empty_user.webp" else url)
        .transform(
            CircleCrop(),
            PercentageRoundVectorFrameTransformation(
                this.context.applicationContext,
                0.8f,
                percentage,
                R.color.avatar_frame,
                R.drawable.ic_avatar_frame
            )
        )
        .fallback(R.drawable.ic_empty_user)
        .error(R.drawable.ic_empty_user)
        .into(this)

fun ImageView.loadCover(url: String?) {
    val urlToLoad = if (url.isNullOrEmpty()) "file:///android_asset/bcg_blue.webp" else url

    Glide
        .with(this)
        .load(urlToLoad)
        .transform(
            CenterCrop(),
            GradientTransformation(
                this.context.applicationContext,
                R.color.cover_gradient_start_color,
                R.color.cover_gradient_end_color
            )
        )
        .into(this)
}

fun ImageView.load(url: String?, @DrawableRes defaultRes: Int) {
    Glide
        .with(this)
        .load(if (url.isNullOrEmpty()) "file:///android_asset/empty_user.webp" else url)
        .apply(RequestOptions.circleCropTransform())
        .fallback(defaultRes)
        .error(defaultRes)
        .into(this)
}

fun ImageView.loadCommentAttachment(url: String?) {
    Glide.with(context)
        .load(url.orEmpty())
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}

fun ImageView.release() {
    Glide.with(this).clear(this)
}

fun Target<*>.clear(context: Context) = Glide.with(context).clear(this)