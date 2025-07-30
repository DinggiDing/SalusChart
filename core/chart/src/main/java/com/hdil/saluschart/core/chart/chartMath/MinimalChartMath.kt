package com.hdil.saluschart.core.chart.chartMath

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.hdil.saluschart.core.chart.RangeChartPoint

object MinimalChartMath {
    
    /**
     * 미니멀 범위 바 차트용 단일 바 위치를 계산합니다.
     * 
     * @param size 캔버스 크기
     * @param rangePoint 범위 데이터 포인트 (yMin, yMax)
     * @param containerMin 컨테이너의 최소값 (전체 범위 시작)
     * @param containerMax 컨테이너의 최대값 (전체 범위 끝)
     * @param padding 패딩
     * @return 범위 바 정보: (컨테이너 위치, 컨테이너 크기, 범위바 위치, 범위바 크기)
     */
    fun computeMinimalRangeBarPosition(
        size: Size,
        rangePoint: RangeChartPoint,
        containerMin: Float,
        containerMax: Float,
        padding: Float = 8f
    ): Pair<Pair<Offset, Size>, Pair<Offset, Size>> {
        val chartWidth = size.width - (padding * 2)
        val chartHeight = size.height - (padding * 2)

        // 텍스트 공간을 예약하는 경우 바 높이를 줄이고 위치를 조정
        val textSpaceHeight = 24f
        val availableBarHeight = chartHeight - textSpaceHeight
        val containerHeight = availableBarHeight * 0.6f // 사용 가능한 높이의 60%

        // 전체 컨테이너 바 (배경)
        val containerX = padding
        val containerY = padding + textSpaceHeight + (availableBarHeight - containerHeight) / 2f
        val containerOffset = Offset(containerX, containerY)
        val containerSize = Size(chartWidth, containerHeight)
        
        val containerRange = containerMax - containerMin
        
        // 데이터 범위가 컨테이너 범위를 벗어나지 않도록 클램핑
        val clampedDataMin = rangePoint.yMin.coerceIn(containerMin, containerMax)
        val clampedDataMax = rangePoint.yMax.coerceIn(containerMin, containerMax)
        
        // 정규화된 위치 계산 (0.0 ~ 1.0)
        val startRatio = if (containerRange > 0) {
            (clampedDataMin - containerMin) / containerRange
        } else 0f
        val endRatio = if (containerRange > 0) {
            (clampedDataMax - containerMin) / containerRange
        } else 1f
        
        // 실제 데이터 범위 바
        val rangeBarX = containerX + (chartWidth * startRatio)
        val rangeBarWidth = chartWidth * (endRatio - startRatio)
        val rangeBarOffset = Offset(rangeBarX, containerY)
        val rangeBarSize = Size(rangeBarWidth, containerHeight)
        
        return Pair(
            Pair(containerOffset, containerSize),
            Pair(rangeBarOffset, rangeBarSize)
        )
    }
}
