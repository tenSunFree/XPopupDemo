package com.home.xpopupdemo.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.home.xpopupdemo.R
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class PartialMaskView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {

    private var screenWidth: Int = 0   // 屏幕的寬
    private var screenHeight: Int = 0  // 屏幕的高
    private var piercedX: Int = 0
    private var piercedY: Int = 0
    private var piercedRadius: Int = 0
    private val compositeDisposable = CompositeDisposable()

    init {
        initializeView()
        initializeCompositeDisposable()
    }

    @SuppressLint("DrawAllocation")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onDraw(canvas: Canvas) {
        val mSrcRect = makeSrcRect()
        val mDstCircle = makeDstCircle()
        val paint = Paint()
        paint.isFilterBitmap = false
        canvas.saveLayer(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), null)
        canvas.drawBitmap(mDstCircle, 0f, 0f, paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OUT)
        paint.alpha = 160
        canvas.drawBitmap(mSrcRect, 0f, 0f, paint)
        paint.xfermode = null
        canvas.saveLayer(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat(), null)
        paint.alpha = 255
        canvas.drawBitmap(
            createDialogBoxBitmap(),
            (screenWidth * 0.235).toFloat(),
            (piercedY + (screenHeight * 0.0833 / 1.5).toInt()).toFloat(),
            paint
        )
    }

    private fun initializeView() {
        val layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setLayoutParams(layoutParams)
        if (screenWidth == 0) {
            val displayMetrics = resources.displayMetrics
            screenWidth = displayMetrics.widthPixels
            screenHeight = displayMetrics.heightPixels
        }
        setPiercePosition()
    }

    private fun initializeCompositeDisposable() {
        compositeDisposable.add(Observable.interval(0, 90, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { count ->
                when ((count!! % 13).toInt()) {
                    0 -> setPiercePosition(2.2)
                    1 -> setPiercePosition(2.4)
                    2 -> setPiercePosition(2.6)
                    3 -> setPiercePosition(2.4)
                    4 -> setPiercePosition(2.2)
                    5 -> setPiercePosition()
                }
            })
    }

    /**
     * @param proportion 圓的縮放比例, 默認為2.0
     */
    private fun setPiercePosition(proportion: Double = 2.0) {
        this.piercedX = (screenWidth * 0.815).toInt()
        this.piercedY = (screenHeight * 0.0833 / 2).toInt()
        this.piercedRadius = (screenHeight * 0.0833 / proportion).toInt()
        invalidate()
    }

    /**
     * 創建鏤空層圓形形狀
     */
    private fun makeDstCircle(): Bitmap {
        val bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.WHITE
        canvas.drawCircle(piercedX.toFloat(), piercedY.toFloat(), piercedRadius.toFloat(), paint)
        return bitmap
    }

    /**
     * 創建遮罩層形狀
     */
    private fun makeSrcRect(): Bitmap {
        val bitmap = Bitmap.createBitmap(screenWidth, screenHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.color = Color.BLACK
        canvas.drawRect(RectF(0f, 0f, screenWidth.toFloat(), screenHeight.toFloat()), paint)
        return bitmap
    }

    private fun createDialogBoxBitmap(): Bitmap {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.iocn_dialog_box)
        val width = bitmap.width
        val height = bitmap.height
        val displayMetrics = resources.displayMetrics  // 獲取屏幕密度
        val realScale = 1.0f / displayMetrics.density  // 恢復到實際像素的縮放
        var settingScale = realScale
        if (realScale * width > screenWidth / 2) {
            settingScale = 0.65f * screenWidth / width
        } else if (realScale * height > screenHeight) {
            settingScale = 0.65f * screenHeight / height
        }
        // 取得想要縮放的matrix參數
        val matrix = Matrix()
        matrix.postScale(settingScale, settingScale)
        // 得到新的圖片
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
    }

    fun clearCompositeDisposable() {
        compositeDisposable.clear()
    }
}