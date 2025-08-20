package com.hdil.saluschart.core.transform

import com.hdil.saluschart.core.util.TimeUnitGroup
import com.hdil.saluschart.core.chart.TimeDataPoint
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.toChartPoints

/**
 * TimeDataPoint에 대한 확장 함수 - 시간 단위 변환
 */
fun TimeDataPoint.transform(
    timeUnit: TimeUnitGroup = TimeUnitGroup.DAY,
    averageDivisorUnit: TimeUnitGroup? = null
): TimeDataPoint {
    return DataTransformer().transform(
        data = this,
        transformTimeUnit = timeUnit,
        averageDivisorUnit = averageDivisorUnit
    )
}

/**
 * TimeDataPoint 리스트에 대한 확장 함수 - 시간 단위 변환
 */
fun List<TimeDataPoint>.transform(
    timeUnit: TimeUnitGroup = TimeUnitGroup.DAY,
    averageDivisorUnit: TimeUnitGroup? = null
): List<ChartPoint> {
    return this.map { dataPoint ->
        DataTransformer().transform(
            data = dataPoint,
            transformTimeUnit = timeUnit,
            averageDivisorUnit = averageDivisorUnit
        )
    }.flatMap { it.toChartPoints() }
}
