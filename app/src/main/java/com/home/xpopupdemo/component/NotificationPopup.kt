package com.home.xpopupdemo.component

import android.content.Context
import android.os.Handler
import androidx.constraintlayout.widget.ConstraintLayout
import com.home.xpopupdemo.R
import com.lxj.xpopup.core.PositionPopupView

class NotificationPopup(context: Context) : PositionPopupView(context) {

    var clickListener: (() -> Unit)? = null

    override fun getImplLayoutId(): Int {
        val delayMillis = 200
        Handler().postDelayed({
            val imageView = findViewById<ConstraintLayout>(R.id.constraint_layout_root)
            imageView.setOnClickListener { clickListener?.invoke() }
        }, delayMillis.toLong())
        return R.layout.popup_notification
    }
}
