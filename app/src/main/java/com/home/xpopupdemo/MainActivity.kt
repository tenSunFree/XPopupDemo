package com.home.xpopupdemo

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.home.xpopupdemo.component.NotificationPopup
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.XPopupUtils
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeView()
    }

    override fun onDestroy() {
        hidePartialMaskView()
        super.onDestroy()
    }

    private fun initializeView() {
        mask_pierce_iew.setOnClickListener {
            toast("click mask_pierce_iew")
            hidePartialMaskView()
        }
        image_view_notification.post {
            val screenHeight = XPopupUtils.getWindowHeight(this)
            val notificationHeight = image_view_notification.height
            val rr = NotificationPopup(this)
            val gg = XPopup.Builder(this)
                .dismissOnBackPressed(false) // 點擊其它地方不消失
                .dismissOnTouchOutside(false) // 點擊返回鍵不消失
                .hasStatusBarShadow(true) // 啟用狀態欄陰影
                .isCenterHorizontal(true)
                .offsetY((screenHeight - notificationHeight) / 2)
                .asCustom(rr)
                .show()
            rr.clickListener = {
                toast("click image_view_notification")
                gg.dismiss()
            }
        }
    }

    private fun hidePartialMaskView() {
        mask_pierce_iew.clearCompositeDisposable()
        mask_pierce_iew.visibility = View.GONE
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }
}
