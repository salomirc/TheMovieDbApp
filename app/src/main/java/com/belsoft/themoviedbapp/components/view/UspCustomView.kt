package com.belsoft.themoviedbapp.components.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import com.belsoft.themoviedbapp.R
import com.belsoft.themoviedbapp.databinding.UspCustomViewBinding

class UspCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.uspCustomViewStyle,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding = UspCustomViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

//    private val imageView: ImageView
//    private val textView: TextView

    init {
//        inflate(context, R.layout.usp_custom_view, this)
//        imageView = findViewById(R.id.icon)
//        textView = findViewById(R.id.caption)

        var imageViewWidth = NOT_SET_BY_ATTR
        var imageViewHeight = NOT_SET_BY_ATTR
        var iconDrawable: Drawable? = null
        var captionText: String? = null

        context.obtainStyledAttributes(
            attrs,
            R.styleable.UspCustomView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                imageViewWidth =
                    getDimensionPixelSize(R.styleable.UspCustomView_icon_width, NOT_SET_BY_ATTR)
                imageViewHeight =
                    getDimensionPixelSize(R.styleable.UspCustomView_icon_height, NOT_SET_BY_ATTR)
                iconDrawable = getDrawable(R.styleable.UspCustomView_icon)
                captionText = getString(R.styleable.UspCustomView_caption_text)
            } finally {
                recycle()
            }
        }

        setViewDimensionsPx(binding.imageView, imageViewWidth, imageViewHeight)
        iconDrawable?.let { binding.imageView.setImageDrawable(it) }
        captionText?.let { binding.textView.text = it }
    }

    fun setText(message: String) {
        binding.textView.text = message
    }

    fun setIcon(@DrawableRes resId: Int) {
        binding.imageView.setImageResource(resId)
    }

    fun setIconDimensions(@DimenRes resIdWidth: Int, @DimenRes resIdHeight: Int) {
        resources?.let {
            setViewDimensionsPx(
                binding.imageView,
                it.getDimensionPixelSize(resIdWidth),
                it.getDimensionPixelSize(resIdHeight)
            )
        }
    }

    private fun setViewDimensionsPx(view: View, width: Int, height: Int) {
        if (width <= NOT_SET_BY_ATTR || height <= NOT_SET_BY_ATTR) return
        val lp = view.layoutParams as LinearLayout.LayoutParams
        view.layoutParams = LinearLayout.LayoutParams(lp).apply {
            this.width = width
            this.height = height
        }
    }

    companion object {
        private const val NOT_SET_BY_ATTR = -3
    }
}