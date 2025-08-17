package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
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
import com.hdil.saluschart.core.chart.chartDraw.LegendPosition
import com.hdil.saluschart.ui.theme.ChartColor

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,      // ChartPoint 기반
    xLabel: String = "Time",
    yLabel: String = "Value",
    title: String = "Line Chart Example",
    lineColor: androidx.compose.ui.graphics.Color = ChartColor.Default,
    strokeWidth: Float = 4f,
    minY: Float? = null,                    // 사용자 지정 최소 Y값
    maxY: Float? = null,                    // 사용자 지정 최대 Y값
    labelTextSize: Float = 28f,
    tooltipTextSize: Float = 32f,
    yPosition: String = "left",             // Y축 위치 ("left" 또는 "right")
    interactionType: InteractionType = InteractionType.POINT,
    showPoint: Boolean = false, // 포인트 표시 여부
    showLegend: Boolean = false,
    legendPosition: LegendPosition = LegendPosition.BOTTOM,
    chartType : ChartType = ChartType.LINE, // 차트 타입 (툴팁 위치 결정용
    maxXTicksLimit: Int? = null             // X축에 표시할 최대 라벨 개수 (null이면 모든 라벨 표시)
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
        Spacer(Modifier.height(16.dp))

        Box(
            Modifier
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
//                val metrics = ChartMath.computeMetrics(size, yValues)
                val metrics = ChartMath.computeMetrics(
                    size = size,
                    values = yValues,
                    chartType = ChartType.BAR,
                    minY = minY, // 사용자 지정 최소 Y값
                    maxY = maxY
                )

                val points = ChartMath.mapToCanvasPoints(data, size, metrics)

                // 포인트 위치와 캔버스 크기를 상태 변수에 저장
                canvasPoints = points
                canvasSize = size
                chartMetrics = metrics

                ChartDraw.drawGrid(this, size, metrics, yPosition)
                ChartDraw.Line.drawLine(this, points, lineColor, strokeWidth)
                ChartDraw.Line.drawXAxisLabels(
                    ctx = drawContext,
                    labels = xLabels.map { it.toString() },
                    metrics = metrics,
                    textSize = labelTextSize,
                    maxXTicksLimit = maxXTicksLimit
                )
            }

//            if (showPoint) {
            // Conditional interaction based on interactionType parameter
                when (interactionType) {
                    InteractionType.TOUCH_AREA -> {
                        // BarMarker interactions (invisible bars for easier touching)
                        chartMetrics?.let { metrics ->
                            ChartDraw.Bar.BarMarker(
                                data = data,
                                minValues = List(yValues.size) { metrics.minY },
                                maxValues = yValues,
                                metrics = metrics,
                                useLineChartPositioning = true,
                                onBarClick = { index, tooltipText ->
                                    selectedPointIndex =
                                        if (selectedPointIndex == index) null else index
                                },
                                isTouchArea = true,
                                chartType = chartType,
                                showTooltipForIndex = selectedPointIndex
                            )
                        }
                        ChartDraw.Scatter.PointMarker(
                            data = data,
                            points = canvasPoints,
                            values = yValues,
                            color = lineColor,
                            showPoint = showPoint,
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
                            data = data,
                            points = canvasPoints,
                            values = yValues,
                            color = lineColor,
                            showPoint = showPoint,
                            selectedPointIndex = selectedPointIndex,
                            onPointClick = { index ->
                                // 이미 선택된 포인트를 다시 클릭하면 선택 해제(null로 설정)
                                selectedPointIndex = if (selectedPointIndex == index) null else index
                            },
                            interactive = true,
                            chartType = chartType,
                            showTooltipForIndex = selectedPointIndex
                        )
                    }
                    else -> {
                        // Non-interactive rendering
                        ChartDraw.Scatter.PointMarker(
                            data = data,
                            points = canvasPoints,
                            values = yValues,
                            color = lineColor,
                            selectedPointIndex = selectedPointIndex,
                            onPointClick = null,
                            interactive = false,
                            chartType = chartType,
                            showTooltipForIndex = null
                        )
                    }
//                }
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}