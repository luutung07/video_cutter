package com.example.videocutter.presentation.widget.headeralert

import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.baseapp.base.extension.getAppColor
import com.example.baseapp.base.extension.getAppDrawable
import com.example.library_base.extension.getApplication
import com.example.videocutter.R

interface IHeaderAlert {
    fun show(msg: CharSequence?, type: HEADER_ALERT_TYPE)
    fun dismiss()
    fun destroy()
}

enum class HEADER_ALERT_TYPE {
    ERROR, WARNING, SUCCESS, CUSTOM
}

enum class HEADER_ALERT_TIME_SHOWN(val time: Long) {
    DELAY_DEFAULT(2000L),
    DELAY_1_HALF_SECOND(1500L),
    DELAY_2_SECOND(2000L),
    DELAY_3_SECOND(3000L)
}

abstract class HeaderAlert(val activity: AppCompatActivity) : IHeaderAlert {
    var message: CharSequence? = null
    var icon: Drawable? = null
    var bgColor : Int? = null
    var alertView: ViewGroup? = null
    private var isShowing = false

    override fun show(msg: CharSequence?, type: HEADER_ALERT_TYPE) {
        if (isShowing) {
            if (msg != message) {
                enqueueMessage(msg, type)
            }
            return
        }

        isShowing = true
        message = msg
        if (alertView == null) {
            alertView = onCreateView(activity, type)
            addView(alertView!!)
        }

        onViewCreated(alertView!!, type)
        showAnim(alertView!!, type)
    }

    override fun dismiss() {
        message = null
        dismissAnim(alertView!!)
    }

    abstract fun onCreateView(activity: AppCompatActivity, type: HEADER_ALERT_TYPE): ViewGroup
    abstract fun onViewCreated(view: ViewGroup, type: HEADER_ALERT_TYPE)

    open fun addView(view: ViewGroup) {
        activity.window.addContentView(alertView,
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
    }

    open fun showAnim(view: ViewGroup, type: HEADER_ALERT_TYPE) {
        val animDown = AnimationUtils.loadAnimation(activity, com.example.library_base.R.anim.slide_in_bottom)
        val animController = LayoutAnimationController(animDown)

        view.visibility = View.VISIBLE
        view.layoutAnimation = animController
        animDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                onShow(type)
                //Log.e("HeaderAlert", "onSHow")
            }

            override fun onAnimationStart(animation: Animation?) {
                //Log.e("HeaderAlert", "onStart")
            }
        })
        view.startAnimation(animDown)
    }

    open fun dismissAnim(view: ViewGroup) {
        val animUp = AnimationUtils.loadAnimation(activity, com.example.library_base.R.anim.slide_out_top)
        val animController = LayoutAnimationController(animUp)

        view.visibility = View.GONE
        view.layoutAnimation = animController
        animUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                isShowing = false
                onDismiss()
            }

            override fun onAnimationStart(animation: Animation?) {
            }

        })
        view.startAnimation(animUp)
    }

    open fun onShow(type: HEADER_ALERT_TYPE) {}
    open fun onDismiss() {}
    open fun enqueueMessage(msg: CharSequence?, type: HEADER_ALERT_TYPE) {}
}

open class HeaderAlertDefault(activity: AppCompatActivity) : HeaderAlert(activity) {

    var timeShown = HEADER_ALERT_TIME_SHOWN.DELAY_2_SECOND
    private val messageEnqueue = arrayListOf<MessageInfo>()
    private val handler = Handler(Looper.getMainLooper())
    private val dismissRunnable = Runnable {
        dismiss()
    }

    override fun onCreateView(activity: AppCompatActivity, type: HEADER_ALERT_TYPE): ViewGroup {
        return LayoutInflater.from(activity).inflate(R.layout.layout_header_alert, null) as ViewGroup
    }

    override fun onViewCreated(view: ViewGroup, type: HEADER_ALERT_TYPE) {
        val ivBackground = view.findViewById<RelativeLayout>(R.id.rlHeaderAlertBg)
        val ivIcon = view.findViewById<ImageView>(R.id.ivHeaderAlertIcon)
        val tvTitle = view.findViewById<TextView>(R.id.tvHeaderAlert)

        when (type) {
            HEADER_ALERT_TYPE.SUCCESS -> {
                ivIcon.setImageDrawable(getAppDrawable(R.drawable.ic_success_white))
                ivBackground.setBackgroundColor(getAppColor(R.color.success))
            }
            HEADER_ALERT_TYPE.WARNING -> {
                ivIcon.setImageDrawable(getAppDrawable(R.drawable.ic_info))
                ivBackground.setBackgroundColor(getAppColor(R.color.warning))
            }
            HEADER_ALERT_TYPE.ERROR -> {
                ivIcon.setImageDrawable(getAppDrawable(R.drawable.ic_info))
                ivBackground.setBackgroundColor(getAppColor(R.color.error))
            }
            HEADER_ALERT_TYPE.CUSTOM ->{
                ivIcon.setImageDrawable(icon)
                ivBackground.setBackgroundColor(getAppColor(bgColor ?: R.color.success))
            }
        }
        tvTitle.text = message

        handler.postDelayed(dismissRunnable, timeShown.time)
    }

    override fun onDismiss() {
        super.onDismiss()
        Log.e("HeaderAlert", "onDismiss: ${messageEnqueue.size}")
        if (messageEnqueue.isNotEmpty()) {
            val nextMsg = messageEnqueue.removeAt(0)
            show(nextMsg.msg, nextMsg.type!!)
        }
    }

    override fun destroy() {
        handler.removeCallbacks(dismissRunnable)
        messageEnqueue.clear()
    }

    override fun enqueueMessage(msg: CharSequence?, type: HEADER_ALERT_TYPE) {
        super.enqueueMessage(msg, type)
        var exist = false
        messageEnqueue.forEach {
            if (it.isSame(msg)) {
                exist = true
                return@forEach
            }
        }
        if (!exist) {
            val msgInfo = MessageInfo()
            msgInfo.msg = msg
            msgInfo.type = type
            messageEnqueue.add(msgInfo)
        }
    }


    private class MessageInfo() {
        var type: HEADER_ALERT_TYPE? = null
        var msg: CharSequence? = null

        fun isSame(msg: CharSequence?): Boolean {
            return this.msg == msg
        }
    }
}
