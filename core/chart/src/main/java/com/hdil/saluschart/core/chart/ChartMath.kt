package com.hdil.saluschart.core.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

object ChartMath {
    data class ChartMetrics(val paddingX: Float, val paddingY: Float, val chartWidth: Float, val chartHeight: Float, val minY: Float, val maxY: Float)

    fun computeMetrics(size: Size, values: List<Float>): ChartMetrics {
        val paddingX = 60f
        val paddingY = 40f
        val chartWidth = size.width - paddingX
        val chartHeight = size.height - paddingY
        val maxY = values.maxOrNull() ?: 1f
        val minY = 0f
        return ChartMetrics(paddingX, paddingY, chartWidth, chartHeight, minY, maxY)
    }

    fun mapToCanvasPoints(data: List<ChartPoint>, size: Size, metrics: ChartMetrics): List<Offset> {
        val spacing = metrics.chartWidth / (data.size - 1)
        return data.mapIndexed { i, point ->
            val x = metrics.paddingX + i * spacing
            val y = metrics.chartHeight - ((point.y - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
            Offset(x, y)
        }
    }
}