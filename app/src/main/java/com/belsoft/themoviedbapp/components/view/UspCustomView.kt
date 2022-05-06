package com.belsoft.themoviedbapp.components.view

import android.content.Context
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
    private val imageView = binding.imageView
    private val textView = binding.textView

//    private val imageView: ImageView
//    private val textView: TextView

    init {
//        inflate(context, R.layout.usp_custom_view, this)
//        imageView = findViewById(R.id.icon)
//        textView = findViewById(R.id.caption)

        val attributes = context.obtainStyledAttributes(
            attrs,
            R.styleable.UspCustomView,
            defStyleAttr,
            defStyleRes
        )
        val imageViewWidth =
            attributes.getDimensionPixelSize(R.styleable.UspCustomView_icon_width, -3)
        val imageViewHeight =
            attributes.getDimensionPixelSize(R.styleable.UspCustomView_icon_height, -3)
        setViewDimensionsPx(imageView, imageViewWidth, imageViewHeight)

        attributes.getDrawable(R.styleable.UspCustomView_icon)
            ?.let { imageView.setImageDrawable(it) }
        attributes.getString(R.styleable.UspCustomView_caption_text)?.let { textView.text = it }

        attributes.recycle()
    }

    fun setText(message: String) {
        textView.text = message
    }

    fun setIcon(@DrawableRes resId: Int) {
        imageView.setImageResource(resId)
    }

    fun setIconDimensions(@DimenRes resIdWidth: Int, @DimenRes resIdHeight: Int) {
        resources?.let {
            setViewDimensionsPx(
                imageView,
                it.getDimensionPixelSize(resIdWidth),
                it.getDimensionPixelSize(resIdHeight)
            )
        }
    }

    private fun setViewDimensionsPx(view: View, width: Int, height: Int) {
        if (width <= -3 || height <= -3) return
        val lp = view.layoutParams as LinearLayout.LayoutParams
        view.layoutParams = LinearLayout.LayoutParams(lp).apply {
            this.width = width
            this.height = height
        }
    }
}