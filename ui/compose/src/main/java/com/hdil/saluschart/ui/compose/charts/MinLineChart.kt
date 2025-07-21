package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath

/**
 * 미니멀 라인 차트 (스파크라인) - 위젯이나 스마트워치 등 작은 화면용
 * 축, 그리드, 레이블 없이 순수 라인만 표시
 * 
 * @param modifier 모디파이어
 * @param data 라인 차트 데이터 포인트들
 * @param color 라인 색상
 * @param width 차트 너비
 * @param height 차트 높이
 * @param strokeWidth 라인 두께
 * @param padding 차트 주변 패딩
 * @param showPoints 끝점을 원으로 표시할지 여부
 */
@Composable
fun MinLineChart(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,
    color: Color = Color.Blue,
    width: Dp = 100.dp,
    height: Dp = 40.dp,
    strokeWidth: Float = 2f,
    padding: Float = 4f,
    showPoints: Boolean = false
) {
    if (data.isEmpty()) return

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
            // 라인 포인트 계산
            val linePoints = ChartMath.Min.computeMinimalLinePoints(
                size = size,
                data = data,
                padding = padding
            )
            
            // 라인 그리기
            ChartDraw.Min.drawMinimalLine(
                drawScope = this,
                points = linePoints,
                color = color,
                strokeWidth = strokeWidth
            )
            
            // 끝점 표시 (옵션)
            if (showPoints && linePoints.isNotEmpty()) {
                val endPoints = listOf(linePoints.first(), linePoints.last())
                ChartDraw.Min.drawMinimalPoints(
                    drawScope = this,
                    points = endPoints,
                    color = color,
                    radius = strokeWidth * 1.5f
                )
            }
        }
    }
}

/**
 * Float 값들로부터 ChartPoint 리스트를 생성하는 편의 함수
 */
fun createLineData(values: List<Float>): List<ChartPoint> {
    return values.mapIndexed { index, value ->
        ChartPoint(x = index.toFloat(), y = value)
    }
}
