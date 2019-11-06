package io.golos.cyber_android.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import io.golos.cyber_android.R


fun Activity.setStatusBarColor(@ColorRes colorId: Int){
    val window = window
    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = ContextCompat.getColor(this, colorId)
}

fun Activity.tintStatusBarIcons(statusBarIsDark: Boolean){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val decor = window.decorView
        if (statusBarIsDark) {
            decor.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            // We want to change tint color to white again.
            // You can also record the flags in advance so that you can turn UI back completely if
            // you have set other flags before, such as translucent or full screen.
            decor.systemUiVisibility = 0
        }
    }
}

fun Activity.setFullScreenMode() {
    val window = window
    window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    window?.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
    window?.statusBarColor = Color.TRANSPARENT
}

fun Activity.clearFullScreenMode() {
    val window = window
    window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    window?.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
    window?.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    window?.statusBarColor = ContextCompat.getColor(this, R.color.white)
}