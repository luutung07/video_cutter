package com.example.videocutter.presentation.widget.headerview

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.baseapp.base.extension.getAppColor
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.baseapp.base.extension.show
import com.example.videocutter.R

class HeaderView constructor(
    ctx: Context,
    attrs: AttributeSet?
) : ConstraintLayout(ctx, attrs) {

    // left
    private var llLeft: LinearLayoutCompat? = null
    private var tvLeft: TextView? = null
    private var icLeft: ImageView? = null
    private var tvLeftSize: Float? = null
    private var labelLeft: String? = null
    private var tvLeftColor: Int? = null
    private var backGroundLeft: Drawable? = null
    private var drawableIcLeft: Drawable? = null
    private var onActionLeft: (() -> Unit)? = null

    // center
    private var tvCenter: TextView? = null
    private var icCenter: ImageView? = null
    private var llCenter: LinearLayoutCompat? = null
    private var labelCenter: String? = null
    private var tvCenterSize: Float? = null
    private var tvCenterColor: Int? = null
    private var backGroundCenter: Drawable? = null
    private var drawableIcCenter: Drawable? = null
    private var onActionCenter: (() -> Unit)? = null

    // right
    private var llRight: LinearLayoutCompat? = null
    private var tvRight: TextView? = null
    private var icRight: ImageView? = null
    private var labelRight: String? = null
    private var tvRightSize: Float? = null
    private var tvRightColor: Int? = null
    private var backGroundRight: Drawable? = null
    private var drawableIcRight: Drawable? = null
    private var onActionRight: (() -> Unit)? = null

    init {
        LayoutInflater.from(ctx).inflate(R.layout.header_view_layout, this, true)
        initView(attrs)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        // left
        llLeft = findViewById(R.id.llHeaderViewLeft)
        tvLeft = findViewById(R.id.tvHeaderViewLeftLabel)
        icLeft = findViewById(R.id.ivHeaderViewLeftIcon)

        // center
        llCenter = findViewById(R.id.llHeaderViewCenter)
        tvCenter = findViewById(R.id.tvHeaderViewCenterLabel)
        icCenter = findViewById(R.id.ivHeaderViewCenterIcon)

        // right
        llRight = findViewById(R.id.llHeaderViewRight)
        tvRight = findViewById(R.id.tvHeaderViewRightLabel)
        icRight = findViewById(R.id.ivHeaderViewRightIcon)

        // ================== set up =================
        //left
        tvLeftSize?.let {
            tvLeft?.setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
        }
        labelLeft?.let {
            tvLeft?.show()
            tvLeft?.setText(it)
        }
        tvLeftColor?.let {
            tvLeft?.setTextColor(it)
        }
        backGroundLeft?.let {
            llLeft?.background = it
        }
        drawableIcLeft?.let {
            icLeft?.show()
            icLeft?.setImageDrawable(it)
        }

        // center
        tvCenterSize?.let {
            tvCenter?.setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
        }
        labelCenter?.let {
            tvCenter?.show()
            tvCenter?.setText(it)
        }
        tvCenterColor?.let {
            tvCenter?.setTextColor(it)
        }
        backGroundCenter?.let {
            llCenter?.background = it
        }
        drawableIcCenter?.let {
            icCenter?.show()
            icCenter?.setImageDrawable(it)
        }

        //right
        tvRightSize?.let {
            tvRight?.setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
        }
        labelRight?.let {
            tvRight?.show()
            tvRight?.setText(it)
        }
        tvRightColor?.let {
            tvRight?.setTextColor(it)
        }
        backGroundRight?.let {
            llRight?.background = it
        }
        drawableIcRight?.let {
            icRight?.show()
            icRight?.setImageDrawable(it)
        }

        // action
        llCenter?.setOnSafeClick {
            onActionCenter?.invoke()
        }

        icLeft?.setOnSafeClick {
            onActionLeft?.invoke()
        }
    }

    private fun initView(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.HeaderView, 0, 0)

        if (ta.hasValue(R.styleable.HeaderView_hv_left_text_size)) {
            tvLeftSize = ta.getDimension(R.styleable.HeaderView_hv_left_text_size, 0f)
        }

        if (ta.hasValue(R.styleable.HeaderView_hv_left_label)) {
            labelLeft = ta.getString(R.styleable.HeaderView_hv_left_label)
        }

        if (ta.hasValue(R.styleable.HeaderView_hv_left_text_color)) {
            tvLeftColor =
                ta.getColor(R.styleable.HeaderView_hv_left_text_color, getAppColor(R.color.black))
        }

        if (ta.hasValue(R.styleable.HeaderView_hv_left_background)) {
            backGroundLeft = ta.getDrawable(R.styleable.HeaderView_hv_left_background)
        }

        if (ta.hasValue(R.styleable.HeaderView_hv_left_icon)) {
            drawableIcLeft = ta.getDrawable(R.styleable.HeaderView_hv_left_icon)
        }

        if (ta.hasValue(R.styleable.HeaderView_hv_center_text_size)) {
            tvCenterSize = ta.getDimension(R.styleable.HeaderView_hv_center_text_size, 0f)
        }

        if (ta.hasValue(R.styleable.HeaderView_hv_center_label)) {
            labelCenter = ta.getString(R.styleable.HeaderView_hv_center_label)
        }

        if (ta.hasValue(R.styleable.HeaderView_hv_center_text_color)) {
            tvCenterColor =
                ta.getColor(R.styleable.HeaderView_hv_center_text_color, getAppColor(R.color.black))
        }

        if (ta.hasValue(R.styleable.HeaderView_hv_center_background)) {
            backGroundCenter = ta.getDrawable(R.styleable.HeaderView_hv_center_background)
        }

        if (ta.hasValue(R.styleable.HeaderView_hv_center_icon)) {
            drawableIcCenter = ta.getDrawable(R.styleable.HeaderView_hv_center_icon)
        }

        if (ta.hasValue(R.styleable.HeaderView_hv_right_text_size)) {
            tvRightSize = ta.getDimension(R.styleable.HeaderView_hv_right_text_size, 0f)
        }

        if (ta.hasValue(R.styleable.HeaderView_hv_right_label)) {
            labelRight = ta.getString(R.styleable.HeaderView_hv_right_label)
        }

        if (ta.hasValue(R.styleable.HeaderView_hv_right_text_color)) {
            tvRightColor =
                ta.getColor(R.styleable.HeaderView_hv_right_text_color, getAppColor(R.color.black))
        }

        if (ta.hasValue(R.styleable.HeaderView_hv_right_background)) {
            backGroundRight = ta.getDrawable(R.styleable.HeaderView_hv_right_background)
        }

        if (ta.hasValue(R.styleable.HeaderView_hv_right_icon)) {
            drawableIcRight = ta.getDrawable(R.styleable.HeaderView_hv_right_icon)
        }

        ta.recycle()
    }

    fun showIcLeft(isShow: Boolean) {
        icLeft?.isVisible = isShow
    }

    fun showTvLeft(isShow: Boolean) {
        tvLeft?.isVisible = isShow
    }

    fun showTvCenter(isShow: Boolean) {
        tvCenter?.isVisible = isShow
    }

    fun showIcCenter(isShow: Boolean) {
        icCenter?.isVisible = isShow
    }

    fun showTvRight(isShow: Boolean) {
        tvRight?.isVisible = isShow
    }

    fun showIcRight(isShow: Boolean) {
        icRight?.isVisible = isVisible
    }

    fun setActionCenter(onAction: () -> Unit) {
        onActionCenter = onAction
    }

    fun setIcCenter(drawable: Drawable?) {
        icCenter?.setImageDrawable(drawable)
    }

    fun setLabelCenter(label: CharSequence) {
        tvCenter?.text = label
    }

    fun setActionLeft(onAction: () -> Unit) {
        onActionLeft = onAction
    }
}
