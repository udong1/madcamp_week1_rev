package com.example.madcamp_week1_rev

import android.app.Dialog
import android.content.Context
import android.graphics.Matrix
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide

class ZoomableImageDialog(context: Context, private val imageUrl: Any) : Dialog(context) {

    private lateinit var imageView: ImageView
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var matrix: Matrix
    private var scaleFactor = 1.0f
    private var moveX = 0f
    private var moveY = 0f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_bigimage)

        imageView = findViewById(R.id.dialogImageView)
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
        matrix = Matrix()


        Glide.with(context).load(imageUrl).into(imageView)

        val closeButton: ImageButton = findViewById(R.id.closeButton)
        closeButton.setOnClickListener {
            dismiss()
        }

        imageView.setOnTouchListener { _, event ->
            handleTouchEvent(event)
            true
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // ScaleGestureDetector로부터 이벤트 전달
        scaleGestureDetector.onTouchEvent(event)

        return true
    }
    private fun handleTouchEvent(event: MotionEvent) {
        scaleGestureDetector.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                // 이미지뷰 이동
                val deltaX = event.x - moveX
                val deltaY = event.y - moveY
                matrix.postTranslate(deltaX, deltaY)
                imageView.imageMatrix = matrix
                moveX = event.x
                moveY = event.y
            }
            MotionEvent.ACTION_DOWN -> {
                moveX = event.x
                moveY = event.y
            }
        }
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            // 줌인 및 줌아웃을 수행
            scaleFactor *= detector.scaleFactor
            scaleFactor = if (scaleFactor < 1.0f) 1.0f else scaleFactor // 최소 스케일 제한
            scaleFactor = if (scaleFactor > 3.0f) 3.0f else scaleFactor // 최대 스케일 제한

            // 이미지뷰에 스케일 적용
            matrix.setScale(scaleFactor, scaleFactor)
            imageView.imageMatrix = matrix

            return true
        }
    }
}