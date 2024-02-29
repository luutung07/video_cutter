package com.example.videocutter.presentation.widget.crop

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.baseapp.base.extension.getAppColor
import com.example.videocutter.R

class CropVideo constructor(
    ctx: Context,
    attrs: AttributeSet
) : View(ctx, attrs) {

    companion object {
        private const val OFF_SET_VIEW = 5f
        private const val MAX_SIZE_CROP = 200f
        private const val OFF_SET_TOUCH_EDGE = 120f
        private const val TAG = "CropVideo"
    }

    private var widthScreen: Int? = null
    private var heightScreen: Int? = null

    private var widthView: Float? = null
    private var heightView: Float? = null

    private var paint = Paint().apply {
        style = Paint.Style.STROKE
        color = getAppColor(R.color.color_1)
        strokeWidth = 6f
    }

    private val paintOverlay = Paint().apply {
        color = getAppColor(R.color.over_lay)
    }

    private val paintTransparent = Paint().apply {
        color = getAppColor(R.color.transparent, context)
    }

    private var cropType = CROP_TYPE.TYPE_CUSTOM

    private var coordinateX = 0f
    private var coordinateY = 0f
    private var left = 0f
    private var top = 0f
    private var right = 0f
    private var bottom = 0f

    private var isMove = true
    private var isTranslationEdge = true

    private var countMatrix = 3
    private var hasInputUser = true

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return CropState(superState, cropType)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val oldState = state as? CropState
        super.onRestoreInstanceState(oldState?.superSavedState ?: state)
        cropType = oldState?.cropType ?: CROP_TYPE.TYPE_CUSTOM
        setType(cropType)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        widthScreen = MeasureSpec.getSize(widthMeasureSpec)
        heightScreen = MeasureSpec.getSize(heightMeasureSpec)
        setUpSizeView()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (widthView == null || heightView == null) return
        drawOutline(canvas)
        drawMain(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!hasInputUser) return false
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                coordinateX = event.x
                coordinateY = event.y
                isMove = true
                isTranslationEdge = true
                true
            }

            MotionEvent.ACTION_MOVE -> {
                if (cropType == CROP_TYPE.TYPE_CUSTOM) {
                    featureTypeCustom(event.x, event.y)
                } else {
                    featureTypeCrop(event.x, event.y)
                }
                coordinateX = event.x
                coordinateY = event.y
                limitEdge()
                invalidate()
                true
            }

            MotionEvent.ACTION_UP -> {
                coordinateX = event.x
                coordinateY = event.y
                true
            }

            else -> false
        }
    }

    private fun drawOutline(canvas: Canvas) {
        canvas.apply {
            drawRect(0f, 0f, left, heightScreen!!.toFloat(), paintOverlay)
            drawRect(left, 0f, right, top, paintOverlay)
            drawRect(left, bottom, right, heightScreen!!.toFloat(), paintOverlay)
            drawRect(right, 0f, widthScreen!!.toFloat(), heightScreen!!.toFloat(), paintOverlay)
        }
    }

    private fun drawMain(canvas: Canvas) {
        canvas.apply {
            drawRect(
                left + OFF_SET_VIEW,
                top + OFF_SET_VIEW,
                right - OFF_SET_VIEW,
                bottom - OFF_SET_VIEW,
                paint
            )
            for (i in 1 until countMatrix) {
                drawLine(
                    left + OFF_SET_VIEW,
                    top + OFF_SET_VIEW + (i * (bottom - top) / countMatrix),
                    right - OFF_SET_VIEW,
                    top + OFF_SET_VIEW + (i * (bottom - top) / countMatrix),
                    paint
                )

                drawLine(
                    left + OFF_SET_VIEW + (i * (right - left) / countMatrix),
                    top + OFF_SET_VIEW,
                    left + OFF_SET_VIEW + (i * (right - left) / countMatrix),
                    bottom - OFF_SET_VIEW,
                    paint
                )
            }
        }
    }

    private fun setUpSizeView() {
        if (widthScreen == null || heightScreen == null) return
        when (cropType) {
            CROP_TYPE.TYPE_CUSTOM -> {
                widthView = widthScreen!!.toFloat()
                heightView = heightScreen!!.toFloat()
                left = 0f
                top = 0f
                right = widthView!!.toFloat()
                bottom = heightView!!.toFloat()
            }

            CROP_TYPE.TYPE_INSTAGRAM -> {
                widthView = widthScreen!! * 0.7f
                heightView = heightScreen!! * 0.7f
                drawCropCenter()
            }

            CROP_TYPE.TYPE_4_5 -> {
                widthView = widthScreen!! * 4f / 5f
                heightView = heightScreen!! * 5f / 4f
                drawCropCenter()
            }

            CROP_TYPE.TYPE_5_4 -> {
                widthView = widthScreen!! * 5f / 4f
                heightView = heightScreen!! * 4f / 5f
                drawCropCenter()
            }

            CROP_TYPE.TYPE_9_16 -> {
                widthView = widthScreen!! * 9f / 16f
                heightView = heightScreen!! * 16f / 9f
                drawCropCenter()
            }

            CROP_TYPE.TYPE_16_9 -> {
                widthView = widthScreen!! * 16f / 9f
                heightView = heightScreen!! * 9f / 16f
                drawCropCenter()
            }

            CROP_TYPE.TYPE_3_2 -> {
                widthView = widthScreen!! * 3f / 2f
                heightView = heightScreen!! * 2f / 3f
                drawCropCenter()
            }

            CROP_TYPE.TYPE_2_3 -> {
                widthView = widthScreen!! * 2f / 3f
                heightView = heightScreen!! * 3f / 2f
                drawCropCenter()
            }

            CROP_TYPE.TYPE_4_3 -> {
                widthView = widthScreen!! * 4f / 3f
                heightView = heightScreen!! * 3f / 4f
                drawCropCenter()
            }

            CROP_TYPE.TYPE_3_4 -> {
                widthView = widthScreen!! * 3f / 4f
                heightView = heightScreen!! * 4f / 3f
                drawCropCenter()
            }
        }
    }

    private fun drawCropCenter() {
        limitSize()
        left = widthScreen!! / 2 - widthView!! / 2
        top = heightScreen!! / 2 - heightView!! / 2
        right = left + widthView!!.toFloat()
        bottom = top + heightView!!.toFloat()
    }

    private fun limitSize() {
        if (widthScreen!! < widthView!!) {
            widthView = widthScreen!!.toFloat()
        }
        if (heightScreen!! < heightView!!) {
            heightView = heightScreen!!.toFloat()
        }
    }

    private fun featureTypeCustom(touchX: Float, touchY: Float) {
        if (isTranslationEdge) {
            moveLeft(touchX)
            moveRight(touchX)
            moveTop(touchY)
            moveBottom(touchY)
        }
        if (isMove && coordinateX in left..right && coordinateY in top..bottom) {
            move(touchX, touchY)
        }
    }

    private fun featureTypeCrop(touchX: Float, touchY: Float) {
        if (isMove && coordinateX in left..right && coordinateY in top..bottom) {
            move(touchX, touchY)
        }
    }

    private fun moveLeft(touch: Float) {
        if (touch in left - OFF_SET_TOUCH_EDGE..left + OFF_SET_TOUCH_EDGE) {
            // di chuyển left
            left += touch - coordinateX
            setDragEdge(true)
            Log.d(TAG, "moveLeft: ")
        }
    }

    private fun moveTop(touch: Float) {
        if (touch in top - OFF_SET_TOUCH_EDGE..top + OFF_SET_TOUCH_EDGE) {
            // di chuyển left
            top += touch - coordinateY
            setDragEdge(true)
            Log.d(TAG, "moveTop: ")
        }
    }

    private fun moveRight(touch: Float) {
        if (touch in right - OFF_SET_TOUCH_EDGE..right + OFF_SET_TOUCH_EDGE) {
            // di chuyển left
            right += touch - coordinateX
            setDragEdge(true)
            Log.d(TAG, "moveRight: ")
        }
    }

    private fun moveBottom(touch: Float) {
        if (touch in bottom - OFF_SET_TOUCH_EDGE..bottom + OFF_SET_TOUCH_EDGE) {
            // di chuyển left
            bottom += touch - coordinateY
            setDragEdge(true)
            Log.d(TAG, "moveBottom: ")
        }
    }

    private fun move(touchX: Float, touchY: Float) {
        if (left > OFF_SET_VIEW && left < right) {
            right += touchX - coordinateX
        }

        if (right < widthScreen!! - OFF_SET_VIEW && right > left) {
            left += touchX - coordinateX
        }

        if (top > OFF_SET_VIEW && top < bottom) {
            bottom += touchY - coordinateY
        }

        if (bottom < heightScreen!! - OFF_SET_VIEW && bottom > top) {
            top += touchY - coordinateY
        }

        setDragEdge(false)
    }

    private fun limitEdge() {
        if (left < OFF_SET_VIEW) {
            left = OFF_SET_VIEW
        }
        if (right > widthScreen!! - OFF_SET_VIEW) {
            right = widthScreen!! - OFF_SET_VIEW
            if (left + MAX_SIZE_CROP > right) {
                left = right - MAX_SIZE_CROP
            }
        }

        if (left + MAX_SIZE_CROP > right) {
            right = left + MAX_SIZE_CROP
        }

        if (top < OFF_SET_VIEW) {
            top = OFF_SET_VIEW
        }
        if (bottom > heightScreen!! - OFF_SET_VIEW) {
            bottom = heightScreen!! - OFF_SET_VIEW
            if (top + MAX_SIZE_CROP > bottom) {
                top = bottom - MAX_SIZE_CROP
            }
        }
        if (top + MAX_SIZE_CROP > bottom) {
            bottom = top + MAX_SIZE_CROP
        }

    }

    private fun setDragEdge(isDrag: Boolean) {
        if (isDrag) {
            isMove = false
            isTranslationEdge = true
        } else {
            isMove = true
            isTranslationEdge = false
        }
    }

    fun setType(type: CROP_TYPE) {
        cropType = type
        requestLayout()
    }

    fun setHasInputUser(isInput: Boolean) {
        hasInputUser = isInput
    }

    class CropState(val superSavedState: Parcelable?, val cropType: CROP_TYPE) :
        BaseSavedState(superSavedState)
}
