package io.golos.cyber_android.ui.shared.utils

import android.text.Editable
import android.text.TextWatcher

/**
 * Simple [TextWatcher] that allows to implement only necessary methods
 */
open class TextWatcherBase : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

}