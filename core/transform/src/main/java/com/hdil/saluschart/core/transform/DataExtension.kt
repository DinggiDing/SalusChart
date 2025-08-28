package com.hdil.saluschart.core.transform

import com.hdil.saluschart.core.util.TimeUnitGroup
import com.hdil.saluschart.core.util.AggregationType
import com.hdil.saluschart.core.chart.ChartPoint

/**
 * TimeDataPoint에 대한 확장 함수 - 시간 단위 변환
 */
fun TimeDataPoint.transform(
    timeUnit: TimeUnitGroup = TimeUnitGroup.DAY,
    aggregationType: AggregationType = AggregationType.SUM
): TimeDataPoint {
    return DataTransformer().transform(
        data = this,
        transformTimeUnit = timeUnit,
        aggregationType = aggregationType
    )
}

/**
 * TimeDataPoint 리스트에 대한 확장 함수 - 시간 단위 변환
 */
fun List<TimeDataPoint>.transform(
    timeUnit: TimeUnitGroup = TimeUnitGroup.DAY,
    aggregationType: AggregationType = AggregationType.SUM
): List<ChartPoint> {
    return this.map { dataPoint ->
        DataTransformer().transform(
            data = dataPoint,
            transformTimeUnit = timeUnit,
            aggregationType = aggregationType
        )
    }.flatMap { it.toChartPoints() }
}