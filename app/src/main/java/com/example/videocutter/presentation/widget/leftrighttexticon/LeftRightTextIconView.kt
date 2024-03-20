package com.example.videocutter.presentation.widget.leftrighttexticon

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.baseapp.base.extension.show
import com.example.videocutter.R

class LeftRightTextIconView constructor(
    ctx: Context,
    attrs: AttributeSet?
) : ConstraintLayout(ctx, attrs) {

    //left
    private var ivLeft: ImageView? = null
    private var tvLeft: TextView? = null
    private var icLeft: Drawable? = null
    private var labelLeft: CharSequence? = null

    // right
    private var ivRight: ImageView? = null
    private var tvRight: TextView? = null
    private var icRight: Drawable? = null
    private var labelRight: CharSequence? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.left_right_text_icon_view, this, true)
        initView(attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        ivLeft = findViewById(R.id.ivLeftRightTextIconLeftIcon)
        tvLeft = findViewById(R.id.tvLeftRightTextIconLeftLabel)
        ivRight = findViewById(R.id.ivLeftRightTextIconRightIcon)
        tvRight = findViewById(R.id.tvLeftRightTextIconRightLabel)

        icLeft?.let {
            ivLeft?.show()
            ivLeft?.setImageDrawable(it)
        }

        labelLeft?.let {
            tvLeft?.show()
            tvLeft?.text = it
        }

        icRight?.let {
            ivRight?.show()
            ivRight?.setImageDrawable(it)
        }

        labelRight?.let {
            tvRight?.show()
            tvRight?.text = it
        }
    }

    @SuppressLint("Recycle")
    private fun initView(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.LeftRightTextIconView, 0, 0)

        if (ta.hasValue(R.styleable.LeftRightTextIconView_lrti_ic_left)) {
            icLeft = ta.getDrawable(R.styleable.LeftRightTextIconView_lrti_ic_left)
        }

        if (ta.hasValue(R.styleable.LeftRightTextIconView_lrti_tv_left)) {
            labelLeft = ta.getString(R.styleable.LeftRightTextIconView_lrti_tv_left)
        }

        if (ta.hasValue(R.styleable.LeftRightTextIconView_lrti_ic_right)) {
            icRight = ta.getDrawable(R.styleable.LeftRightTextIconView_lrti_ic_right)
        }

        if (ta.hasValue(R.styleable.LeftRightTextIconView_lrti_tv_right)) {
            labelRight = ta.getString(R.styleable.LeftRightTextIconView_lrti_tv_right)
        }

        ta.recycle()
    }

    fun setTextRight(label: CharSequence) {
        tvRight?.show()
        labelRight = label
        tvRight?.text = label
    }

    fun setTextLeft(label: CharSequence) {
        labelLeft = label
        tvLeft?.text = label
        tvLeft?.show()
    }

    fun showIcLeft(isShow: Boolean) {
        ivLeft?.isVisible = isShow
    }

    fun setIcLeft(drawable: Drawable?){
        ivLeft?.show()
        icLeft = drawable
        ivLeft?.setImageDrawable(drawable)
    }

}
