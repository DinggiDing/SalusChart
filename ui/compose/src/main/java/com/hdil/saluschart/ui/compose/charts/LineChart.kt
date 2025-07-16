package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.ChartDraw
import com.hdil.saluschart.core.chart.ChartMath
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.ui.theme.ChartColor

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,      // ✅ ChartPoint 기반
    xLabel: String = "Time",
    yLabel: String = "Value",
    title: String = "Line Chart Example",
    lineColor: androidx.compose.ui.graphics.Color = ChartColor.Default,
    width: Dp = 250.dp,
    height: Dp = 250.dp
) {
    if (data.isEmpty()) return

    val xLabels = data.map { it.x }
    val yValues = data.map { it.y }

    Column(modifier = modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Box(
            Modifier
                .width(width)
                .height(height)
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val metrics = ChartMath.computeMetrics(size, yValues)
                val points = ChartMath.mapToCanvasPoints(data, size, metrics)

                ChartDraw.drawGrid(this, size, metrics)
                ChartDraw.drawLinePath(this, points, lineColor)
                ChartDraw.drawPoints(this, points, yValues)
                ChartDraw.drawXAxisLabels(drawContext, xLabels.map { it.toString() }, metrics)
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}

/**
 * Draws the value label using smart positioning to avoid line overlaps
 *
 * @param drawScope Drawing scope
 * @param pointIndex Index of the selected point
 * @param points All canvas points
 * @param yValues Y-axis values
 * @param totalPoints Total number of points
 */
private fun drawSmartPointLabel(
    drawScope: androidx.compose.ui.graphics.drawscope.DrawScope,
    pointIndex: Int,
    points: List<Offset>,
    yValues: List<Float>,
    totalPoints: Int
) {
    val currentPoint = points[pointIndex]
    val value = yValues[pointIndex]

    // Label dimensions
    val labelWidth = 50f
    val labelHeight = 30f

    // Get nearby line segments as obstacles
    val nearbyLines = ChartMath.getNearbyLineSegments(points, pointIndex, radius = 2)

    // Calculate smart label position
    val labelPosition = ChartMath.calculateSmartLabelPosition(
        centerX = currentPoint.x,
        centerY = currentPoint.y,
        labelWidth = labelWidth,
        labelHeight = labelHeight,
        nearbyLines = nearbyLines,
        padding = 20f
    )

    val labelCenterX = labelPosition.x + labelWidth / 2
    val labelCenterY = labelPosition.y + labelHeight / 2

    // Draw background for better readability
    drawScope.drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = 25f,
        center = Offset(labelCenterX, labelCenterY)
    )

    drawScope.drawCircle(
        color = Color.Black.copy(alpha = 0.1f),
        radius = 25f,
        center = Offset(labelCenterX, labelCenterY),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
    )

    // Draw the value text
    drawScope.drawContext.canvas.nativeCanvas.drawText(
        value.toInt().toString(),
        labelCenterX,
        labelCenterY + 8f, // Slight vertical adjustment for text centering
        android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 32f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
    )
}
