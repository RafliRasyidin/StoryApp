package com.rasyidin.storyapp.presentation.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.rasyidin.storyapp.R

class PasswordEditText : AppCompatEditText, View.OnTouchListener {

    private lateinit var eyeButton: Drawable
    private lateinit var iconPassword: Drawable

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
        showImageDrawables()
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val eyeButtonStart: Float
            val eyeButtonEnd: Float
            var isEyeButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                eyeButtonEnd = (eyeButton.intrinsicWidth + paddingStart).toFloat()
                if (event.x < eyeButtonEnd) {
                    isEyeButtonClicked = true
                }
            } else {
                eyeButtonStart = (width - paddingEnd - eyeButton.intrinsicWidth).toFloat()
                if (event.x > eyeButtonStart) {
                    isEyeButtonClicked = true
                }
            }

            if (isEyeButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        transformationMethod =
                            if (transformationMethod == HideReturnsTransformationMethod.getInstance()) {
                                PasswordTransformationMethod.getInstance()
                            } else {
                                HideReturnsTransformationMethod.getInstance()
                            }
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        transformationMethod =
                            if (transformationMethod == HideReturnsTransformationMethod.getInstance()) {
                                PasswordTransformationMethod.getInstance()
                            } else {
                                HideReturnsTransformationMethod.getInstance()
                            }
                        return true
                    }
                    else -> return false
                }
            }
        }
        return false
    }

    private fun init() {
        eyeButton = ContextCompat.getDrawable(context, R.drawable.ic_show) as Drawable
        iconPassword = ContextCompat.getDrawable(context, R.drawable.ic_padlock) as Drawable

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                text: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
                when {
                    text.isBlank() -> {
                        hideError()
                        showHint()
                    }
                    text.length < 6 -> showError()
                    text.length > 6 -> hideError()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
    }

    private fun showHint() {
        hint = context.getString(R.string.hint_et_password)
    }

    private fun showError() {
        error = context.getString(R.string.error_password)
    }

    private fun hideError() {
        error = null
    }

    private fun showImageDrawables() {
        setButtonDrawables(startOfTheText = iconPassword, endOfTheText = eyeButton)
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