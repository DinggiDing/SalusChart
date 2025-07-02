package com.hdil.saluschart.core.chart

data class ChartPoint(
    val x: Float,
    val y: Float,
    val label: String? = null,
    val color: Int? = null,
    val isSelected: Boolean = false
) {
    override fun toString(): String {
        return "ChartPoint(x=$x, y=$y, label=$label, color=$color, isSelected=$isSelected)"
    }
}

/**
 * 범위 바 차트를 위한 데이터 포인트 클래스
 */
data class RangeChartPoint(
    val x: Float,
    val yMin: Float,
    val yMax: Float,
    val label: String? = null,
    val color: Int? = null,
    val isSelected: Boolean = false
) {
    override fun toString(): String {
        return "RangeChartPoint(x=$x, yMin=$yMin, yMax=$yMax, label=$label, color=$color, isSelected=$isSelected)"
    }
}