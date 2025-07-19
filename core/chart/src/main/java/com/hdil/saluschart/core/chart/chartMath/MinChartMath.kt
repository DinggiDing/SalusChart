package com.hdil.saluschart.core.chart.chartMath

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.RangeChartPoint

object MinChartMath {
    
    /**
     * 미니멀 차트용 기본 메트릭을 계산합니다 (축이나 레이블 없이).
     * 
     * @param size 캔버스 크기
     * @param values Y축 데이터 값 목록
     * @param padding 차트 주변 패딩
     * @return Triple<너비, 높이, 값 범위>
     */
    fun computeMinimalMetrics(
        size: Size,
        values: List<Float>,
        padding: Float = 8f
    ): Triple<Float, Float, Pair<Float, Float>> {
        val chartWidth = size.width - (padding * 2)
        val chartHeight = size.height - (padding * 2)
        
        val minValue = values.minOrNull() ?: 0f
        val maxValue = values.maxOrNull() ?: 1f
        val range = if (maxValue == minValue) 1f else maxValue - minValue
        
        return Triple(chartWidth, chartHeight, Pair(minValue, maxValue))
    }
    
    /**
     * 미니멀 바 차트용 바 위치를 계산합니다.
     * 
     * @param size 캔버스 크기
     * @param values 데이터 값들
     * @param padding 패딩
     * @return List<바 정보: (x, y, 너비, 높이)>
     */
    fun computeMinimalBarPositions(
        size: Size,
        values: List<Float>,
        padding: Float = 8f
    ): List<BarPosition> {
        val (chartWidth, chartHeight, range) = computeMinimalMetrics(size, values, padding)
        val (minValue, maxValue) = range
        
        val barWidth = chartWidth / values.size * 0.8f // 80% 너비로 간격 생성
        val spacing = chartWidth / values.size
        
        return values.mapIndexed { index, value ->
            val normalizedValue = if (maxValue == minValue) 0.5f else (value - minValue) / (maxValue - minValue)
            val barHeight = chartHeight * normalizedValue
            
            val x = padding + index * spacing + (spacing - barWidth) / 2f
            val y = size.height - padding - barHeight
            
            BarPosition(x, y, barWidth, barHeight)
        }
    }
    
    /**
     * 미니멀 라인 차트용 포인트들을 계산합니다.
     * 
     * @param size 캔버스 크기
     * @param data 차트 데이터 포인트들
     * @param padding 패딩
     * @return 화면 좌표로 변환된 포인트들
     */
    fun computeMinimalLinePoints(
        size: Size,
        data: List<ChartPoint>,
        padding: Float = 8f
    ): List<Offset> {
        if (data.isEmpty()) return emptyList()
        
        val values = data.map { it.y }
        val (chartWidth, chartHeight, range) = computeMinimalMetrics(size, values, padding)
        val (minValue, maxValue) = range
        
        val spacing = if (data.size > 1) chartWidth / (data.size - 1) else 0f
        
        return data.mapIndexed { index, point ->
            val normalizedValue = if (maxValue == minValue) 0.5f else (point.y - minValue) / (maxValue - minValue)
            
            val x = padding + index * spacing
            val y = size.height - padding - (chartHeight * normalizedValue)
            
            Offset(x, y)
        }
    }
    
    /**
     * 미니멀 범위 바 차트용 단일 바 위치를 계산합니다.
     * 
     * @param size 캔버스 크기
     * @param rangePoint 범위 데이터 포인트
     * @param padding 패딩
     * @return 범위 바 정보: (전체 컨테이너, 범위 바)
     */
    fun computeMinimalRangeBarPosition(
        size: Size,
        rangePoint: RangeChartPoint,
        padding: Float = 8f
    ): Pair<BarPosition, BarPosition> {
        val chartWidth = size.width - (padding * 2)
        val chartHeight = size.height - (padding * 2)
        
        // 전체 범위를 기준으로 정규화
        val totalRange = rangePoint.yMax - rangePoint.yMin
        val containerHeight = chartHeight * 0.6f // 컨테이너는 60% 높이
        
        // 전체 컨테이너 바 (배경)
        val containerX = padding
        val containerY = (size.height - containerHeight) / 2f
        val container = BarPosition(containerX, containerY, chartWidth, containerHeight)
        
        // 실제 범위 바는 컨테이너 전체를 채움 (단일 데이터 포인트이므로)
        val rangeBar = BarPosition(containerX, containerY, chartWidth, containerHeight)
        
        return Pair(container, rangeBar)
    }
    
    /**
     * 바 위치 정보를 담는 데이터 클래스
     */
    data class BarPosition(
        val x: Float,
        val y: Float,
        val width: Float,
        val height: Float
    )
}
