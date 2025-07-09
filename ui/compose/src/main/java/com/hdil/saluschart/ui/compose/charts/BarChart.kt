package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.ChartDraw
import com.hdil.saluschart.core.chart.ChartMath
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.ui.theme.ChartColor

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,      // ✅ ChartPoint 기반
    xLabel: String = "Time",
    yLabel: String = "Value",
    title: String = "Bar Chart Example",
    barColor: androidx.compose.ui.graphics.Color = ChartColor.Default,
    width: Dp = 250.dp,
    height: Dp = 250.dp
) {
    if (data.isEmpty()) return

    val xLabels = data.map { it.label ?: it.x.toString() }
    val yValues = data.map { it.y }
    
    // State to track which bar is selected
    var selectedBarIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Box(
            Modifier
                .width(width)
                .height(height)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            // Hit testing: determine which bar was clicked
                            selectedBarIndex = determineClickedBar(offset, size, data.size)
                        }
                    }
            ) {
                val metrics = ChartMath.computeMetrics(size, yValues, chartType = ChartType.BAR)

                ChartDraw.drawGrid(this, size, metrics)
                ChartDraw.drawXAxis(this, metrics)
                ChartDraw.drawYAxis(this, metrics)
                ChartDraw.drawBars(this, yValues, metrics, barColor)
                ChartDraw.drawBarXAxisLabels(drawContext, xLabels, metrics)
                
                // Draw value label for selected bar
                selectedBarIndex?.let { index ->
                    if (index in yValues.indices) {
                        drawValueLabel(this, index, yValues[index], metrics, data.size)
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}

/**
 * Determines which bar was clicked based on touch coordinates
 *
 * @param offset Touch coordinates
 * @param canvasSize Canvas size
 * @param barCount Number of bars
 * @return Index of clicked bar, or null if no bar was clicked
 */
private fun determineClickedBar(
    offset: Offset,
    canvasSize: androidx.compose.ui.geometry.Size,
    barCount: Int
): Int? {
    val paddingX = 60f
    val chartWidth = canvasSize.width - paddingX
    val barWidth = chartWidth / barCount
    
    // Check if click is within the chart area (X-axis)
    if (offset.x < paddingX || offset.x > paddingX + chartWidth) {
        return null
    }
    
    // Calculate which bar was clicked
    val relativeX = offset.x - paddingX
    val barIndex = (relativeX / barWidth).toInt()
    
    return if (barIndex in 0 until barCount) barIndex else null
}

/**
 * Draws the value label above the selected bar
 *
 * @param drawScope Drawing scope
 * @param barIndex Index of the selected bar
 * @param value Value to display
 * @param metrics Chart metrics
 * @param totalBars Total number of bars
 */
private fun drawValueLabel(
    drawScope: androidx.compose.ui.graphics.drawscope.DrawScope,
    barIndex: Int,
    value: Float,
    metrics: ChartMath.ChartMetrics,
    totalBars: Int
) {
    val barWidth = metrics.chartWidth / totalBars
    val barCenterX = metrics.paddingX + (barIndex + 0.5f) * barWidth
    
    // Calculate the bar top position
    val barHeight = (value / (metrics.maxY - metrics.minY)) * metrics.chartHeight
    val barTopY = metrics.chartHeight - barHeight
    
    // Position label above the bar
    val labelX = barCenterX
    val labelY = barTopY - 20f // 20px above the bar
    
    // Draw background for better readability
    drawScope.drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = 25f,
        center = Offset(labelX, labelY)
    )
    
    drawScope.drawCircle(
        color = Color.Black.copy(alpha = 0.1f),
        radius = 25f,
        center = Offset(labelX, labelY),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
    )
    
    // Draw the value text
    drawScope.drawContext.canvas.nativeCanvas.drawText(
        value.toInt().toString(),
        labelX,
        labelY + 8f, // Slight vertical adjustment for text centering
        android.graphics.Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 32f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
    )
}