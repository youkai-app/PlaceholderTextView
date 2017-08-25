/**
 * Copyright (C) 2017 The Youkai Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.youkai.placeholdertextview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet


/**
 * A custom TextView which shows placeholder lines given a sample text when it has no text set to it.
 */
class PlaceholderTextView : AppCompatTextView {
    private val LINE_SPACING by lazy { 4.toPx(context) } // 4dp

    private var sampleText: String? = null
    private var placeholderColor: Int? = null

    private var ruleLines: Int? = null
    private var ruleMaxLines: Int? = null

    private var totalPlaceholderWidth: Float = 0f
    private var singleLineWidth: Float = 0f
    private var linesToDraw: Float = 0f
    private var placeholderLineHeight: Float = 0f

    // This is later set to the text size of this TextView
    private val placeholderPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : super(context) {
        init(context, null, null, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, null, null)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, null)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int?, defStyleRes: Int?) {
        /* Get values from the layout */
        val a = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.PlaceholderTextView,
                defStyleAttr ?: 0,
                defStyleRes ?: 0
        )

        val androidAttrs = intArrayOf(android.R.attr.lines, android.R.attr.maxLines)
        val ta = context.obtainStyledAttributes(attrs, androidAttrs)
        try {
            val lines = ta.getInt(0, 0)
            val maxLines = ta.getInt(1, 0)
            if (lines != 0) ruleLines = lines
            if (maxLines != 0) ruleMaxLines = maxLines
        } finally {
            ta.recycle()
        }

        try {
            sampleText = a.getString(R.styleable.PlaceholderTextView_ptv_sampleText)
            val color = a.getColor(R.styleable.PlaceholderTextView_ptv_placeholderColor, 0)
            placeholderColor = if (color != 0) color else null
        } finally {
            a.recycle()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (shouldShowPlaceholder()) {
            // Calculate with regard to display width for now.
            calculatePlaceholderValues()

            val widthMode = MeasureSpec.getMode(widthMeasureSpec)
            val widthSize = MeasureSpec.getSize(widthMeasureSpec)
            val heightMode = MeasureSpec.getMode(heightMeasureSpec)
            val heightSize = MeasureSpec.getSize(heightMeasureSpec)

            val desiredWidth = Math.round(singleLineWidth) + compoundPaddingLeft + compoundPaddingRight

            val width: Int
            if (widthMode == MeasureSpec.EXACTLY) {
                width = widthSize
            } else if (widthMode == MeasureSpec.AT_MOST) {
                width = Math.min(desiredWidth, widthSize)
            } else {
                width = desiredWidth
            }

            // ceil here because 3.3 lines means 3 full lines and 1 0.3 of a full line
            val linesCount = Math.ceil(linesToDraw.toDouble())
            // -1 here because there are n-1 empty spaces between n lines
            val lineSpacing = (linesCount - 1) * LINE_SPACING
            val padding = compoundPaddingTop + compoundPaddingBottom
            val desiredHeight = Math.round(linesCount * placeholderLineHeight +
                    padding + lineSpacing).toInt()

            val height: Int
            if (heightMode == MeasureSpec.EXACTLY) {
                height = heightSize
            } else if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(desiredHeight, heightSize)
            } else {
                height = desiredHeight
            }

            setMeasuredDimension(width, height)

            // Now that we know our width, calculate again.
            calculatePlaceholderValues()
        } else {
            // Not showing placeholder - let normal TextView calculation proceed.
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    /**
     * Returns average estimated width for a character in the sample text.
     *
     * We simply multiple the height by a percentage and return the result. 0.45 is found to be a
     * nice value which yields results with total length/width similar to text shown in a regular
     * TextView with the same properties.
     */
    internal fun getAvgCharWidth(): Float {
        return placeholderLineHeight * 0.45f
    }

    /**
     * Loads placeholder color and height.
     *
     * Calculates placeholder line width and the total number of lines to draw based on either view
     * width (if available), or screen width.
     *
     * Watches out for android:lines and android:maxLines. Gives dominance to android:lines.
     */
    internal fun calculatePlaceholderValues() {
        loadPlaceholderColor()
        loadPlaceholderLineHeight()

        val availableWidth = if (measuredWidth != 0) {
            measuredWidth
        } else {
            context.resources.displayMetrics.widthPixels
        }
        totalPlaceholderWidth = (sampleText?.length?.toFloat() ?: 0f) * getAvgCharWidth()
        singleLineWidth = Math.max((availableWidth - (compoundPaddingLeft + compoundPaddingRight)).toFloat(), 1f)
        linesToDraw = if (totalPlaceholderWidth > availableWidth) {
            val lines = totalPlaceholderWidth / singleLineWidth
            val linesRound = Math.ceil(lines.toDouble()).toInt()
            if (ruleLines != null) {
                if (linesRound > ruleLines!!) ruleLines!!.toFloat() else lines
            } else if (ruleMaxLines != null) {
                if (linesRound > ruleMaxLines!!) ruleMaxLines!!.toFloat() else lines
            } else {
                lines
            }
        } else 1f
    }

    /**
     * Sets the placeholder color to the current text color of this TextView with 20% alpha, if a
     * value isn't set manually.
     */
    internal fun loadPlaceholderColor() {
        placeholderPaint.color = if (placeholderColor != null) {
            placeholderColor!!
        } else {
            (currentTextColor and 0x00FFFFFF) or 0x33000000
        }
    }

    /**
     * Sets the placeholder line height to the current text size of this TextView.
     */
    internal fun loadPlaceholderLineHeight() {
        placeholderLineHeight = textSize
    }

    override fun onDraw(canvas: Canvas) {
        if (shouldShowPlaceholder()) {
            drawPlaceholder(canvas)
        } else {
            super.onDraw(canvas)
        }
    }

    /**
     * Draws placeholder lines.
     */
    internal fun drawPlaceholder(canvas: Canvas) {
        if (linesToDraw == 1f) {
            drawLine(canvas, compoundPaddingTop.toFloat(), totalPlaceholderWidth)
        } else {
            // Loop as many times as we have full lines and draw them
            for (i in 0..linesToDraw.toInt() - 1) {

                if (i == 0) {
                    // The first/top line - goes at the top of the view + padding
                    drawLine(canvas, compoundPaddingTop.toFloat(), singleLineWidth)
                } else {
                    // Remaining full width lines
                    // Top padding + every previous line + line spacings in between
                    drawLine(
                            canvas,
                            compoundPaddingTop.toFloat() + (placeholderLineHeight + LINE_SPACING) * i,
                            singleLineWidth
                    )
                }
            }

            // Float minus Int gives us the width multiplier of the last line (e.g. 0.74).
            val lastLineWidthMultiplier = linesToDraw - linesToDraw.toInt()

            // 0 would mean we only had full width lines and no leftover
            if (lastLineWidthMultiplier > 0f) {
                drawLine(
                        canvas,
                        compoundPaddingTop.toFloat() + (placeholderLineHeight + LINE_SPACING) * linesToDraw.toInt(),
                        singleLineWidth * lastLineWidthMultiplier
                )
            }
        }
    }

    /**
     * Draws a single placeholder line on the given canvas.
     *
     * @param canvas Canvas to draw on
     * @param top The top coordinate of the line (rect) to draw
     * @param width The total width of the line to draw
     */
    internal fun drawLine(canvas: Canvas, top: Float, width: Float) {
        val left = compoundPaddingLeft.toFloat()
        val right = left + width
        val bottom = top + placeholderLineHeight
        val cornerRadius = 4.toPx(context).toFloat()
        canvas.drawRoundRect(
                RectF(left, top, right, bottom),
                cornerRadius, cornerRadius, placeholderPaint
        )
    }

    /**
     * Returns a boolean indicating whether we should show placeholder or not. We should, if the text
     * of this TextView is empty, and shouldn't otherwise.
     */
    internal fun shouldShowPlaceholder(): Boolean = text.isEmpty()

    /**
     * Sets sample text
     */
    fun setSampleText(text: String) {
        sampleText = text
        requestLayout()
    }

    /**
     * Returns current sample text
     */
    fun getSampleText(): String? = sampleText

    /**
     * Sets placeholder color
     */
    fun setPlaceholderColor(color: Int) {
        placeholderColor = if (color != 0) color else null
        invalidate()
    }

    /**
     * Returns current placeholder color.
     *
     * Calling this before the view has been drawn may result in wrong color values, simply because
     * the correct value hasn't been calculated at that point.
     */
    fun getPlaceholderColor(): Int = placeholderPaint.color

    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        requestLayout()
    }
}