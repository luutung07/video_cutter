package com.example.videocutter.presentation.widget.tabselect

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.baseapp.base.extension.getAppColor
import com.example.baseapp.base.extension.getAppDrawable
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.videocutter.R
import org.w3c.dom.Text

class TabSelectView constructor(
    ctx: Context,
    attrs: AttributeSet?
) : ConstraintLayout(ctx, attrs) {

    private var tvLeft: TextView? = null
    private var labelLeft: String? = null
    private var onActionLeft: (() -> Unit)? = null

    private var tvRight: TextView? = null
    private var labelRight: String? = null
    private var onActionRight: (() -> Unit)? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.tab_select_layout, this, true)
        initView(attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        tvLeft = findViewById(R.id.tvTabSelectLeft)
        tvRight = findViewById(R.id.tvTabSelectRight)

        labelLeft?.let {
            tvLeft?.text = it
        }

        labelRight?.let {
            tvRight?.text = it
        }

        tvLeft?.setOnSafeClick {
            onActionLeft?.invoke()
            selectLeft()
        }

        tvRight?.setOnSafeClick {
            onActionRight?.invoke()
            selectLeft(false)
        }
    }

    private fun initView(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.TabSelectView, 0, 0)
        if (ta.hasValue(R.styleable.TabSelectView_ts_text_left)) {
            labelLeft = ta.getString(R.styleable.TabSelectView_ts_text_left)
        }
        if (ta.hasValue(R.styleable.TabSelectView_ts_text_right)) {
            labelRight = ta.getString(R.styleable.TabSelectView_ts_text_right)
        }
        ta.recycle()
    }

    private fun selectLeft(isSelectLeft: Boolean = true) {
        if (isSelectLeft) {
            tvLeft?.setTextColor(getAppColor(R.color.white))
            tvLeft?.background = getAppDrawable(R.drawable.shape_bg_color_1_corner_left_4)

            tvRight?.setTextColor(getAppColor(R.color.color_1))
            tvRight?.background = null
        } else {
            tvRight?.setTextColor(getAppColor(R.color.white))
            tvRight?.background = getAppDrawable(R.drawable.shape_bg_color_1_corner_right_4)

            tvLeft?.setTextColor(getAppColor(R.color.color_1))
            tvLeft?.background = null
        }
    }

    fun setLeftLabel(label: String) {
        labelLeft = label
        tvLeft?.text = labelLeft
    }

    fun setRightLabel(label: String) {
        labelRight = label
        tvRight?.text = labelRight
    }

    fun setOnActionLeft(action: () -> Unit) {
        onActionLeft = action
    }

    fun setOnActionRight(action: () -> Unit) {
        onActionRight = action
    }
}
