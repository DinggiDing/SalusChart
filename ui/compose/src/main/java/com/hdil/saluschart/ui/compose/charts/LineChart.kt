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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.InteractionType
import com.hdil.saluschart.ui.theme.ChartColor

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,      // ChartPoint 기반
    xLabel: String = "Time",
    yLabel: String = "Value",
    title: String = "Line Chart Example",
    lineColor: androidx.compose.ui.graphics.Color = ChartColor.Default,
    width: Dp = 250.dp,
    height: Dp = 250.dp,
    strokeWidth: Float = 4f,
    labelTextSize: Float = 28f,
    tooltipTextSize: Float = 32f,
    interactionType: InteractionType = InteractionType.POINT,
    chartType : ChartType = ChartType.LINE // 차트 타입 (툴팁 위치 결정용
) {
    if (data.isEmpty()) return

    val xLabels = data.map { it.x }
    val yValues = data.map { it.y }

    var canvasPoints by remember { mutableStateOf(listOf<androidx.compose.ui.geometry.Offset>()) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    var chartMetrics by remember { mutableStateOf<ChartMath.ChartMetrics?>(null) }

    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

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

                // 포인트 위치와 캔버스 크기를 상태 변수에 저장
                canvasPoints = points
                canvasSize = size
                chartMetrics = metrics

                ChartDraw.drawGrid(this, size, metrics)
                ChartDraw.Line.drawLine(this, points, lineColor, strokeWidth)
                ChartDraw.Line.drawXAxisLabels(
                    ctx = drawContext,
                    labels = xLabels.map { it.toString() },
                    metrics = metrics,
                    textSize = labelTextSize
                )
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
                        onPointClick = null,
                        interactive = false,
                        chartType = chartType,
                        showTooltipForIndex = selectedPointIndex
                    )
                }
                InteractionType.POINT -> {
                    // PointMarker interactions (interactive data points)
                    ChartDraw.Scatter.PointMarker(
                        points = canvasPoints,
                        values = yValues.map { it.toInt().toString() },
                        selectedPointIndex = selectedPointIndex,
                        onPointClick = { index ->
                            // 이미 선택된 포인트를 다시 클릭하면 선택 해제(null로 설정)
                            selectedPointIndex = if (selectedPointIndex == index) null else index
                        },
                        interactive = true,
                        chartType = chartType,
                        showTooltipForIndex = null
                    )
                }
                else -> {
                    // Non-interactive rendering
                    ChartDraw.Scatter.PointMarker(
                        points = canvasPoints,
                        values = yValues.map { it.toInt().toString() },
                        selectedPointIndex = selectedPointIndex,
                        onPointClick = null,
                        interactive = false,
                        chartType = chartType,
                        showTooltipForIndex = null
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}