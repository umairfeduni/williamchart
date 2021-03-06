package com.db.williamchart.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import androidx.annotation.ColorInt
import com.db.williamchart.ChartContract
import com.db.williamchart.R
import com.db.williamchart.animation.DefaultHorizontalAnimation
import com.db.williamchart.animation.NoAnimation
import com.db.williamchart.data.*
import com.db.williamchart.extensions.drawChartBar
import com.db.williamchart.extensions.obtainStyledAttributes
import com.db.williamchart.renderer.HorizontalBarChartRenderer

class HorizontalBarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AxisChartView(context, attrs, defStyleAttr), ChartContract.BarView {

    @Suppress("MemberVisibilityCanBePrivate")
    var spacing = defaultSpacing

    @ColorInt
    @Suppress("MemberVisibilityCanBePrivate")
    var barsColor: Int = defaultBarsColor

    @ColorInt
    @Suppress("MemberVisibilityCanBePrivate")
    var barSelectColor : Int = defaultBarSelectedColor

    @Suppress("MemberVisibilityCanBePrivate")
    var barRadius: Float = defaultBarsRadius

    @Suppress("MemberVisibilityCanBePrivate")
    var barsBackgroundColor: Int = -1


    override val chartConfiguration: ChartConfiguration
        get() = BarChartConfiguration(
            measuredWidth,
            measuredHeight,
            Paddings(
                paddingLeft.toFloat(),
                paddingTop.toFloat(),
                paddingRight.toFloat(),
                paddingBottom.toFloat()
            ),
            axis,
            labelsSize,
            scale,
            spacing,
            labelsFormatter,
            barsBackgroundColor,
            displayInteger
        )

    init {
        renderer = HorizontalBarChartRenderer(this, painter, NoAnimation())
        animation = DefaultHorizontalAnimation()
        handleAttributes(obtainStyledAttributes(attrs, R.styleable.BarChartAttrs))
        handleEditMode()
    }

    override fun drawBars(
            points: List<DataPoint>,
            innerFrame: Frame
    ) {

        val halfBarWidth =
                (innerFrame.bottom - innerFrame.top - (points.size + 1) * spacing) / points.size / 2

        painter.prepare(color = barsColor, style = Paint.Style.FILL)
        points.forEach {
            canvas.drawRoundRect(
                    RectF(
                            innerFrame.left,
                            it.screenPositionY - halfBarWidth,
                            it.screenPositionX,
                            it.screenPositionY + halfBarWidth
                    ),
                    barRadius,
                    barRadius,
                    painter.paint
            )
        }
    }

    override fun drawBarsBackground(points: List<DataPoint>, innerFrame: Frame) {

        val halfBarWidth =
            (innerFrame.bottom - innerFrame.top - (points.size + 1) * spacing) / points.size / 2

        painter.prepare(color = barsBackgroundColor, style = Paint.Style.FILL)
        points.forEach {
            canvas.drawRoundRect(
                RectF(
                    innerFrame.left,
                    it.screenPositionY - halfBarWidth,
                    innerFrame.right,
                    it.screenPositionY + halfBarWidth
                ),
                barRadius,
                barRadius,
                painter.paint
            )
        }
    }

    override fun drawLabels(xLabels: List<Label>) {

        painter.prepare(
            textSize = labelsSize,
            color = labelsColor,
            font = labelsFont
        )

        xLabels.forEach {
            canvas.drawText(
                it.label,
                it.screenPositionX,
                it.screenPositionY,
                painter.paint
            )
        }
    }


    override fun drawDebugFrame(outerFrame: Frame, innerFrame: Frame, labelsFrame: List<Frame>) {
        painter.prepare(color = -0x1000000, style = Paint.Style.STROKE)
        canvas.drawRect(outerFrame.toRect(), painter.paint)
        canvas.drawRect(innerFrame.toRect(), painter.paint)
        labelsFrame.forEach { canvas.drawRect(it.toRect(), painter.paint) }
    }

    override fun drawToolTip( bar: Bar)  {

        painter.prepare(color = barSelectColor, style = Paint.Style.FILL)

        canvas.drawChartBar(bar.rectF, barRadius, painter.paint)
    }

    private fun handleAttributes(typedArray: TypedArray) {
        typedArray.apply {
            spacing = getDimension(R.styleable.BarChartAttrs_chart_spacing, spacing)
            barsColor = getColor(R.styleable.BarChartAttrs_chart_barsColor, barsColor)
            barRadius = getDimension(R.styleable.BarChartAttrs_chart_barsRadius, barRadius)
            barSelectColor = getColor(R.styleable.BarChartAttrs_chart_barSelectedColor, barSelectColor)
            barsBackgroundColor =
                getColor(R.styleable.BarChartAttrs_chart_barsBackgroundColor, barsBackgroundColor)
            recycle()
        }
    }
    override fun getFormattedLabel(label : String) : String{
        val value = label.toFloatOrNull()
        if(value!= null) {
            return if (displayInteger) {
                label.toFloat().toInt().toString()
            }else{
                String.format("%.1f",label.toFloat())
            }
        }
        return label
    }

    companion object {
        private const val defaultSpacing = 10f
        private const val defaultBarsColor = -0x1000000
        private const val defaultBarsRadius = 0F
        private const val defaultBarSelectedColor = Color.GRAY

    }
}
