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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.core.chart.ChartPoint

@Composable
fun ScatterPlot(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,
    xLabel: String = "X Axis",
    yLabel: String = "Y Axis",
    title: String = "Scatter Plot Example",
    pointColor: Color = com.hdil.saluschart.ui.theme.ChartColor.Default,
    width: androidx.compose.ui.unit.Dp = 250.dp,
    height: androidx.compose.ui.unit.Dp = 250.dp
) {
    if (data.isEmpty()) return

    val xLabels = data.map { it.x }
    val yValues = data.map { it.y }

    // State variables for points and selection
    var canvasPoints by remember { mutableStateOf(listOf<Offset>()) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            Modifier
                .width(width)
                .height(height)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val metrics = ChartMath.computeMetrics(size, yValues)
                val points = ChartMath.mapToCanvasPoints(data, size, metrics)

                // Store points and canvas size
                canvasPoints = points
                canvasSize = size

                ChartDraw.drawGrid(this, size, metrics)
                ChartDraw.Line.drawXAxisLabels(drawContext, xLabels.map { it.toString() }, metrics)
            }

            // Add PointMarkers for each data point
            canvasPoints.forEachIndexed { index, point ->
                ChartDraw.Scatter.PointMarker(
                    center = point,
                    value = yValues[index].toInt().toString(),
                    isSelected = selectedPointIndex == null || selectedPointIndex == index,
                    onClick = {
                        selectedPointIndex = if (selectedPointIndex == index) null else index
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}