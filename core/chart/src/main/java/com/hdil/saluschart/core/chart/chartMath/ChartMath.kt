package com.hdil.saluschart.core.chart.chartMath

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.RangeChartPoint
import java.time.YearMonth
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

object ChartMath {

    var Pie = PieChartMath
    var Calendar = CalendarChartMath
    val RangeBar = RangeBarChartMath
    val Line = LineChartMath

    /**
     * 차트 그리기에 필요한 메트릭 정보를 담는 데이터 클래스
     *
     * @param paddingX X축 패딩 값
     * @param paddingY Y축 패딩 값
     * @param chartWidth 차트의 실제 너비
     * @param chartHeight 차트의 실제 높이
     * @param minY Y축의 최소값
     * @param maxY Y축의 최대값
     * @param yTicks Y축에 표시할 눈금 값들
     */
    data class ChartMetrics(
        val paddingX: Float,
        val paddingY: Float,
        val chartWidth: Float,
        val chartHeight: Float,
        val minY: Float,
        val maxY: Float,
        val yTicks: List<Float>
    )

    /**
     * y-axis 눈금 값들을 계산합니다.
     * 1, 2, 5의 배수를 사용하여 시각적으로 깔끔한 눈금을 생성합니다.
     *
     * @param min 데이터의 최소값
     * @param max 데이터의 최대값
     * @param tickCount 원하는 눈금 개수 (기본값: 5)
     * @return 계산된 눈금 값들의 리스트
     */
    fun computeNiceTicks(min: Float, max: Float, tickCount: Int = 5): List<Float> {
        if (min >= max) {
            return listOf(0f, 1f)
        }
        val rawStep = (max - min) / tickCount.toDouble()
        val power = 10.0.pow(floor(log10(rawStep)))
        val candidates = listOf(1.0, 2.0, 5.0).map { it * power }
        val step = candidates.minByOrNull { abs(it - rawStep) } ?: power

        val niceMin = floor(min / step) * step
        val niceMax = ceil(max / step) * step

        val ticks = mutableListOf<Float>()
        var t = niceMin
        while (t <= niceMax + 1e-6) {
            // Fix floating-point precision issues
            val roundedTick = round(t * 1000000) / 1000000
            ticks.add(roundedTick.toFloat())
            t += step
        }

        return ticks
    }

    /**
     * 차트 그리기에 필요한 메트릭 값을 계산합니다.
     *
     * @param size Canvas의 전체 크기
     * @param values 차트에 표시할 Y축 데이터 값 목록
     * @param tickCount 원하는 Y축 눈금 개수 (기본값: 5)
     * @param chartType 차트 타입 (BAR/STACKED_BAR 타입일 경우 minY를 항상 0으로 설정)
     * @return 차트 메트릭 객체
     */
    fun computeMetrics(size: Size, values: List<Float>, tickCount: Int = 5, chartType: ChartType? = null): ChartMetrics { // TODO: tickCount = 5 고정
        val paddingX = 60f
        val paddingY = 40f
        val chartWidth = size.width - paddingX
        val chartHeight = size.height - paddingY

        val dataMax = values.maxOrNull() ?: 1f
        val dataMin = values.minOrNull() ?: 0f

        // BAR 및 STACKED_BAR 차트의 경우 항상 minY를 0으로 설정
        val minY = if (chartType == ChartType.BAR || chartType == ChartType.STACKED_BAR) {
            0f
        } else {
            if (dataMin >= 0 && dataMin < dataMax * 0.1) 0f else dataMin
        }
        val maxY = dataMax

        val yTicks = computeNiceTicks(minY, maxY, tickCount)

        val actualMinY = yTicks.minOrNull() ?: minY
        val actualMaxY = yTicks.maxOrNull() ?: maxY

        return ChartMetrics(paddingX, paddingY, chartWidth, chartHeight, actualMinY, actualMaxY, yTicks)
    }

    /**
     * 데이터 포인트를 화면 좌표로 변환합니다.
     *
     * @param data 차트 데이터 포인트 목록
     * @param size Canvas의 전체 크기
     * @param metrics 차트 메트릭 정보
     * @return 화면 좌표로 변환된 Offset 목록
     */
    fun mapToCanvasPoints(data: List<ChartPoint>, size: Size, metrics: ChartMetrics): List<Offset> {
        val spacing = metrics.chartWidth / (data.size - 1)
        return data.mapIndexed { i, point ->
            val x = metrics.paddingX + i * spacing
            val y = metrics.chartHeight - ((point.y - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
            Offset(x, y)
        }
    }
}