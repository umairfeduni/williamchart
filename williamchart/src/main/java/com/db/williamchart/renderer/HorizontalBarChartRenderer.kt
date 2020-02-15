package com.db.williamchart.renderer

import android.graphics.Paint
import android.graphics.RectF
import com.db.williamchart.ChartContract
import com.db.williamchart.Painter
import com.db.williamchart.animation.ChartAnimation
import com.db.williamchart.data.*
import com.db.williamchart.data.toOuterFrame
import com.db.williamchart.extensions.limits
import com.db.williamchart.extensions.maxValueBy
import com.db.williamchart.extensions.toDataPoints
import com.db.williamchart.extensions.toLabels
import com.db.williamchart.renderer.executor.DebugWithLabelsFrame
import com.db.williamchart.renderer.executor.MeasureHorizontalBarChartPaddings
import kotlin.math.max

class HorizontalBarChartRenderer(
    private val view: ChartContract.BarView,
    private val painter: Painter,
    private var animation: ChartAnimation<DataPoint>
) : ChartContract.Renderer {

    private var touchedBar : Bar? = null
    private var bars = emptyList<Bar>()

    private var data = emptyList<DataPoint>()

    private lateinit var outerFrame: Frame

    private lateinit var innerFrame: Frame

    private lateinit var chartConfiguration: BarChartConfiguration

    private val xLabels: List<Label> by lazy {
        val scaleStep = chartConfiguration.scale.size / RendererConstants.defaultScaleNumberOfSteps

        List(RendererConstants.defaultScaleNumberOfSteps + 1) {
            val scaleValue = chartConfiguration.scale.min + scaleStep * it
            Label(
                label = view.getFormattedLabel(chartConfiguration.labelsFormatter(scaleValue)),
                screenPositionX = 0F,
                screenPositionY = 0F
            )
        }
    }

    private val yLabels by lazy {
        data.toLabels()
    }

    override fun preDraw(configuration: ChartConfiguration): Boolean {

        if (data.isEmpty()) return true

        chartConfiguration = configuration as BarChartConfiguration

        if (chartConfiguration.scale.notInitialized())
            chartConfiguration =
                chartConfiguration.copy(
                    scale = Scale(
                        min = 0F,
                        max = data.limits().second
                    )
                )

        val yLongestChartLabelWidth =
            yLabels.maxValueBy {
                painter.measureLabelWidth(
                    it.label,
                    chartConfiguration.labelsSize
                )
            }
                ?: throw IllegalArgumentException("Looks like there's no labels to find the longest width.")

        val paddings = MeasureHorizontalBarChartPaddings()(
            axisType = chartConfiguration.axis,
            labelsHeight = painter.measureLabelHeight(chartConfiguration.labelsSize),
            xLastLabelWidth = painter.measureLabelWidth(
                xLabels.last().label,
                chartConfiguration.labelsSize
            ),
            yLongestLabelWidth = yLongestChartLabelWidth,
            labelsPaddingToInnerChart = RendererConstants.labelsPaddingToInnerChart
        )

        outerFrame = chartConfiguration.toOuterFrame()
        innerFrame = outerFrame.withPaddings(paddings)

        if (chartConfiguration.axis.shouldDisplayAxisX())
            placeLabelsX(innerFrame)

        if (chartConfiguration.axis.shouldDisplayAxisY())
            placeLabelsY(outerFrame, innerFrame)

        placeDataPoints(innerFrame)

        bars = processData(data)

        animation.animateFrom(innerFrame.bottom, data) { view.postInvalidate() }


        return false
    }

    override fun draw() {

        if (data.isEmpty()) return

        if (chartConfiguration.axis.shouldDisplayAxisX())
            view.drawLabels(xLabels)

        if (chartConfiguration.axis.shouldDisplayAxisY())
            view.drawLabels(yLabels)

        if (chartConfiguration.barsBackgroundColor != -1)
            view.drawBarsBackground(data, innerFrame)

        view.drawBars(bars, innerFrame)

        if (RendererConstants.inDebug) {
            view.drawDebugFrame(
                outerFrame,
                innerFrame,
                DebugWithLabelsFrame()(
                    painter = painter,
                    axisType = chartConfiguration.axis,
                    xLabels = xLabels,
                    yLabels = yLabels,
                    labelsSize = chartConfiguration.labelsSize
                )
            )
        }
    }

    override fun render(entries: LinkedHashMap<String, Float>) {
        data = entries.toDataPoints()
        view.postInvalidate()
    }

    override fun anim(entries: LinkedHashMap<String, Float>, animation: ChartAnimation<DataPoint>) {
        data = entries.toDataPoints()
        this.animation = animation
        view.postInvalidate()
    }

    override fun showToolTip(x: Float, y: Float) {

    }

    private fun placeLabelsX(innerFrame: Frame) {

        val widthBetweenLabels =
            (innerFrame.right - innerFrame.left) / RendererConstants.defaultScaleNumberOfSteps
        val xLabelsVerticalPosition =
            innerFrame.bottom -
                painter.measureLabelAscent(chartConfiguration.labelsSize) +
                RendererConstants.labelsPaddingToInnerChart

        xLabels.forEachIndexed { index, label ->
            label.screenPositionX = innerFrame.left + widthBetweenLabels * index
            label.screenPositionY = xLabelsVerticalPosition
        }
    }

    private fun placeLabelsY(outerFrame: Frame, innerFrame: Frame) {

        val halfBarWidth = (innerFrame.bottom - innerFrame.top) / yLabels.size / 2
        val labelsTopPosition = innerFrame.top + halfBarWidth
        val labelsBottomPosition = innerFrame.bottom - halfBarWidth
        val heightBetweenLabels = (labelsBottomPosition - labelsTopPosition) / (yLabels.size - 1)

        yLabels.forEachIndexed { index, label ->
            label.screenPositionX =
                outerFrame.left +
                    painter.measureLabelWidth(label.label, chartConfiguration.labelsSize) / 2
            label.screenPositionY =
                labelsBottomPosition -
                    heightBetweenLabels * index +
                    painter.measureLabelDescent(chartConfiguration.labelsSize)
        }
    }

    private fun placeDataPoints(innerFrame: Frame) {

        val scaleSize = chartConfiguration.scale.max - chartConfiguration.scale.min
        val chartWidth = innerFrame.right - innerFrame.left
        val halfBarWidth = (innerFrame.bottom - innerFrame.top) / yLabels.size / 2
        val labelsBottomPosition = innerFrame.bottom - halfBarWidth
        val labelsTopPosition = innerFrame.top + halfBarWidth
        val heightBetweenLabels = (labelsBottomPosition - labelsTopPosition) / (yLabels.size - 1)

        data.forEachIndexed { index, dataPoint ->
            dataPoint.screenPositionX =
                innerFrame.left +
                    // bar length must be positive, or zero
                    (chartWidth * max(
                        0f,
                        dataPoint.value - chartConfiguration.scale.min
                    ) / scaleSize)
            dataPoint.screenPositionY =
                labelsBottomPosition -
                    heightBetweenLabels * index
        }
    }


    fun processData(points: List<DataPoint>) : ArrayList<Bar>{
        val halfBarWidth =
                (innerFrame.bottom - innerFrame.top - (points.size + 1) * chartConfiguration.spacing) / points.size / 2

        val list = ArrayList<Bar>()
        points.forEach {
            list.add(
                    Bar(
                            view.getFormattedLabel(it.value.toString()),
                            Label(it.label,it.screenPositionX,it.screenPositionY),
                            RectF(innerFrame.left,
                                    it.screenPositionY - halfBarWidth,
                                    it.screenPositionX,
                                    it.screenPositionY + halfBarWidth)
                    )
            )
        }

        return list
    }

}
