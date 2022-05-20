package com.rasyidin.storyapp.presentation.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.rasyidin.storyapp.R

class EmailEditText : AppCompatEditText {

    private lateinit var iconEmail: Drawable

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        showHint()
        showIconEmail()
    }

    private fun init() {
        iconEmail = ContextCompat.getDrawable(context, R.drawable.ic_email) as Drawable

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                if (text.isBlank()) {
                    hideError()
                    showHint()
                }

            }

            override fun afterTextChanged(editable: Editable) {
                if (isEmailValid(editable)) hideError() else showError()
            }
        })
    }

    private fun isEmailValid(textEmail: CharSequence): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()
    }

    private fun showHint() {
        hint = context.getString(R.string.email)
    }

    private fun showError() {
        error = context.getString(R.string.error_email)
    }

    private fun hideError() {
        error = null
    }

    private fun showIconEmail() {
        setButtonDrawables(startOfTheText = iconEmail)
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

}