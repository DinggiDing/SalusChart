package com.hdil.saluschart.core.chart

interface BaseChartPoint {
    val x: Float
    val y: Float
    val label: String?
}

data class ChartPoint(
    override val x: Float,
    override val y: Float,
    override val label: String? = null,
    val color: Int? = null,
    val isSelected: Boolean = false
) : BaseChartPoint {
    override fun toString(): String {
        return "ChartPoint(x=$x, y=$y, label=$label, color=$color, isSelected=$isSelected)"
    }
}

/**
 * 범위 바 차트를 위한 데이터 포인트 클래스
 */
data class RangeChartPoint(
    override val x: Float,
    val yMin: Float,
    val yMax: Float,
    override val label: String? = null,
    val color: Int? = null,
    val isSelected: Boolean = false
) : BaseChartPoint {
    override val y: Float get() = yMax - yMin
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
 * @param segmentColors 각 세그먼트의 색상 (null인 경우 기본 색상 팔레트 사용)
 * @param isSelected 선택 상태 여부
 */
data class StackedChartPoint(
    override val x: Float,
    val values: List<Float>,
    override val label: String? = null,
    val segmentColors: List<Int>? = null,
    val isSelected: Boolean = false
) : BaseChartPoint {
    val total: Float = values.sum()

    override val y: Float get() = total // TODO : y값을 어떻게 정의할지 논의 필요 (현재는 그냥 총합으로 설정)
    
    override fun toString(): String {
        return "StackedChartPoint(x=$x, values=$values, total=$total, label=$label, isSelected=$isSelected)"
    }
}

/**
 * 프로그레스 차트를 위한 데이터 포인트 클래스
 * 
 * @param x X축 위치 또는 인덱스 (0, 1, 2 for Move, Exercise, Stand)
 * @param current 현재 값
 * @param max 최대 값
 * @param label 라벨 (예: "Move", "Exercise", "Stand")
 * @param unit 단위 (예: "KJ", "min", "h")
 * @param color 색상 (null인 경우 기본 색상 팔레트 사용)
 * @param isSelected 선택 상태 여부
 */
data class ProgressChartPoint(
    override val x: Float,
    val current: Float,
    val max: Float,
    override val label: String? = null,
    val unit: String? = null,
    val color: Int? = null,
    val isSelected: Boolean = false
) : BaseChartPoint {
    val progress: Float = if (max > 0f) (current / max).coerceIn(0f, 1f) else 0f
    val percentage: Float = progress * 100f
    override val y: Float get() = progress

    override fun toString(): String {
        return "ProgressChartPoint(x=$x, current=$current, max=$max, progress=$progress, label=$label, unit=$unit)"
    }
}
