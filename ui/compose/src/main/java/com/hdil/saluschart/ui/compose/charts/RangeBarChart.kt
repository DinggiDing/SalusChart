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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.RangeChartPoint
import com.hdil.saluschart.ui.theme.ChartColor

@Composable
fun RangeBarChart(
    modifier: Modifier = Modifier,
    data: List<RangeChartPoint>,
    xLabel: String = "Time",
    yLabel: String = "Value", 
    title: String = "Range Bar Chart",
    barColor: androidx.compose.ui.graphics.Color = ChartColor.Default,
    width: Dp = 250.dp,
    height: Dp = 250.dp,
    barWidthRatio: Float = 0.6f,
    chartType: ChartType = ChartType.RANGE_BAR // 차트 타입 (툴팁 위치 결정용)
) {
    if (data.isEmpty()) return
    
    val labels = data.map { it.label ?: it.x.toString() }

    Column(modifier = modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Box(
            Modifier
                .width(width)
                .height(height)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val metrics = ChartMath.RangeBar.computeRangeMetrics(size, data)

                ChartDraw.drawGrid(this, size, metrics)
                ChartDraw.drawYAxis(this, metrics)
                ChartDraw.RangeBar.drawRangeBars(this, data, metrics, barColor, barWidthRatio)
                ChartDraw.Bar.drawBarXAxisLabels(drawContext, labels, metrics)
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}
