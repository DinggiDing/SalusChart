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
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.chartDraw.LineChartDraw
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
 * @param chartType 차트 타입 (툴팁 위치 결정용)
 */
@Composable
fun MinimalLineChart(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,
    color: Color = Color.Blue,
    strokeWidth: Float = 2f,
    padding: Float = 4f,
    showPoints: Boolean = false,
    chartType: ChartType = ChartType.MINIMAL_LINE // 차트 타입 (툴팁 위치 결정용)
) {
    if (data.isEmpty()) return

    Box(
        modifier = modifier
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 미니멀 차트 메트릭 계산
            val metrics = ChartMath.computeMetrics(
                size = size,
                values = data.map { it.y },
                isMinimal = true,
                paddingX = padding,
                paddingY = padding
            )
            val points = ChartMath.mapToCanvasPoints(data, size, metrics)

            // 라인 그리기 (포인트 표시 포함)
            LineChartDraw.drawLine(this, points, color, strokeWidth)
        }
    }
}
