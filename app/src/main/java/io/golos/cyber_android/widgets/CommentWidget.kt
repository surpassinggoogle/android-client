package io.golos.cyber_android.widgets

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.InputFilter
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import io.golos.cyber_android.R
import io.golos.cyber_android.views.utils.BaseTextWatcher
import io.golos.cyber_android.views.utils.colorizeHashTags
import io.golos.cyber_android.views.utils.colorizeLinks
import kotlinx.android.synthetic.main.view_comment_widget.view.*


class CommentWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    var listener: Listener? = null

    var isSendEnabled: Boolean = false
        set(value) {
            field = value
            sendButton.isEnabled = value
            sendButton.alpha = if (sendButton.isEnabled) 1f else 0.3f
        }

    init {
        inflate(context, R.layout.view_comment_widget, this)

        galleryButton.setOnClickListener { listener?.onGalleryClick() }
        sendButton.setOnClickListener { listener?.onSendClick(comment.text.toString()) }

        comment.addTextChangedListener(object : BaseTextWatcher() {
            override fun afterTextChanged(s: Editable?) {
                super.afterTextChanged(s)
                listener?.onCommentChanged(s.toString())
                s?.colorizeHashTags()
                s?.colorizeLinks()
            }
        })
    }

    /**
     * Clears comment text. Typically needs to be called when comment was sent successfully
     */
    fun clearText() {
        userName = null
        comment.setText("")
    }

    private var userName: String? = null

    fun setUserToReply(userName: String?) {
        val prevUserName = this.userName
        this.userName = userName
        //if there is previous username then remove it
        if (prevUserName != null && comment.text?.startsWith(prevUserName) == true)
            comment.setText(comment.text?.replaceRange(0, "$prevUserName ".length, ""))

        if (userName != null) {
            if (comment.text?.startsWith(userName) == false)
                addUserName(userName)
            comment.filters = arrayOf(InputFilter { source, _, _, dest, _, _ ->
                if (dest.length > source.length) {
                    if (dest.toString().compareTo(userName) == 0) {
                        listener?.onUserNameCleared()
                        return@InputFilter ""
                    }
                }
                return@InputFilter source
            })
            showKeyboard()
        }
    }

    private fun addUserName(userName: String) {
        val newText = SpannableStringBuilder("$userName ${comment.text}")
        newText.setSpan(StyleSpan(Typeface.BOLD), 0, userName.length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
        comment.text = newText
    }

    private fun showKeyboard() {
        comment.requestFocus()
        (context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(
            comment,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    interface Listener {
        fun onSendClick(text: String)

        fun onGalleryClick()

        fun onUserNameCleared()

        fun onCommentChanged(text: String)
    }
}