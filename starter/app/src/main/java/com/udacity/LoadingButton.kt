package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
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

    private var rectButtonText = Rect()
    private var rectCircularAnimationPosition = RectF()

    private var valueAnimatorRectangular = ValueAnimator()
    private var valueAnimatorCircular = ValueAnimator()

    private var progressRectangularAnimation = 0
    private var progressCircularAnimation = 0

    private var paintBackground = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paintText = Paint(Paint.ANTI_ALIAS_FLAG)

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when (new) {
            ButtonState.Loading -> {

                isEnabled = false
                valueAnimatorRectangular = ValueAnimator.ofInt(0, widthSize).apply {
                    duration = 2500
                    repeatCount = ValueAnimator.INFINITE
                    repeatMode = ValueAnimator.RESTART
                    addUpdateListener { valueAnimator ->
                        progressRectangularAnimation = animatedValue as Int
                        invalidate()
                    }
                    start()
                }

                valueAnimatorCircular = ValueAnimator.ofInt(0, 360).apply {
                    duration = 2500
                    addUpdateListener { valueAnimator ->
                        progressCircularAnimation = valueAnimator.animatedValue as Int
                        valueAnimator.repeatCount = ValueAnimator.INFINITE
                        invalidate()
                    }
                    start()
                }

                buttonText = context.getString(R.string.button_loading)
                invalidate()
            }
            ButtonState.Completed -> {
                valueAnimatorRectangular.end()
                valueAnimatorCircular.end()
                isEnabled = true
                buttonText = context.getString(R.string.download)
                progressRectangularAnimation = 0
                progressCircularAnimation = 0
                invalidate()
            }
        }
    }


    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
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

        buttonText = resources.getString(R.string.download)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawInitialBackgroundColor(canvas)

        drawRectangularAnimation(canvas)

        drawButtonText(canvas)

        drawCircularAnimation(canvas)
    }

    private fun drawCircularAnimation(canvas: Canvas) {
        paintBackground.color = Color.YELLOW
        canvas.drawArc(
            rectCircularAnimationPosition,
            0F,
            progressCircularAnimation.toFloat(),
            true,
            paintBackground
        )
    }

    private fun drawButtonText(canvas: Canvas) {
        paintText.getTextBounds(
            buttonText,
            0,
            buttonText.length,
            rectButtonText
        )  // calculates rectButtonText in this line.
        canvas.drawText(
            buttonText,
            width / 2f - rectButtonText.centerX(),
            height / 2f - rectButtonText.centerY(),
            paintText
        )
    }

    private fun drawRectangularAnimation(canvas: Canvas) {
        paintBackground.color = ContextCompat.getColor(context, R.color.colorPrimaryDark)
        canvas.drawRect(
            0f,
            0f,
            widthSize.toFloat() * progressRectangularAnimation / 100,
            heightSize.toFloat(), paintBackground
        )
    }

    private fun drawInitialBackgroundColor(canvas: Canvas) {
        paintBackground.color = ContextCompat.getColor(context, R.color.colorPrimary)
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paintBackground)
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

        rectCircularAnimationPosition.set(
            widthSize - 200f,
            heightSize / 2 - 50f,
            widthSize.toFloat() - 100f,
            heightSize / 2 + 50f
        )
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

}