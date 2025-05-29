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