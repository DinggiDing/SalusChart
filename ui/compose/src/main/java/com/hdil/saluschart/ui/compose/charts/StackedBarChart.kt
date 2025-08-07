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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.StackedChartPoint

/**
 * 스택 바 차트 컴포저블 (건강 데이터 시각화에 최적화)
 * 
 * 영양소 섭취량, 운동 시간 등 여러 구성 요소를 가진 데이터를 시각화할 때 사용합니다.
 * 예: 일별 영양소 섭취량 (단백질, 지방, 탄수화물)
 * 
 * @param modifier 커스텀 modifier
 * @param data 스택 차트 데이터 포인트 목록
 * @param segmentLabels 각 세그먼트의 레이블들 (예: ["단백질", "지방", "탄수화물"])
 * @param xLabel X축 레이블 (예: "날짜")
 * @param yLabel Y축 레이블 (예: "영양소 (g)")
 * @param title 차트 제목
 * @param colors 각 세그먼트의 색상 팔레트 (건강 데이터에 적합한 기본 색상 제공)
 * @param width 차트 너비
 * @param height 차트 높이
 * @param barWidthRatio 바 너비 비율 (0.0 ~ 1.0)
 * @param showLegend 범례 표시 여부
 */
@Composable
fun StackedBarChart(
    modifier: Modifier = Modifier,
    data: List<StackedChartPoint>,
    segmentLabels: List<String> = emptyList(),
    xLabel: String = "Time",
    yLabel: String = "Value",
    title: String = "Stacked Bar Chart",
    colors: List<Color> = listOf(
        Color(0xFF2196F3), // 파랑 (단백질)
        Color(0xFFFF9800), // 주황 (지방)
        Color(0xFF4CAF50), // 초록 (탄수화물)
        Color(0xFF9C27B0), // 보라 (기타)
        Color(0xFFE91E63), // 분홍
        Color(0xFFFFEB3B), // 노랑
    ),
    width: Dp = 300.dp,
    height: Dp = 300.dp,
    barWidthRatio: Float = 0.6f,
    showLegend: Boolean = true,
    chartType: ChartType = ChartType.STACKED_BAR // 차트 타입 (툴팁 위치 결정용)
) {
    if (data.isEmpty()) return

    val xLabels = data.map { it.label ?: it.x.toString() }
    
    Column(modifier = modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Box(
            Modifier
                .width(width)
                .height(height)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val totalValues = data.map { it.total }
                val metrics = ChartMath.computeMetrics(size, totalValues, chartType = ChartType.STACKED_BAR)

                ChartDraw.drawGrid(this, size, metrics)
                ChartDraw.Bar.drawStackedBars(this, data, metrics, colors, barWidthRatio)
                ChartDraw.drawXAxis(this, metrics)
                ChartDraw.drawYAxis(this, metrics)
                ChartDraw.Bar.drawBarXAxisLabels(drawContext, xLabels, metrics)
                
                // 범례 그리기 (통합된 범례 시스템 사용)
                if (showLegend && segmentLabels.isNotEmpty()) {
                    val legendPosition = Offset(size.width, 20f)
                    ChartDraw.drawChartLegend(
                        drawScope = this,
                        labels = segmentLabels,
                        colors = colors,
                        position = legendPosition,
                        chartSize = size,
                        title = null, 
                        itemHeight = 18f
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}
