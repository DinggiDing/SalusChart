package com.hdil.saluschart.core.transform

import com.hdil.saluschart.core.util.TimeUnitGroup
import com.hdil.saluschart.core.chart.TimeDataPoint
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.time.temporal.WeekFields
import java.util.*
import kotlin.div
import kotlin.text.toInt
import kotlin.text.toLong

/**
 * 시간 기반 데이터 변환 엔진
 */
class DataTransformer {

    /**
     * 시간 기반 데이터를 차트 포인트로 변환
     * @param data 원시 시간 데이터 리스트
     * @param timeUnit 그룹핑할 시간 단위
     * @param filtering 필터링 옵션
     */
    fun transform(
        data: TimeDataPoint,
        transformTimeUnit: TimeUnitGroup,
//        filtering: Filtering = Filtering.ALL,
    ): TimeDataPoint {

//        // 시간 단위 변환 검증 (작은 단위 -> 큰 단위만 허용)
//        require(data.timeUnit.isSmallerThan(transformTimeUnit) || data.timeUnit == transformTimeUnit) {
//            "시간 단위 변환은 증가 방향으로만 가능합니다: ${data.timeUnit} -> $transformTimeUnit"
//        }

        // 같은 시간 단위인 경우 그대로 반환
        if (data.timeUnit == transformTimeUnit) {
            return data
        }

        val transformedData = groupByTimeUnit(data, transformTimeUnit)
        return transformedData
    }
//
//    /**
//     * 필터링 적용
//     */
//    private fun applyFiltering(
//        data: List<TimeDataPoint>,
//        filtering: Filtering
//    ): List<TimeDataPoint> {
//        val now = LocalDateTime.now()
//        return when (filtering) {
//            Filtering.ALL -> data
//            Filtering.LAST_WEEK -> data.filter {
//                ChronoUnit.DAYS.between(it.timestamp, now) <= 7
//            }
//            Filtering.LAST_MONTH -> data.filter {
//                ChronoUnit.DAYS.between(it.timestamp, now) <= 30
//            }
//            Filtering.LAST_YEAR -> data.filter {
//                ChronoUnit.DAYS.between(it.timestamp, now) <= 365
//            }
//        }
//    }

    /**
     * ISO 8601 문자열을 LocalDateTime으로 파싱
     */
    private fun parseIsoString(isoString: String): LocalDateTime {
        return try {
            // ISO_DATE_TIME은 "Z" 접미사를 포함한 표준 ISO 8601 형식을 지원
            if (isoString.endsWith("Z")) {
                // UTC 타임존 표시가 있는 경우, ZonedDateTime으로 파싱 후 LocalDateTime으로 변환
                ZonedDateTime.parse(isoString).toLocalDateTime()
            } else {
                LocalDateTime.parse(isoString, DateTimeFormatter.ISO_DATE_TIME)
            }
        } catch (e: Exception) {
            // 백업 파서: 다양한 형식 시도
            try {
                LocalDateTime.parse(isoString.replace("Z", ""), DateTimeFormatter.ISO_DATE_TIME)
            } catch (e2: Exception) {
                throw IllegalArgumentException("ISO 문자열 파싱 실패: $isoString", e)
            }
        }
    }

    /**
     * 시간 단위별 그룹핑
     */
    private fun groupByTimeUnit(
        data: TimeDataPoint,
        targetTimeUnit: TimeUnitGroup
    ): TimeDataPoint {

        // ISO 문자열을 LocalDateTime으로 변환
        val parsedTimes = data.x.map { parseIsoString(it) }

        // 시간별 데이터 페어링
        val timeValuePairs = parsedTimes.zip(data.y)

        // 목표 시간 단위에 따라 그룹핑
        val groupedData = when (targetTimeUnit) {
            TimeUnitGroup.HOUR -> timeValuePairs.map { it.first to listOf(it.second) }.toMap()
            TimeUnitGroup.DAY -> groupByDay(timeValuePairs)
            TimeUnitGroup.WEEK -> groupByWeek(timeValuePairs)
            TimeUnitGroup.MONTH -> groupByMonth(timeValuePairs)
            TimeUnitGroup.YEAR -> groupByYear(timeValuePairs)
        }

        // 그룹별 합계 계산
        val aggregatedData = groupedData.map { (time, values) ->
            time to values.sum().toFloat()
        }.sortedBy { it.first }

//        Log.d("DataTransformer", "집계된 데이터 크기: ${aggregatedData.size}")
//        aggregatedData.take(3).forEach { (time, sum) ->
//            Log.d("DataTransformer", "집계 결과 $time: $sum")
//        }

        // 결과를 ISO 문자열과 값 리스트로 변환
        val newXValues = aggregatedData.map { formatTimeToIso(it.first, targetTimeUnit) }
        val newYValues = aggregatedData.map { it.second }

        // X축 레이블 생성
        val xAxisLabels = aggregatedData.map { (time, _) ->
            formatDateLabel(time, targetTimeUnit)
        }

        return TimeDataPoint(
            x = newXValues,
            y = newYValues,
            timeUnit = targetTimeUnit,
            label = null // 단순화: 레이블은 toChartPoints()에서 처리
        )
    }

    /**
     * 일별 그룹핑
     */
    private fun groupByDay(timeValuePairs: List<Pair<LocalDateTime, Float>>): Map<LocalDateTime, List<Float>> {
        return timeValuePairs.groupBy { (time, _) ->
            time.truncatedTo(ChronoUnit.DAYS)
        }.mapValues { (_, pairs) -> pairs.map { it.second } }
    }

    /**
     * 주별 그룹핑 (월요일 기준)
     */
    private fun groupByWeek(timeValuePairs: List<Pair<LocalDateTime, Float>>): Map<LocalDateTime, List<Float>> {
        return timeValuePairs.groupBy { (time, _) ->
            val monday = time.toLocalDate().minusDays((time.dayOfWeek.value - 1).toLong())
            monday.atStartOfDay()
        }.mapValues { (_, pairs) -> pairs.map { it.second } }
    }

    /**
     * 월별 그룹핑
     */
    private fun groupByMonth(timeValuePairs: List<Pair<LocalDateTime, Float>>): Map<LocalDateTime, List<Float>> {
        return timeValuePairs.groupBy { (time, _) ->
            LocalDateTime.of(time.year, time.month, 1, 0, 0)
        }.mapValues { (_, pairs) -> pairs.map { it.second } }
    }

    /**
     * 연별 그룹핑
     */
    private fun groupByYear(timeValuePairs: List<Pair<LocalDateTime, Float>>): Map<LocalDateTime, List<Float>> {
        return timeValuePairs.groupBy { (time, _) ->
            LocalDateTime.of(time.year, 1, 1, 0, 0)
        }.mapValues { (_, pairs) -> pairs.map { it.second } }
    }

    /**
     * 시간 단위에 맞게 LocalDateTime을 ISO 문자열로 포맷팅
     */
    private fun formatTimeToIso(time: LocalDateTime, timeUnit: TimeUnitGroup): String {
        val normalizedTime = when (timeUnit) {
            TimeUnitGroup.HOUR -> time
            TimeUnitGroup.DAY -> time.truncatedTo(ChronoUnit.DAYS)
            TimeUnitGroup.WEEK -> {
                val monday = time.toLocalDate().minusDays((time.dayOfWeek.value - 1).toLong())
                monday.atStartOfDay()
            }
            TimeUnitGroup.MONTH -> LocalDateTime.of(time.year, time.month, 1, 0, 0)
            TimeUnitGroup.YEAR -> LocalDateTime.of(time.year, 1, 1, 0, 0)
        }
        return normalizedTime.format(DateTimeFormatter.ISO_DATE_TIME) + "Z"
    }

    /**
     * 날짜 레이블 포맷팅
     * HOUR: "14시" (for 2 PM)
     * DAY: "5/4" (for May 4th)
     * WEEK: "5월 1주차" (for first week of May)
     * MONTH: "2025년 5월" (for May 2025)
     * YEAR: "2025년" (for year 2025)
     */
    private fun formatDateLabel(time: LocalDateTime, timeUnit: TimeUnitGroup): String {
        return when (timeUnit) {
            TimeUnitGroup.HOUR -> "${time.hour}시"
            TimeUnitGroup.DAY -> "${time.monthValue}/${time.dayOfMonth}"
            TimeUnitGroup.WEEK -> {
                // 월요일 기준으로 해당 월의 몇 번째 주인지 계산
                val monday = time.toLocalDate().minusDays((time.dayOfWeek.value - 1).toLong())
                val firstMondayOfMonth = LocalDate.of(monday.year, monday.month, 1)
                    .let { firstDay ->
                        // 해당 월 1일이 속한 주의 월요일 찾기
                        val dayOfWeek = firstDay.dayOfWeek.value
                        if (dayOfWeek == 1) firstDay else firstDay.minusDays((dayOfWeek - 1).toLong())
                    }

                val weekNumber = ((monday.toEpochDay() - firstMondayOfMonth.toEpochDay()) / 7 + 1).toInt()
                "${monday.monthValue}월 ${weekNumber}주차"
            }
            TimeUnitGroup.MONTH -> "${time.year}년 ${time.monthValue}월"
            TimeUnitGroup.YEAR -> "${time.year}년"
        }
    }
}