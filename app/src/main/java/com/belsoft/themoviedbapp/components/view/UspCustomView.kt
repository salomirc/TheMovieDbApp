package com.belsoft.themoviedbapp.components.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.belsoft.themoviedbapp.R

class UspCustomView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = R.style.UspCustomViewDefaultStyle
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val imageView: ImageView
    private val textView: TextView

    private val drawableWidth = resources.getDimensionPixelSize(R.dimen.usp_icon_width)
    private val drawableHeight = resources.getDimensionPixelSize(R.dimen.usp_icon_height)

    init {
        inflate(context, R.layout.usp_custom_view, this)

        imageView = findViewById(R.id.icon)
        textView = findViewById(R.id.caption)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.UspCustomView, defStyleAttr, defStyleRes)
        imageView.setImageDrawable(attributes.getDrawable(R.styleable.UspCustomView_icon))
        textView.text = attributes.getString(R.styleable.UspCustomView_caption_text)

        attributes.recycle()
    }

    fun setText(message: String) {
        textView.text = message
    }

    private fun setIcon(@DrawableRes resId: Int) {
        imageView.setImageResource(resId)
    }
}