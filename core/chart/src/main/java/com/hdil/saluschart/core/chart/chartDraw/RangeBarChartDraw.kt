package com.hdil.saluschart.core.chart.chartDraw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.hdil.saluschart.core.chart.RangeChartPoint
import com.hdil.saluschart.core.chart.chartMath.ChartMath

object RangeBarChartDraw {
    /**
     * 범위 바 차트의 막대들을 그립니다.
     * 각 막대는 yMin에서 yMax까지의 범위를 표시합니다.
     *
     * @param drawScope 그리기 영역
     * @param data 범위 차트 데이터 포인트 목록
     * @param metrics 차트 메트릭 정보
     * @param color 바 색상
     * @param barWidthRatio 바 너비 비율 (0.0 ~ 1.0, 기본값 0.6)
     */
    fun drawRangeBars(
        drawScope: DrawScope,
        data: List<RangeChartPoint>,
        metrics: ChartMath.ChartMetrics,
        color: Color,
        barWidthRatio: Float = 0.6f
    ) {
        val barWidth = (metrics.chartWidth / data.size) * barWidthRatio
        val spacing = metrics.chartWidth / data.size

        data.forEachIndexed { i, rangePoint ->
            val yMinScreen = metrics.chartHeight - ((rangePoint.yMin - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
            val yMaxScreen = metrics.chartHeight - ((rangePoint.yMax - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight

            val barHeight = yMinScreen - yMaxScreen // 범위의 높이
            val barX = metrics.paddingX + (spacing - barWidth) / 2 + i * spacing

            drawScope.drawRect(
                color = color,
                topLeft = Offset(barX, yMaxScreen),
                size = Size(barWidth, barHeight)
            )
        }
    }
}