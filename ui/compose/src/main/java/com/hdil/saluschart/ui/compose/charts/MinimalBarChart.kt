package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.chartDraw.BarChartDraw
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.core.chart.ChartType

/**
 * 미니멀 바 차트 - 위젯이나 스마트워치 등 작은 화면용
 * 축, 그리드, 레이블 없이 순수 바 차트만 표시
 *
 * @param modifier 모디파이어
 * @param data 바 차트 데이터 값들
 * @param color 바 색상
 * @param width 차트 너비
 * @param height 차트 높이
 * @param padding 차트 주변 패딩
 */
@Composable
fun MinimalBarChart(
    modifier: Modifier = Modifier,
    data: List<Float>,
    color: Color = Color.Blue,
    width: Dp = 100.dp,
    height: Dp = 40.dp,
    padding: Float = 4f,
    chartType: ChartType = ChartType.MINIMAL_BAR // 차트 타입 (툴팁 위치 결정용
) {
    if (data.isEmpty()) return

    var chartMetrics by remember { mutableStateOf<ChartMath.ChartMetrics?>(null) }

    Box(
        modifier = modifier
            .then(
                if (width != Dp.Unspecified && height != Dp.Unspecified) {
                    Modifier.size(width, height)
                } else {
                    Modifier.fillMaxSize()
                }
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val metrics = ChartMath.computeMetrics(
                size = size,
                values = data,
                isMinimal = true,
                paddingX = padding,
                paddingY = padding,
                chartType = ChartType.MINIMAL_BAR
            )

            // Store metrics for BarMarker
            chartMetrics = metrics
        }

        // Visual bars (non-interactive) using BarMarker
        chartMetrics?.let { metrics ->
            ChartDraw.Bar.BarMarker(
                minValues = List(data.size) { 0f },
                maxValues = data,
                metrics = metrics,
                color = color,
                barWidthRatio = 0.8f,
                interactive = false,
                chartType = chartType
            )
        }
    }
}
