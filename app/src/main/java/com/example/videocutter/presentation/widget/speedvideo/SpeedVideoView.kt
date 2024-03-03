package com.example.videocutter.presentation.widget.speedvideo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.baseapp.base.extension.getAppColor
import com.example.baseapp.base.extension.getAppDimension
import com.example.baseapp.base.extension.gone
import com.example.baseapp.base.extension.hide
import com.example.baseapp.base.extension.setOnSafeClick
import com.example.baseapp.base.extension.show
import com.example.videocutter.R
import kotlin.math.abs

class SpeedVideoView constructor(
    ctx: Context,
    attributeSet: AttributeSet?
) : ConstraintLayout(ctx, attributeSet) {

    var listener: ISpeedListener? = null

    private val TAG = "SpeedVideo"
    private var newParamsIVSelect: MarginLayoutParams? = null

    // khởi tạo
    private var flRoot: FrameLayout? = null

    private var ivSelect: ImageView? = null
    private var ivChose0Dot75X: ImageView? = null
    private var ivChose0Dot5X: ImageView? = null
    private var ivChose0Dot25X: ImageView? = null
    private var ivChose1X: ImageView? = null
    private var ivChose2X: ImageView? = null
    private var ivChose3X: ImageView? = null
    private var ivChose4X: ImageView? = null

    private var vUnSelect0At75X: View? = null
    private var vUnSelect0At5X: View? = null
    private var vUnSelect0At25X: View? = null
    private var vUnSelect1X: View? = null
    private var vUnSelect2X: View? = null
    private var vUnSelect3X: View? = null
    private var vUnSelect4X: View? = null

    private var tvUnSelect0At75X: TextView? = null
    private var tvUnSelect0At5X: TextView? = null
    private var tvUnSelect0At25X: TextView? = null
    private var tvUnSelect1X: TextView? = null
    private var tvUnSelect2X: TextView? = null
    private var tvUnSelect3X: TextView? = null
    private var tvUnSelect4X: TextView? = null

    // lấy tọa độ X của view
    private var flCoordinateX = 0

    private var ivSelectCoordinateX = 0
    private var ivNavigationCoordinateX = 0

    private var vUnSelect0At75CoordinateX = 0
    private var vUnSelect0At5CoordinateX = 0
    private var vUnSelect0At25CoordinateX = 0
    private var vUnSelect1CoordinateX = 0
    private var vUnSelect2CoordinateX = 0
    private var vUnSelect3CoordinateX = 0
    private var vUnSelect4CoordinateX = 0

    // view click
    private var vClickSpeedVideo0_75X: View? = null
    private var vClickSpeedVideo0_5X: View? = null
    private var vClickSpeedVideo0_25X: View? = null
    private var vClickSpeedVideo1X: View? = null
    private var vClickSpeedVideo2X: View? = null
    private var vClickSpeedVideo3X: View? = null
    private var vClickSpeedVideo4X: View? = null

    // tọa độ điểm chạm đầu tiên
    private var coordinateXFirst = 0f

    // margin left ban đầu của iv select
    private var originMarginIvSelect = 0

    // tạo biến để thay đổi trạng thái
    private var viewOld: View? = null
    private var textViewOld: TextView? = null
    private var ivOld: ImageView? = null

    private var widthParent = 0

    //khoảng cách giữa 2 speed
    private var distanceSpeed = 0

    private var oldSpeedType: SPEED_TYPE? = null

    private var isInit = true

    init {
        LayoutInflater.from(ctx).inflate(R.layout.speed_video_layout, this, true)
        mapView()
        initViewOrigin()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //12f độ dài của view chấm tròn trắng : vUnSelect (speed) X
        widthParent = (MeasureSpec.getSize(widthMeasureSpec) - 12f).toInt()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        flCoordinateX = getCoordinateXView(flRoot as View)

        ivSelectCoordinateX = getCoordinateXView(ivSelect as View)
        ivNavigationCoordinateX = getCoordinateXView(ivChose0Dot75X as View)

        vUnSelect0At75CoordinateX = getCoordinateXView(vUnSelect0At75X as View)
        vUnSelect0At5CoordinateX = getCoordinateXView(vUnSelect0At5X as View)
        vUnSelect0At25CoordinateX = getCoordinateXView(vUnSelect0At25X as View)
        vUnSelect1CoordinateX = getCoordinateXView(vUnSelect1X as View)
        vUnSelect2CoordinateX = getCoordinateXView(vUnSelect2X as View)
        vUnSelect3CoordinateX = getCoordinateXView(vUnSelect3X as View)
        vUnSelect4CoordinateX = getCoordinateXView(vUnSelect4X as View)

        distanceSpeed = abs(vUnSelect4CoordinateX - vUnSelect3CoordinateX)
        eventView()
    }

    private fun eventView() {
        dragSelectSpeed()
        selectSpeed()
        if (oldSpeedType != null && isInit) {
            moveOldSpeed(oldSpeedType!!)
            isInit = false
        }
    }

    private fun initViewOrigin() {
        vUnSelect1X?.isVisible = false
        tvUnSelect1X?.apply {
            setTextColor(getAppColor(R.color.color_purple))
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getAppDimension(com.example.library_base.R.dimen.dimen_14)
            )
        }
        ivChose1X?.isVisible = true
        viewOld = vUnSelect1X
        textViewOld = tvUnSelect1X
        ivOld = ivChose1X
    }

    private fun mapView() {
        flRoot = findViewById(R.id.imSpeedVideoBackground)

        ivSelect = findViewById(R.id.ivSpeedVideoSelected)
        ivChose0Dot75X = findViewById(R.id.ivSpeedVideoChose0_75X)
        ivChose0Dot5X = findViewById(R.id.ivSpeedVideoChose0_5X)
        ivChose0Dot25X = findViewById(R.id.ivSpeedVideoChose0_25X)
        ivChose1X = findViewById(R.id.ivSpeedVideoChose1X)
        ivChose2X = findViewById(R.id.ivSpeedVideoChose2X)
        ivChose3X = findViewById(R.id.ivSpeedVideoChose3X)
        ivChose4X = findViewById(R.id.ivSpeedVideoChose4X)

        vUnSelect0At75X = findViewById(R.id.vSpeedVideo0_75X)
        vUnSelect0At5X = findViewById(R.id.vSpeedVideo0_5X)
        vUnSelect0At25X = findViewById(R.id.vSpeedVideo0_25X)
        vUnSelect1X = findViewById(R.id.vSpeedVideo1X)
        vUnSelect2X = findViewById(R.id.vSpeedVideo2X)
        vUnSelect3X = findViewById(R.id.vSpeedVideo3X)
        vUnSelect4X = findViewById(R.id.vSpeedVideo4X)

        tvUnSelect0At75X = findViewById(R.id.tvSpeedVideo0_75X)
        tvUnSelect0At5X = findViewById(R.id.tvSpeedVideo0_5X)
        tvUnSelect0At25X = findViewById(R.id.tvSpeedVideo0_25X)
        tvUnSelect1X = findViewById(R.id.tvSpeedVideo1X)
        tvUnSelect2X = findViewById(R.id.tvSpeedVideo2X)
        tvUnSelect3X = findViewById(R.id.tvSpeedVideo3X)
        tvUnSelect4X = findViewById(R.id.tvSpeedVideo4X)

        // view click
        vClickSpeedVideo0_75X = findViewById(R.id.vSpeedVideoClick0_75X)
        vClickSpeedVideo0_5X = findViewById(R.id.vSpeedVideoClick0_5X)
        vClickSpeedVideo0_25X = findViewById(R.id.vSpeedVideoClick0_25X)
        vClickSpeedVideo1X = findViewById(R.id.vSpeedVideoClick1X)
        vClickSpeedVideo2X = findViewById(R.id.vSpeedVideoClick2X)
        vClickSpeedVideo3X = findViewById(R.id.vSpeedVideoClick3X)
        vClickSpeedVideo4X = findViewById(R.id.vSpeedVideoClick4X)

        newParamsIVSelect = ivSelect?.layoutParams as MarginLayoutParams
    }

    private fun getCoordinateXView(view: View): Int {
        val location = IntArray(2)
        view.getLocationOnScreen(location)
        return location[0]
    }

    private fun selectSpeed0At75X() {
        setEvent0At75X()
        if (vUnSelect0At75CoordinateX != 0) {
            newParamsIVSelect?.leftMargin = vUnSelect0At75CoordinateX - widthParent / 2
        }
        ivSelect?.layoutParams = newParamsIVSelect
        listener?.onSpeedChange(SPEED_TYPE.SPEED_0_75)
    }

    private fun selectSpeed0At5X() {
        setEvent0At5X()
        if (vUnSelect0At5CoordinateX != 0) {
            newParamsIVSelect?.leftMargin = vUnSelect0At5CoordinateX - widthParent / 2
        }
        ivSelect?.layoutParams = newParamsIVSelect
        listener?.onSpeedChange(SPEED_TYPE.SPEED_0_5)
    }

    private fun selectSpeed0At25X() {
        setEvent0At25X()
        if (vUnSelect0At25CoordinateX != 0) {
            newParamsIVSelect?.leftMargin = vUnSelect0At25CoordinateX - widthParent / 2
        }
        ivSelect?.layoutParams = newParamsIVSelect
        listener?.onSpeedChange(SPEED_TYPE.SPEED_0_25)
    }

    private fun selectSpeed1X() {
        setEvent1X()
        if (vUnSelect1CoordinateX != 0) {
            newParamsIVSelect?.leftMargin = vUnSelect1CoordinateX - widthParent / 2
        }
        ivSelect?.layoutParams = newParamsIVSelect
        listener?.onSpeedChange(SPEED_TYPE.SPEED_1)
    }

    private fun selectSpeed2X() {
        setEvent2X()
        if (vUnSelect2CoordinateX != 0) {
            newParamsIVSelect?.leftMargin = vUnSelect2CoordinateX - widthParent / 2
        }
        ivSelect?.layoutParams = newParamsIVSelect
        listener?.onSpeedChange(SPEED_TYPE.SPEED_2)
    }

    private fun selectSpeed3X() {
        setEvent3X()
        if (vUnSelect3CoordinateX != 0) {
            newParamsIVSelect?.leftMargin = vUnSelect3CoordinateX - widthParent / 2
        }
        ivSelect?.layoutParams = newParamsIVSelect
        listener?.onSpeedChange(SPEED_TYPE.SPEED_3)
    }

    private fun selectSpeed4X() {
        setEvent4X()
        if (vUnSelect4CoordinateX != 0) {
            newParamsIVSelect?.leftMargin = vUnSelect4CoordinateX - widthParent / 2
        }
        ivSelect?.layoutParams = newParamsIVSelect
        listener?.onSpeedChange(SPEED_TYPE.SPEED_4)
    }

    private fun moveOldSpeed(type: SPEED_TYPE) {
        when (type) {
            SPEED_TYPE.SPEED_0_75 -> {
                selectSpeed0At75X()
            }

            SPEED_TYPE.SPEED_0_5 -> {
                selectSpeed0At5X()
            }

            SPEED_TYPE.SPEED_0_25 -> {
                selectSpeed0At25X()
            }

            SPEED_TYPE.SPEED_1 -> {
                selectSpeed1X()
            }

            SPEED_TYPE.SPEED_2 -> {
                selectSpeed2X()
            }

            SPEED_TYPE.SPEED_3 -> {
                selectSpeed3X()
            }

            SPEED_TYPE.SPEED_4 -> {
                selectSpeed4X()
            }
        }
    }

    private fun selectSpeed() {
        vClickSpeedVideo0_75X?.setOnSafeClick {
            selectSpeed0At75X()
        }

        vClickSpeedVideo0_5X?.setOnSafeClick {
            selectSpeed0At5X()
        }

        vClickSpeedVideo0_25X?.setOnSafeClick {
            selectSpeed0At25X()
        }

        vClickSpeedVideo1X?.setOnSafeClick {
            selectSpeed1X()
        }

        vClickSpeedVideo2X?.setOnSafeClick {
            selectSpeed2X()
        }

        vClickSpeedVideo3X?.setOnSafeClick {
            selectSpeed3X()
        }

        vClickSpeedVideo4X?.setOnSafeClick {
            selectSpeed4X()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun dragSelectSpeed() {

        ivSelect?.setOnTouchListener { iv, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    originMarginIvSelect = (iv.layoutParams as MarginLayoutParams).leftMargin
                    coordinateXFirst = event.rawX
                }

                MotionEvent.ACTION_MOVE -> {
                    val coordinateXSecond = event.rawX - coordinateXFirst

                    var expectedMarginLeft = originMarginIvSelect + coordinateXSecond

                    when {
                        vUnSelect0At75CoordinateX != 0 && expectedMarginLeft < vUnSelect0At75CoordinateX - widthParent / 2 -> {
                            expectedMarginLeft =
                                (vUnSelect0At75CoordinateX - widthParent / 2).toFloat()
                        }

                        vUnSelect4CoordinateX != 0 && expectedMarginLeft >= vUnSelect4CoordinateX - widthParent / 2 -> {
                            expectedMarginLeft = (vUnSelect4CoordinateX - widthParent / 2).toFloat()
                        }
                    }

                    newParamsIVSelect?.leftMargin = expectedMarginLeft.toInt()
                    iv?.layoutParams = newParamsIVSelect

                    when {
                        event.rawX <= vUnSelect0At75CoordinateX.toFloat() + distanceSpeed / 2 -> {
                            setEvent0At75X()
                        }

                        event.rawX <= vUnSelect0At5CoordinateX.toFloat() + distanceSpeed / 2 && event.rawX > vUnSelect0At5CoordinateX.toFloat() - distanceSpeed / 2 -> {
                            setEvent0At5X()
                        }

                        event.rawX <= vUnSelect0At25CoordinateX.toFloat() + distanceSpeed / 2 && event.rawX > vUnSelect0At25CoordinateX.toFloat() - distanceSpeed / 2 -> {
                            setEvent0At25X()
                        }

                        event.rawX <= vUnSelect1CoordinateX.toFloat() + distanceSpeed / 2 && event.rawX > vUnSelect1CoordinateX.toFloat() - distanceSpeed / 2 -> {
                            setEvent1X()
                        }

                        event.rawX <= vUnSelect2CoordinateX.toFloat() + distanceSpeed / 2 && event.rawX > vUnSelect2CoordinateX.toFloat() - distanceSpeed / 2 -> {
                            setEvent2X()
                        }

                        event.rawX <= vUnSelect3CoordinateX.toFloat() + distanceSpeed / 2 && event.rawX > vUnSelect3CoordinateX.toFloat() - distanceSpeed / 2 -> {
                            setEvent3X()
                        }

                        event.rawX >= vUnSelect4CoordinateX.toFloat() - distanceSpeed / 2 -> {
                            setEvent4X()
                        }
                    }
                    invalidate()
                }

                MotionEvent.ACTION_UP -> {

                    when {
                        event.rawX <= vUnSelect0At75CoordinateX.toFloat() + distanceSpeed / 2 -> {
                            selectSpeed0At75X()
                        }

                        event.rawX <= vUnSelect0At5CoordinateX.toFloat() + distanceSpeed / 2 && event.rawX > vUnSelect0At5CoordinateX.toFloat() - distanceSpeed / 2 -> {
                            selectSpeed0At5X()
                        }

                        event.rawX <= vUnSelect0At25CoordinateX.toFloat() + distanceSpeed / 2 && event.rawX > vUnSelect0At25CoordinateX.toFloat() - distanceSpeed / 2 -> {
                            selectSpeed0At25X()
                        }

                        event.rawX <= vUnSelect1CoordinateX.toFloat() + distanceSpeed / 2 && event.rawX > vUnSelect1CoordinateX.toFloat() - distanceSpeed / 2 -> {
                            selectSpeed1X()
                        }

                        event.rawX <= vUnSelect2CoordinateX.toFloat() + distanceSpeed / 2 && event.rawX > vUnSelect2CoordinateX.toFloat() - distanceSpeed / 2 -> {
                            selectSpeed2X()
                        }

                        event.rawX <= vUnSelect3CoordinateX.toFloat() + distanceSpeed / 2 && event.rawX > vUnSelect3CoordinateX.toFloat() - distanceSpeed / 2 -> {
                            selectSpeed3X()
                        }

                        event.rawX >= vUnSelect4CoordinateX.toFloat() - distanceSpeed / 2 -> {
                            selectSpeed4X()
                        }

                        else -> selectSpeed1X()
                    }
                    invalidate()
                }
            }
            true
        }
    }

    private fun setEvent4X() {
        resetViewOld()
        vUnSelect4X?.gone()
        tvUnSelect4X?.apply {
            setTextColor(getAppColor(R.color.color_purple))
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getAppDimension(com.example.library_base.R.dimen.dimen_14)
            )
        }
        ivChose4X?.show()
        viewOld = vUnSelect4X
        textViewOld = tvUnSelect4X
        ivOld = ivChose4X
    }

    private fun setEvent3X() {
        resetViewOld()
        vUnSelect3X?.gone()
        tvUnSelect3X?.apply {
            setTextColor(getAppColor(R.color.color_purple))
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getAppDimension(com.example.library_base.R.dimen.dimen_14)
            )
        }
        ivChose3X?.show()
        viewOld = vUnSelect3X
        textViewOld = tvUnSelect3X
        ivOld = ivChose3X
    }

    private fun setEvent2X() {
        resetViewOld()
        vUnSelect2X?.gone()
        tvUnSelect2X?.apply {
            setTextColor(getAppColor(R.color.color_purple))
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getAppDimension(com.example.library_base.R.dimen.dimen_14)
            )
        }
        ivChose2X?.show()
        viewOld = vUnSelect2X
        textViewOld = tvUnSelect2X
        ivOld = ivChose2X
    }

    private fun setEvent1X() {
        resetViewOld()
        vUnSelect1X?.gone()
        tvUnSelect1X?.apply {
            setTextColor(getAppColor(R.color.color_purple))
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getAppDimension(com.example.library_base.R.dimen.dimen_14)
            )
        }
        ivChose1X?.show()
        viewOld = vUnSelect1X
        textViewOld = tvUnSelect1X
        ivOld = ivChose1X
    }

    private fun setEvent0At25X() {
        resetViewOld()
        vUnSelect0At25X?.gone()
        tvUnSelect0At25X?.apply {
            setTextColor(getAppColor(R.color.color_purple))
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getAppDimension(com.example.library_base.R.dimen.dimen_14)
            )
        }
        ivChose0Dot25X?.show()
        viewOld = vUnSelect0At25X
        textViewOld = tvUnSelect0At25X
        ivOld = ivChose0Dot25X
    }

    private fun setEvent0At5X() {
        resetViewOld()
        vUnSelect0At5X?.gone()
        tvUnSelect0At5X?.apply {
            setTextColor(getAppColor(R.color.color_purple))
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getAppDimension(com.example.library_base.R.dimen.dimen_14)
            )
        }
        ivChose0Dot5X?.show()
        viewOld = vUnSelect0At5X
        textViewOld = tvUnSelect0At5X
        ivOld = ivChose0Dot5X
    }

    private fun setEvent0At75X() {
        resetViewOld()
        vUnSelect0At75X?.gone()
        tvUnSelect0At75X?.apply {
            setTextColor(getAppColor(R.color.color_purple))
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getAppDimension(com.example.library_base.R.dimen.dimen_14)
            )
        }
        ivChose0Dot75X?.show()
        viewOld = vUnSelect0At75X
        textViewOld = tvUnSelect0At75X
        ivOld = ivChose0Dot75X
    }

    private fun resetViewOld() {
        viewOld?.show()
        textViewOld?.apply {
            setTextColor(Color.BLACK)
            setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                getAppDimension(com.example.library_base.R.dimen.dimen_10)
            )
        }
        ivOld?.hide()
    }

    fun setOldPosition(type: SPEED_TYPE) {
        oldSpeedType = type
        requestLayout()
    }

    interface ISpeedListener {
        fun onSpeedChange(type: SPEED_TYPE)
    }
}
