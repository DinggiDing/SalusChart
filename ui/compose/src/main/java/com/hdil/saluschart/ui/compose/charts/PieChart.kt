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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.ui.theme.ChartColor
import com.hdil.saluschart.ui.theme.ColorUtils

/**
 * 파이 차트를 표시하는 컴포저블 함수입니다.
 *
 * @param modifier 모디파이어
 * @param data 파이 차트에 표시할 데이터 포인트 목록
 * @param title 차트 제목
 * @param isDonut 도넛 차트로 표시할지 여부 (기본값: true)
 * @param colors 각 조각에 사용할 색상 목록
 * @param showLegend 범례를 표시할지 여부
 * @param width 차트의 너비
 * @param height 차트의 높이
 */
@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,
    title: String = "Pie Chart Example",
    isDonut: Boolean = true,
    colors: List<androidx.compose.ui.graphics.Color> = ColorUtils.ColorUtils(data.size.coerceAtLeast(1)),
    showLegend: Boolean = false,
    width: Dp = 250.dp,
    height: Dp = 250.dp
) {
    if (data.isEmpty()) return

    Column(modifier = modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Box(
            Modifier
                .width(width)
                .height(height),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 파이 차트의 핵심 요소 그리기 (섹션 및 레이블)
                val (center, radius) = ChartMath.Pie.computePieMetrics(size)
                val sections = ChartMath.Pie.computePieAngles(data)

                if (sections.isNotEmpty()) {
                    // 각 섹션 그리기
                    sections.forEachIndexed { i, (startAngle, sweepAngle, _) ->
                        val colorIndex = i % colors.size
                        ChartDraw.drawPieSection(
                            drawScope = this,
                            center = center,
                            radius = radius,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle,
                            color = colors[colorIndex],
                            isDonut = isDonut,
                            strokeWidth = 60f
                        )
                    }

                    // 레이블 그리기
                    ChartDraw.drawPieLabels(
                        drawScope = this,
                        center = center,
                        radius = radius,
                        data = data,
                        sections = sections
                    )

                    // 범례 그리기
                    if (showLegend) {
                        val legendPosition = Offset(size.width, 20f)
                        ChartDraw.drawChartLegend(
                            drawScope = this,
                            chartData = data,
                            colors = colors,
                            position = legendPosition,
                            chartSize = size,
                            itemHeight = 40f
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}