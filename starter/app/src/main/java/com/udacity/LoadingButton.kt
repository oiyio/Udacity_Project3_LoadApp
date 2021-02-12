package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var buttonText = ""
    private var buttonTextColor = 0
    private var buttonBackgroundColor = 0

    private var rectText = Rect()

    private val valueAnimator = ValueAnimator()

    private var paintBackground = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paintText = Paint(Paint.ANTI_ALIAS_FLAG)

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

    }


    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonText = getString(R.styleable.LoadingButton_text) ?: ""
            buttonTextColor = getInt(R.styleable.LoadingButton_textColor, 0)
            buttonBackgroundColor = getInt(R.styleable.LoadingButton_backgroundColor, 0)
        }

        paintBackground.apply {
            color = buttonBackgroundColor
        }

        paintText.apply {
            color = buttonTextColor
            textSize = context.resources.getDimension(R.dimen.default_text_size)
            /*textAlign = Paint.Align.CENTER*/
        }
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintBackground)

        paintText.getTextBounds(buttonText, 0, buttonText.length, rectText)
        canvas.drawText(buttonText, width/2f-rectText.centerX(), height/2f - rectText.centerY(), paintText)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

}