package com.practicum.playlistmaker.player.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.StyleRes
import androidx.core.graphics.drawable.toBitmap
import com.practicum.playlistmaker.R
import kotlin.math.min

class PlaybackButtonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    @AttrRes defStyleAttr: Int = 0,
    @StyleRes defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private val imagePlay: Bitmap?
    private val imagePause: Bitmap?
    private val imageDisable: Bitmap?
    private var imageRect = RectF(0f, 0f, 0f, 0f)
    private var playing = false
    private var ready = false

    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.PlaybackButtonView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                imagePlay = getDrawable(R.styleable.PlaybackButtonView_imagePlay)?.toBitmap()
                imagePause = getDrawable(R.styleable.PlaybackButtonView_imagePause)?.toBitmap()
                imageDisable = getDrawable(R.styleable.PlaybackButtonView_imageDisable)?.toBitmap()
            } finally {
                recycle()
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        this.performClick()
        when (event.action) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_UP -> {
                return false
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        imageRect = RectF(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        if (ready) {
            if (playing) {
                canvas.drawButtonPause()
            } else {
                canvas.drawButtonPlay()
            }
        } else {
            canvas.drawButtonDisable()
        }


    }

    private fun Canvas.drawButtonPlay() {
        imagePlay?.let {
            this.drawBitmap(imagePlay, null, imageRect, null)
        }
    }

    private fun Canvas.drawButtonPause() {
        imagePause?.let {
            this.drawBitmap(imagePause, null, imageRect, null)
        }
    }

    private fun Canvas.drawButtonDisable() {
        imageDisable?.let {
            this.drawBitmap(imageDisable, null, imageRect, null)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = min(measuredWidth, measuredHeight)
        setMeasuredDimension(size, size)
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    fun defaultState() {
        playing = false
        ready = false
        invalidate()
    }

    fun preparedOrPausedState() {
        playing = false
        ready = true
        invalidate()
    }

    fun playingState() {
        playing = true
        ready = true
        invalidate()
    }

}