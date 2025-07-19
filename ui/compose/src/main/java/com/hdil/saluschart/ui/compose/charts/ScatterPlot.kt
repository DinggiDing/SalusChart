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

    // ScatterPlot implementation goes here
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

                ChartDraw.drawGrid(this, size, metrics)
                ChartDraw.Scatter.drawPoints(this, points, yValues)
                ChartDraw.Line.drawXAxisLabels(drawContext, xLabels.map { it.toString() }, metrics)
            }
        }


        Spacer(modifier = Modifier.height(4.dp))
    }
}