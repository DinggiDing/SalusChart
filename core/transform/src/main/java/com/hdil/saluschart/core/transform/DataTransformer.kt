package com.hdil.saluschart.core.transform

import com.hdil.saluschart.core.util.TimeUnitGroup
import com.hdil.saluschart.core.util.AggregationType
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

/**
 * 시간 기반 데이터 변환 엔진
 */
class DataTransformer {

    /**
     * 시간 기반 데이터를 차트 포인트로 변환
     * @param data 원시 시간 데이터 리스트
     * @param transformTimeUnit 그룹핑할 시간 단위
     * @param aggregationType 집계 방법 (합계 또는 평균)
     */
    fun transform(
        data: TimeDataPoint,
        transformTimeUnit: TimeUnitGroup,
        aggregationType: AggregationType = AggregationType.SUM
    ): TimeDataPoint {

        // 평균 계산 시 유효성 검증
        if (aggregationType == AggregationType.AVERAGE) {
            // 원본 시간 단위가 변환 시간 단위보다 작아야 함
            require(data.timeUnit.isSmallerThan(transformTimeUnit)) {
                "평균 계산을 위해서는 원본 시간 단위(${data.timeUnit})가 변환 시간 단위($transformTimeUnit)보다 작아야 합니다."
            }
        }

        // 같은 시간 단위이고 합계 계산인 경우 그대로 반환
        if (data.timeUnit == transformTimeUnit && aggregationType == AggregationType.SUM) {
            return data
        }

        val transformedData = groupByTimeUnit(data, transformTimeUnit, aggregationType)
        return transformedData
    }

    /**
     * 시간 단위별 그룹핑
     */
    private fun groupByTimeUnit(
        data: TimeDataPoint,
        targetTimeUnit: TimeUnitGroup,
        aggregationType: AggregationType
    ): TimeDataPoint {

        // Instant를 LocalDateTime으로 변환
        val parsedTimes = data.x.map { instant ->
            LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        }

        // 시간과 데이터값을 1:1로 연결
        val timeValuePairs = parsedTimes.zip(data.y)

        // 목표 시간 단위에 따라 그룹핑
        val groupedData = when (targetTimeUnit) {
            TimeUnitGroup.HOUR -> timeValuePairs.map { it.first to listOf(it.second) }.toMap()
            TimeUnitGroup.DAY -> groupByDay(timeValuePairs)
            TimeUnitGroup.WEEK -> groupByWeek(timeValuePairs)
            TimeUnitGroup.MONTH -> groupByMonth(timeValuePairs)
            TimeUnitGroup.YEAR -> groupByYear(timeValuePairs)
        }

        val aggregatedData = when (aggregationType) {
            AggregationType.SUM -> {
                // 합계 계산
                groupedData.map { (time, values) ->
                    time to values.sum().toFloat()
                }.sortedBy { it.first }
            }
            AggregationType.AVERAGE -> {
                // 평균 계산 (원본 시간 단위를 기준으로)
                groupedData.map { (time, values) ->
                    val sum = values.sum().toFloat()
                    val divisor = calculateTimeDivisor(time, targetTimeUnit, data.timeUnit)
                    val actualDivisor = countActualTimeUnits(
                        groupedTimeValuePairs = timeValuePairs,
                        targetTime = time,
                        targetTimeUnit = targetTimeUnit,
                        divisorUnit = data.timeUnit
                    )
                    time to (sum / actualDivisor)
                }.sortedBy { it.first }
            }
        }

        // 결과를 Instant와 값 리스트로 변환
        val newXValues = aggregatedData.map { (time, _) ->
            time.atZone(ZoneId.systemDefault()).toInstant()
        }
        val newYValues = aggregatedData.map { it.second }

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
     * 주별 그룹핑 (일요일 기준)
     */
    private fun groupByWeek(timeValuePairs: List<Pair<LocalDateTime, Float>>): Map<LocalDateTime, List<Float>> {
        return timeValuePairs.groupBy { (time, _) ->
            // 일요일을 기준으로 해당 주의 시작 날짜 계산
            val sunday = time.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
            sunday.atStartOfDay()
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
     * 평균 계산을 위한 시간 단위 나누는 값 계산
     * @param time 기준 시간
     * @param targetTimeUnit 목표 시간 단위 (예: MONTH, WEEK)
     * @param divisorUnit 나누는 시간 단위 (예: DAY, HOUR)
     * @return 나누는 값 (예: 해당 월의 일 수, 해당 주의 시간 수)
     */
    private fun calculateTimeDivisor(
        time: LocalDateTime,
        targetTimeUnit: TimeUnitGroup,
        divisorUnit: TimeUnitGroup
    ): Float {
        return when (targetTimeUnit) {
            TimeUnitGroup.YEAR -> {
                when (divisorUnit) {
                    TimeUnitGroup.MONTH -> 12f // 1년 = 12개월
                    TimeUnitGroup.DAY -> {
                        val year = time.year
                        if (isLeapYear(year)) 366f else 365f // 윤년/평년 일수
                    }
                    TimeUnitGroup.HOUR -> {
                        val year = time.year
                        val daysInYear = if (isLeapYear(year)) 366f else 365f
                        daysInYear * 24f // 1년의 총 시간 수
                    }
                    else -> 1f
                }
            }
            TimeUnitGroup.MONTH -> {
                when (divisorUnit) {
                    TimeUnitGroup.DAY -> {
                        // 해당 월의 일 수 계산
                        val yearMonth = java.time.YearMonth.of(time.year, time.month)
                        yearMonth.lengthOfMonth().toFloat()
                    }
                    TimeUnitGroup.HOUR -> {
                        // 해당 월의 총 시간 수 계산
                        val yearMonth = java.time.YearMonth.of(time.year, time.month)
                        yearMonth.lengthOfMonth().toFloat() * 24f
                    }
                    else -> 1f
                }
            }
            TimeUnitGroup.WEEK -> {
                when (divisorUnit) {
                    TimeUnitGroup.DAY -> 7f // 1주 = 7일
                    TimeUnitGroup.HOUR -> 168f // 1주 = 168시간 (7일 * 24시간)
                    else -> 1f
                }
            }
            TimeUnitGroup.DAY -> {
                when (divisorUnit) {
                    TimeUnitGroup.HOUR -> 24f // 1일 = 24시간
                    else -> 1f
                }
            }
            else -> 1f
        }
    }

    /**
     * 윤년 판별
     */
    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    /**
     * 실제 데이터에서 특정 그룹에 속하는 고유 시간 단위의 개수 계산
     * @param groupedTimeValuePairs 전체 시간-값 페어 리스트
     * @param targetTime 대상 그룹의 대표 시간 (예: 2025년 8월 1일 00:00)
     * @param targetTimeUnit 대상 그룹의 시간 단위 (예: MONTH)
     * @param divisorUnit 나누는 시간 단위 (예: DAY)
     * @return 실제 존재하는 고유 시간 단위 개수
     */
    private fun countActualTimeUnits(
        groupedTimeValuePairs: List<Pair<LocalDateTime, Float>>,
        targetTime: LocalDateTime,
        targetTimeUnit: TimeUnitGroup,
        divisorUnit: TimeUnitGroup
    ): Float {
        // 대상 그룹에 속하는 데이터만 필터링
        val dataInTargetGroup = groupedTimeValuePairs.filter { (time, _) ->
            belongsToSameGroup(time, targetTime, targetTimeUnit)
        }

        // divisorUnit에 따라 고유한 시간 단위들을 카운트
        val uniqueTimeUnits = when (divisorUnit) {
            TimeUnitGroup.HOUR -> {
                // 고유한 시간(년-월-일-시) 카운트
                dataInTargetGroup.map { (time, _) ->
                    "${time.year}-${time.monthValue}-${time.dayOfMonth}-${time.hour}"
                }.distinct().size
            }
            TimeUnitGroup.DAY -> {
                // 고유한 날짜(년-월-일) 카운트
                dataInTargetGroup.map { (time, _) ->
                    "${time.year}-${time.monthValue}-${time.dayOfMonth}"
                }.distinct().size
            }
            TimeUnitGroup.WEEK -> {
                // 고유한 주(일요일 기준) 카운트
                dataInTargetGroup.map { (time, _) ->
                    val sunday = time.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                    "${sunday.year}-${sunday.monthValue}-${sunday.dayOfMonth}"
                }.distinct().size
            }
            TimeUnitGroup.MONTH -> {
                // 고유한 월(년-월) 카운트
                dataInTargetGroup.map { (time, _) ->
                    "${time.year}-${time.monthValue}"
                }.distinct().size
            }
            TimeUnitGroup.YEAR -> {
                // 고유한 년도 카운트
                dataInTargetGroup.map { (time, _) ->
                    "${time.year}"
                }.distinct().size
            }
            else -> 1
        }

        return maxOf(uniqueTimeUnits.toFloat(), 1f) // 최소 1로 설정하여 0으로 나누기 방지
    }

    /**
     * 두 시간이 같은 그룹에 속하는지 확인
     * @param time1 비교할 시간 1
     * @param time2 비교할 시간 2
     * @param timeUnit 그룹핑 기준 시간 단위
     * @return 같은 그룹에 속하면 true
     */
    private fun belongsToSameGroup(time1: LocalDateTime, time2: LocalDateTime, timeUnit: TimeUnitGroup): Boolean {
        return when (timeUnit) {
            TimeUnitGroup.HOUR -> {
                time1.year == time2.year && time1.monthValue == time2.monthValue &&
                time1.dayOfMonth == time2.dayOfMonth && time1.hour == time2.hour
            }
            TimeUnitGroup.DAY -> {
                time1.year == time2.year && time1.monthValue == time2.monthValue &&
                time1.dayOfMonth == time2.dayOfMonth
            }
            TimeUnitGroup.WEEK -> {
                val sunday1 = time1.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                val sunday2 = time2.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
                sunday1 == sunday2
            }
            TimeUnitGroup.MONTH -> {
                time1.year == time2.year && time1.monthValue == time2.monthValue
            }
            TimeUnitGroup.YEAR -> {
                time1.year == time2.year
            }
        }
    }
}
