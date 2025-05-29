package com.hdil.saluschart.core.chart

data class ChartConfig(
    val chartType: ChartType = ChartType.LINE,
    val axis: Axis = Axis.XY,
    val showGrid: Boolean = true,
    val showLegend: Boolean = true,
    val showLabels: Boolean = true,
    val labelFormat: String = "%.2f",
    val animationEnabled: Boolean = true,
    val animationDuration: Long = 300L
) {
    enum class ChartType {
        LINE, BAR, PIE, AREA
    }

    enum class Axis {
        X, Y, XY
    }
}