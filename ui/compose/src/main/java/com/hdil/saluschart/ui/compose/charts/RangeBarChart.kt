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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.RangeChartPoint
import com.hdil.saluschart.core.chart.InteractionType
import com.hdil.saluschart.ui.theme.ChartColor

@Composable
fun RangeBarChart(
    modifier: Modifier = Modifier,
    data: List<RangeChartPoint>,
    xLabel: String = "Time",
    yLabel: String = "Value", 
    title: String = "Range Bar Chart",
    barColor: androidx.compose.ui.graphics.Color = ChartColor.Default,
    barWidthRatio: Float = 0.6f,
    yPosition: String = "left", // Y축 위치 ("left" 또는 "right")
    interactionType: InteractionType = InteractionType.BAR,
    onBarClick: ((Int, RangeChartPoint) -> Unit)? = null,
    chartType: ChartType = ChartType.RANGE_BAR,
    maxXTicksLimit: Int? = null             // X축에 표시할 최대 라벨 개수 (null이면 모든 라벨 표시)
) {
    if (data.isEmpty()) return
    
    val labels = data.map { it.label ?: it.x.toString() }
    var selectedBarIndex by remember { mutableStateOf<Int?>(null) }
    var chartMetrics by remember { mutableStateOf<ChartMath.ChartMetrics?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Box(
            Modifier
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val metrics = ChartMath.RangeBar.computeRangeMetrics(size, data)
                chartMetrics = metrics

                ChartDraw.drawGrid(this, size, metrics, yPosition)
                ChartDraw.drawYAxis(this, metrics, yPosition)
                ChartDraw.Bar.drawBarXAxisLabels(drawContext, labels, metrics, maxXTicksLimit = maxXTicksLimit)
            }

            // Conditional interaction based on interactionType parameter
            when (interactionType) {
                InteractionType.BAR -> {
                    // Interactive range bars
                    chartMetrics?.let { metrics ->
                        ChartDraw.Bar.BarMarker(
                            data = data,
                            minValues = data.map { it.yMin },
                            maxValues = data.map { it.yMax },
                            metrics = metrics,
                            color = barColor,
                            barWidthRatio = barWidthRatio,
                            interactive = true,
                            onBarClick = { index, tooltipText ->
                                selectedBarIndex = if (selectedBarIndex == index) null else index
                                onBarClick?.invoke(index, data[index])
                            },
                            chartType = chartType,
                            showTooltipForIndex = selectedBarIndex
                        )
                    }
                }
                InteractionType.TOUCH_AREA -> {
                    // Non-interactive range bars
                    chartMetrics?.let { metrics ->
                        ChartDraw.Bar.BarMarker(
                            data = data,
                            minValues = data.map { it.yMin },
                            maxValues = data.map { it.yMax },
                            metrics = metrics,
                            color = barColor,
                            barWidthRatio = barWidthRatio,
                            interactive = false,
                            chartType = chartType,
                            showTooltipForIndex = selectedBarIndex
                        )
                    }

                    chartMetrics?.let { metrics ->
                        ChartDraw.Bar.BarMarker(
                            data = data,
                            minValues = List(data.size) { metrics.minY },
                            maxValues = data.map { it.yMax },
                            metrics = metrics,
                            onBarClick = { index, _ ->
                                selectedBarIndex = if (selectedBarIndex == index) null else index
                                onBarClick?.invoke(index, data[index])
                            },
                            chartType = chartType,
                            showTooltipForIndex = selectedBarIndex,
                            isTouchArea = true
                        )
                    }
                }
                else -> {
                    // Default case - no interaction
                    chartMetrics?.let { metrics ->
                        ChartDraw.Bar.BarMarker(
                            data = data,
                            minValues = data.map { it.yMin },
                            maxValues = data.map { it.yMax },
                            metrics = metrics,
                            color = barColor,
                            barWidthRatio = barWidthRatio,
                            interactive = false,
                            chartType = chartType,
                            showTooltipForIndex = null
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}
