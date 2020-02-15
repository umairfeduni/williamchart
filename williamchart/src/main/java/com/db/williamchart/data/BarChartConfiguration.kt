package com.db.williamchart.data

data class BarChartConfiguration(

    override val width: Int,
    override val height: Int,
    override val paddings: Paddings,
    override val axis: AxisType,
    override val labelsSize: Float,
    override val scale: Scale,
    val spacing: Float,
    override val labelsFormatter: (Float) -> String = { it.toString() },
    val barsBackgroundColor: Int,
    override val displayInteger: Boolean

) : ChartConfiguration(
    width = width,
    height = height,
    paddings = paddings,
    axis = axis,
    labelsSize = labelsSize,
    scale = scale,
    labelsFormatter = labelsFormatter,
    displayInteger = displayInteger

)
