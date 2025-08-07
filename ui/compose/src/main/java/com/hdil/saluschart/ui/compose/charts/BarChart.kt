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
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.InteractionType
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.ui.theme.ChartColor

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,      // ChartPoint 기반
    xLabel: String = "Time",
    yLabel: String = "Value",
    title: String = "Bar Chart Example",
    barColor: androidx.compose.ui.graphics.Color = ChartColor.Default,
    width: Dp = 250.dp,
    height: Dp = 250.dp,
    minY: Float? = null,                    // 사용자 지정 최소 Y값
    maxY: Float? = null,                    // 사용자 지정 최대 Y값
    barWidthRatio: Float = 0.8f,       // 바 너비 배수
    labelTextSize: Float = 28f,             // X축 레이블 텍스트 크기
    tooltipTextSize: Float = 32f,           // 툴팁 텍스트 크기
    interactionType: InteractionType = InteractionType.BAR, // 상호작용 타입
    onBarClick: ((Int, Float) -> Unit)? = null,  // 바 클릭 콜백
    chartType: ChartType = ChartType.BAR // 차트 타입 (툴팁 위치 결정용)
) {
    if (data.isEmpty()) return

    val xLabels = data.map { it.label ?: it.x.toString() }
    val yValues = data.map { it.y }

    var canvasSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }
    var chartMetrics by remember { mutableStateOf<ChartMath.ChartMetrics?>(null) }
    var selectedBarIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = modifier
                .width(width)
                .height(height)
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val metrics = ChartMath.computeMetrics(
                    size = size,
                    values = yValues,
                    chartType = ChartType.BAR,
                    minY = minY,
                    maxY = maxY
                )

                // Store metrics and canvas size for InteractiveBars
                canvasSize = size
                chartMetrics = metrics

                ChartDraw.drawGrid(this, size, metrics)
                ChartDraw.drawXAxis(this, metrics)
                ChartDraw.drawYAxis(this, metrics)

                ChartDraw.Bar.drawBarXAxisLabels(
                    ctx = drawContext,
                    labels = xLabels,
                    metrics = metrics,
                    textSize = labelTextSize
                )
            }

            // Conditional interaction based on interactionType parameter
            when (interactionType) {
                InteractionType.NEAR_X_AXIS -> {
                    // Visual bars (non-interactive)
                    chartMetrics?.let { metrics ->
                        ChartDraw.Bar.BarMarker(
                            minValues = List(yValues.size) { metrics.minY },
                            maxValues = yValues,
                            metrics = metrics,
                            color = barColor,
                            barWidthRatio = barWidthRatio,
                            interactive = false,
                            chartType = ChartType.BAR,
                            showTooltipForIndex = selectedBarIndex
                        )
                    }

                    // Interactive bars overlay (transparent bars for easier touching)
                    chartMetrics?.let { metrics ->
                        ChartDraw.Bar.BarMarker(
                            minValues = List(yValues.size) { metrics.minY },
                            maxValues = yValues,
                            metrics = metrics,
                            onBarClick = { index, tooltipText ->
                                // Handle bar click - toggle selection
                                selectedBarIndex = if (selectedBarIndex == index) null else index
                                onBarClick?.invoke(index, tooltipText.toFloat())
                            },
                            chartType = chartType,
                            showTooltipForIndex = selectedBarIndex,
                            isTouchArea = true
                        )
                    }
                }
                InteractionType.BAR -> {
                    // Interactive visual bars (direct bar touching)
                    chartMetrics?.let { metrics ->
                        ChartDraw.Bar.BarMarker(
                            minValues = List(yValues.size) { metrics.minY },
                            maxValues = yValues,
                            metrics = metrics,
                            color = barColor,
                            barWidthRatio = barWidthRatio,
                            interactive = true,
                            onBarClick = { index, tooltipText ->
                                onBarClick?.invoke(index, tooltipText.toFloat())
                            },
                            chartType = chartType
                        )
                    }
                }
                else -> {
                    // Visual bars (non-interactive)
                    chartMetrics?.let { metrics ->
                        ChartDraw.Bar.BarMarker(
                            minValues = List(yValues.size) { metrics.minY },
                            maxValues = yValues,
                            metrics = metrics,
                            color = barColor,
                            barWidthRatio = barWidthRatio,
                            interactive = false,
                            chartType = chartType,
                            showTooltipForIndex = selectedBarIndex
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}
