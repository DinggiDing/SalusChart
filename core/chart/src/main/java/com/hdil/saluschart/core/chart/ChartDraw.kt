package com.hdil.saluschart.core.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawContext
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas

object ChartDraw {
    fun drawGrid(drawScope: DrawScope, size: Size, metrics: ChartMath.ChartMetrics) {
        val step = (metrics.maxY - metrics.minY) / 5
        for (i in 0..5) {
            val yVal = metrics.minY + i * step
            val y = metrics.chartHeight - ((yVal - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
            drawScope.drawLine(
                color = androidx.compose.ui.graphics.Color.LightGray,
                start = Offset(metrics.paddingX, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            drawScope.drawContext.canvas.nativeCanvas.drawText(
                "%.0f".format(yVal),
                10f,
                y + 10f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 28f
                }
            )
        }
    }

    fun drawLinePath(drawScope: DrawScope, points: List<Offset>, color: Color) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawScope.drawPath(path, color = color, style = Stroke(width = 4f))
    }

    fun drawPoints(drawScope: DrawScope, points: List<Offset>, values: List<Float>) {
        points.forEachIndexed { i, point ->
            drawScope.drawCircle(color = Color.Blue, radius = 8f, center = point)
            drawScope.drawCircle(color = Color.White, radius = 4f, center = point)
            drawScope.drawContext.canvas.nativeCanvas.drawText(
                values[i].toInt().toString(),
                point.x, point.y - 12f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 28f
                }
            )
        }
    }

    fun drawXAxisLabels(ctx: DrawContext, labels: List<String>, metrics: ChartMath.ChartMetrics) {
        val spacing = metrics.chartWidth / (labels.size - 1)
        labels.forEachIndexed { i, label ->
            val x = metrics.paddingX + i * spacing
            ctx.canvas.nativeCanvas.drawText(
                label,
                x - 20f,
                metrics.chartHeight + 50f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 28f
                }
            )
        }
    }
}