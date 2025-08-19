package com.hdil.saluschart.core.chart

import com.hdil.saluschart.core.util.TimeUnitGroup
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.time.DayOfWeek


/**
 * 시간 기반 원시 데이터 포인트
 * ChartPoint로 변환해서 사용
 * @param x 시간 데이터 (Instant 타입)
 * @param y 측정값 리스트
 * @param timeUnit 시간 단위
 * @param label 데이터 레이블 (선택사항)
 */
data class TimeDataPoint(
    val x : List<Instant>,
    val y : List<Float>,
    val timeUnit : TimeUnitGroup = TimeUnitGroup.HOUR,
    val label: String? = null
)

/**
 * TimeDataPoint를 ChartPoint 리스트로 변환하는 확장 함수
 *
 * @return ChartPoint 리스트
 *
 * 각 시간 단위에 따라 레이블이 생성됩니다:
 * HOUR: "14시" (for 2 PM)
 * DAY: "5/4" (for May 4th)
 * WEEKDAY: "월" (for Monday)
 * WEEK: "5월 1주차" (for first week of May)
 * MONTH: "2025년 5월" (for May 2025)
 * YEAR: "2025년" (for year 2025)
 */
fun TimeDataPoint.toChartPoints(): List<ChartPoint> {
    // DataTransformer에서 변환된 데이터의 경우, 적절한 레이블 생성
    val labels = when (timeUnit) {
        TimeUnitGroup.HOUR -> {
            // Instant에서 시간 추출하여 "시간시" 형태로 변환
            x.map { instant ->
                val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                "${dateTime.hour}시"
            }
        }
        TimeUnitGroup.DAY -> {
            // Instant에서 날짜 추출하여 "월/일" 형태로 변환
            x.map { instant ->
                val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                "${dateTime.monthValue}/${dateTime.dayOfMonth}"
            }
        }
        TimeUnitGroup.WEEKDAY -> {
            // Instant에서 요일 추출하여 한국어 요일명으로 변환 (일요일부터 토요일까지)
            x.map { instant ->
                val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                when (dateTime.dayOfWeek.value) {
                    1 -> "월"
                    2 -> "화"
                    3 -> "수"
                    4 -> "목"
                    5 -> "금"
                    6 -> "토"
                    7 -> "일"
                    else -> "알 수 없음"
                }
            }
        }
        TimeUnitGroup.WEEK -> {
            // Instant에서 주 정보 추출하여 "월 주차" 형태로 변환 (일요일 기준)
            x.map { instant ->
                val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                // 일요일을 기준으로 해당 주의 시작 날짜 계산
                val sunday = dateTime.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                // 해당 월의 첫 번째 일요일 찾기
                val firstSundayOfMonth = LocalDate.of(sunday.year, sunday.month, 1)
                    .let { firstDay ->
                        val dayOfWeek = firstDay.dayOfWeek.value
                        if (dayOfWeek == 7) firstDay else firstDay.plusDays((7 - dayOfWeek).toLong())
                    }
                val weekNumber = ((sunday.toEpochDay() - firstSundayOfMonth.toEpochDay()) / 7 + 1).toInt()
                "${sunday.monthValue}월 ${weekNumber}주차"
            }
        }
        TimeUnitGroup.MONTH -> {
            // Instant에서 월 정보 추출하여 "년 월" 형태로 변환
            x.map { instant ->
                val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                "${dateTime.year}년 ${dateTime.monthValue}월"
            }
        }
        TimeUnitGroup.YEAR -> {
            // Instant에서 연도 정보 추출하여 "년" 형태로 변환
            x.map { instant ->
                val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                "${dateTime.year}년"
            }
        }
    }

    return x.indices.map { index ->
        ChartPoint(
            x = index.toFloat(),
            y = y[index],
            label = labels.getOrNull(index) ?: x.getOrNull(index)?.toString()
        )
    }
}
