package com.hdil.saluschart.core.chart

import com.hdil.saluschart.core.util.TimeUnitGroup
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
 * @param segmentColors 각 세그먼트의 색상 (null인 경우 기본 색상 팔레트 사용)
 * @param isSelected 선택 상태 여부
 */
data class StackedChartPoint(
    val x: Float,
    val values: List<Float>,
    val label: String? = null,
    val segmentColors: List<Int>? = null,
    val isSelected: Boolean = false
) {

    val total: Float = values.sum()
    
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
    val x: Float,
    val current: Float,
    val max: Float,
    val label: String? = null,
    val unit: String? = null,
    val color: Int? = null,
    val isSelected: Boolean = false
) {
    val progress: Float = if (max > 0f) (current / max).coerceIn(0f, 1f) else 0f
    val percentage: Float = progress * 100f
    
    override fun toString(): String {
        return "ProgressChartPoint(x=$x, current=$current, max=$max, progress=$progress, label=$label, unit=$unit)"
    }
}

/**
 * 시간 기반 원시 데이터 포인트
 * ChartPoint로 변환해서 사용
 * @param timestamp 데이터 발생 시각
 * @param value 측정값
 * @param label 데이터 레이블 (선택사항)
 */
data class TimeDataPoint(
    val x : List<String>,
    val y : List<Float>,
    val timeUnit : TimeUnitGroup = TimeUnitGroup.HOUR,
    val label: String? = null
)

/**
 * TimeDataPoint를 ChartPoint 리스트로 변환하는 확장 함수
 *
 * @return ChartPoint 리스트
 */
fun TimeDataPoint.toChartPoints(): List<ChartPoint> {
    // DataTransformer에서 변환된 데이터의 경우, 적절한 레이블 생성
    val labels = when (timeUnit) {
        TimeUnitGroup.HOUR -> {
            // ISO 타임스탬프에서 시간 추출하여 "시간시" 형태로 변환
            x.map { isoString ->
                try {
                    val cleanedString = isoString.replace("Z", "")
                    val dateTime = LocalDateTime.parse(cleanedString, DateTimeFormatter.ISO_DATE_TIME)
                    "${dateTime.hour}시"
                } catch (e: Exception) {
                    isoString // 파싱 실패시 원본 문자열 사용
                }
            }
        }
        TimeUnitGroup.DAY -> {
            // ISO 타임스탬프에서 날짜 추출하여 "월/일" 형태로 변환
            x.map { isoString ->
                try {
                    val cleanedString = isoString.replace("Z", "")
                    val dateTime = LocalDateTime.parse(cleanedString, DateTimeFormatter.ISO_DATE_TIME)
                    "${dateTime.monthValue}/${dateTime.dayOfMonth}"
                } catch (e: Exception) {
                    isoString // 파싱 실패시 원본 문자열 사용
                }
            }
        }
        TimeUnitGroup.WEEK -> {
            // ISO 타임스탬프에서 주 정보 추출하여 "월 주차" 형태로 변환
            x.map { isoString ->
                try {
                    val cleanedString = isoString.replace("Z", "")
                    val dateTime = LocalDateTime.parse(cleanedString, DateTimeFormatter.ISO_DATE_TIME)
                    val monday = dateTime.toLocalDate().minusDays((dateTime.dayOfWeek.value - 1).toLong())
                    val firstMondayOfMonth = LocalDate.of(monday.year, monday.month, 1)
                        .let { firstDay ->
                            val dayOfWeek = firstDay.dayOfWeek.value
                            if (dayOfWeek == 1) firstDay else firstDay.minusDays((dayOfWeek - 1).toLong())
                        }
                    val weekNumber = ((monday.toEpochDay() - firstMondayOfMonth.toEpochDay()) / 7 + 1).toInt()
                    "${monday.monthValue}월 ${weekNumber}주차"
                } catch (e: Exception) {
                    isoString // 파싱 실패시 원본 문자열 사용
                }
            }
        }
        TimeUnitGroup.MONTH -> {
            // ISO 타임스탬프에서 월 정보 추출하여 "년 월" 형태로 변환
            x.map { isoString ->
                try {
                    val cleanedString = isoString.replace("Z", "")
                    val dateTime = LocalDateTime.parse(cleanedString, DateTimeFormatter.ISO_DATE_TIME)
                    "${dateTime.year}년 ${dateTime.monthValue}월"
                } catch (e: Exception) {
                    isoString // 파싱 실패시 원본 문자열 사용
                }
            }
        }
        TimeUnitGroup.YEAR -> {
            // ISO 타임스탬프에서 연도 정보 추출하여 "년" 형태로 변환
            x.map { isoString ->
                try {
                    val cleanedString = isoString.replace("Z", "")
                    val dateTime = LocalDateTime.parse(cleanedString, DateTimeFormatter.ISO_DATE_TIME)
                    "${dateTime.year}년"
                } catch (e: Exception) {
                    isoString // 파싱 실패시 원본 문자열 사용
                }
            }
        }
    }

    return x.indices.map { index ->
        ChartPoint(
            x = index.toFloat(),
            y = y[index],
            label = labels.getOrNull(index) ?: x.getOrNull(index)
        )
    }
}
