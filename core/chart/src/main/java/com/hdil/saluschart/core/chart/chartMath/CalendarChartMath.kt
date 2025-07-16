package com.hdil.saluschart.core.chart.chartMath

import java.time.YearMonth

object CalendarChartMath {
    /**
     * 캘린더에 필요한 정보를 계산합니다.
     *
     * @param yearMonth 표시할 년월
     * @return 달력 구성에 필요한 정보 (첫 번째 요일 위치, 해당 월의 일 수, 필요한 행 수)
     */
    fun computeCalendarMetrics(yearMonth: YearMonth): Triple<Int, Int, Int> {
        val firstDayOfMonth = yearMonth.atDay(1)
        val lastDayOfMonth = yearMonth.atEndOfMonth()
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 일요일이 0이 되도록 조정
        val totalDays = lastDayOfMonth.dayOfMonth

        // 필요한 행의 수 계산 (첫 요일 위치 + 일수에 따라 필요한 행 결정)
        val weeks = (firstDayOfWeek + totalDays + 6) / 7

        return Triple(firstDayOfWeek, totalDays, weeks)
    }

    /**
     * 값에 따른 원의 크기를 계산합니다.
     *
     * @param value 현재 값
     * @param maxValue 가능한 최대값
     * @param minSize 최소 원 크기
     * @param maxSize 최대 원 크기
     * @return 계산된 원의 크기
     */
    fun calculateBubbleSize(value: Float, maxValue: Float, minSize: Float, maxSize: Float): Float {
        if (maxValue <= 0f) return minSize
        val normalizedValue = value / maxValue
        return minSize + (maxSize - minSize) * normalizedValue
    }
}