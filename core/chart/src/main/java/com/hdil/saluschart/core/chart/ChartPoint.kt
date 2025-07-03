package com.hdil.saluschart.core.chart

data class ChartPoint(
    val x: Float,
    val y: Float,
    val label: String? = null,
    val color: Int? = null,
    val isSelected: Boolean = false
) {
    override fun toString(): String {
        return "ChartPoint(x=$x, y=$y, label=$label, color=$color, isSelected=$isSelected)"
    }
}

/**
 * 범위 바 차트를 위한 데이터 포인트 클래스
 * 
 * @param x X축 위치 또는 시간 인덱스
 * @param yMin Y축의 최소값
 * @param yMax Y축의 최대값  
 * @param label X축에 표시할 레이블 (예: 날짜, 시간 등)
 * @param color 바 색상 (null인 경우 기본 색상 사용)
 * @param isSelected 선택 상태 여부
 */
data class RangeChartPoint(
    val x: Float,
    val yMin: Float,
    val yMax: Float,
    val label: String? = null,
    val color: Int? = null,
    val isSelected: Boolean = false
) {
    override fun toString(): String {
        return "RangeChartPoint(x=$x, yMin=$yMin, yMax=$yMax, label=$label, color=$color, isSelected=$isSelected)"
    }
}

/**
 * 스택 바 차트를 위한 데이터 포인트 클래스
 * 
 * @param x X축 위치 또는 시간 인덱스
 * @param values 각 세그먼트의 값들 (예: [단백질, 지방, 탄수화물])
 * @param label X축에 표시할 레이블 (예: 날짜, 요일 등)
 * @param segmentLabels 각 세그먼트의 레이블들 (예: ["단백질", "지방", "탄수화물"])
 * @param segmentColors 각 세그먼트의 색상 (null인 경우 기본 색상 팔레트 사용)
 * @param isSelected 선택 상태 여부
 */
data class StackedChartPoint(
    val x: Float,
    val values: List<Float>,
    val label: String? = null,
    val segmentLabels: List<String>? = null,
    val segmentColors: List<Int>? = null,
    val isSelected: Boolean = false
) {

    val total: Float = values.sum()
    
    override fun toString(): String {
        return "StackedChartPoint(x=$x, values=$values, total=$total, label=$label, segmentLabels=$segmentLabels, isSelected=$isSelected)"
    }
}