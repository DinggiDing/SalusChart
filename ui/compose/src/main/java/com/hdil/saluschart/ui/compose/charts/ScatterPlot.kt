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
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.InteractionType

@Composable
fun ScatterPlot(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,
    xLabel: String = "X Axis",
    yLabel: String = "Y Axis",
    title: String = "Scatter Plot Example",
    pointColor: Color = com.hdil.saluschart.ui.theme.ChartColor.Default,
    width: androidx.compose.ui.unit.Dp = 250.dp,
    height: androidx.compose.ui.unit.Dp = 250.dp,
    tooltipTextSize: Float = 32f,        // 툴팁 텍스트 크기
    interactionType: InteractionType = InteractionType.POINT,
    chartType: ChartType = ChartType.SCATTERPLOT // 차트 타입 (툴팁 위치 결정용
) {
    if (data.isEmpty()) return

    val xLabels = data.map { it.x }
    val yValues = data.map { it.y }

    var canvasPoints by remember { mutableStateOf(listOf<Offset>()) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var chartMetrics by remember { mutableStateOf<ChartMath.ChartMetrics?>(null) }
    
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

                canvasPoints = points
                canvasSize = size
                chartMetrics = metrics

                ChartDraw.drawGrid(this, size, metrics)
                ChartDraw.Line.drawXAxisLabels(drawContext, xLabels.map { it.toString() }, metrics)
            }

            // Conditional interaction based on interactionType parameter
            when (interactionType) {
                InteractionType.NEAR_X_AXIS -> {
                    // BarMarker interactions (invisible bars for easier touching)
                    chartMetrics?.let { metrics ->
                        ChartDraw.Bar.BarMarker(
                            values = yValues,
                            metrics = metrics,
                            color = Color.Transparent,
                            barWidthRatio = 1.0f,
                            useFullHeight = true,
                            interactive = true,
                            useLineChartPositioning = true,
                            onBarClick = { index, value ->
                                // Handle bar click - same logic as point click
                                selectedPointIndex = if (selectedPointIndex == index) null else index
                            },
                            chartType = chartType
                        )
                    }
                    ChartDraw.Scatter.PointMarker(
                        points = canvasPoints,
                        values = yValues.map { it.toInt().toString() },
                        selectedPointIndex = selectedPointIndex,
                        onPointClick = null, // No point interaction in this mode
                        interactive = false, // Visual only, no interactions
                        chartType = chartType
                    )
                }
                InteractionType.POINT -> {
                    // PointMarker interactions (direct point touching)
                    ChartDraw.Scatter.PointMarker(
                        points = canvasPoints,
                        values = yValues.map { it.toInt().toString() },
                        selectedPointIndex = selectedPointIndex,
                        onPointClick = { index ->
                            // Handle point click - toggle selection
                            selectedPointIndex = if (selectedPointIndex == index) null else index
                        },
                        chartType = chartType
                    )
                }
                else -> {
                    // Default to non-interactive rendering
                    ChartDraw.Scatter.PointMarker(
                        points = canvasPoints,
                        values = yValues.map { it.toInt().toString() },
                        selectedPointIndex = null, // No selection in non-interactive mode
                        onPointClick = null,
                        chartType = chartType
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}