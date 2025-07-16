package com.hdil.saluschart.core.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.IntSize
import java.time.YearMonth
import java.time.LocalDate
import kotlin.math.*

object ChartMath {
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

    /**
     * 파이 차트의 중심점과 반지름을 계산합니다.
     *
     * @param size 캔버스 크기
     * @param padding 원 테두리 패딩 값
     * @return Pair(중심 좌표, 반지름)
     */
    fun computePieMetrics(size: Size, padding: Float = 32f): Pair<Offset, Float> {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = minOf(size.width, size.height) / 2 - padding
        return Pair(center, radius)
    }

    /**
     * 파이 차트의 각 섹션의 각도를 계산합니다.
     *
     * @param data 차트 데이터 포인트 목록
     * @return List<Triple<시작 각도, 스윕 각도, 값 비율>>
     */
    fun computePieAngles(data: List<ChartPoint>): List<Triple<Float, Float, Float>> {
        val totalValue = data.sumOf { it.y.toDouble() }.toFloat()
        if (totalValue <= 0f) return emptyList()

        var startAngle = -90f // 12시 방향에서 시작

        return data.map { point ->
            val ratio = point.y / totalValue
            val sweepAngle = ratio * 360f
            val result = Triple(startAngle, sweepAngle, ratio)
            startAngle += sweepAngle
            result
        }
    }

    /**
     * 파이 섹션의 레이블 위치를 계산합니다.
     *
     * @param center 원의 중심점
     * @param radius 원의 기본 반지름
     * @param radiusFactor 레이블 위치 조정을 위한 반지름 인수 (1보다 작으면 안쪽, 크면 바깥쪽)
     * @param angle 각도(라디안)
     * @return 레이블이 표시될 위치 좌표
     */
    fun calculateLabelPosition(center: Offset, radius: Float, radiusFactor: Float, angleInDegrees: Float): Offset {
        val angleInRadians = Math.toRadians(angleInDegrees.toDouble())
        val labelRadius = radius * radiusFactor
        val x = center.x + labelRadius * Math.cos(angleInRadians).toFloat()
        val y = center.y + labelRadius * Math.sin(angleInRadians).toFloat()
        return Offset(x, y)
    }

    /**
     * 캘린더 차트에서 사용될 데이터 클래스
     *
     * @param date 날짜
     * @param value 데이터 값
     * @param color 색상 코드 (null인 경우 기본 색상 사용)
     */
    data class CalendarData(val date: LocalDate, val value: Float, val color: Int? = null)

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

    /**
     * 범위 차트 그리기에 필요한 메트릭 값을 계산합니다.
     *
     * @param size Canvas의 전체 크기
     * @param data 범위 차트 데이터 포인트 목록
     * @param tickCount 원하는 Y축 눈금 개수 (기본값: 5)
     * @return 차트 메트릭 객체
     */
    fun computeRangeMetrics(size: Size, data: List<RangeChartPoint>, tickCount: Int = 5): ChartMetrics {
        val paddingX = 60f
        val paddingY = 40f
        val chartWidth = size.width - paddingX
        val chartHeight = size.height - paddingY
        
        val allYValues = data.flatMap { listOf(it.yMin, it.yMax) }
        val dataMax = allYValues.maxOrNull() ?: 1f
        val dataMin = allYValues.minOrNull() ?: 0f
        
        val yTicks = computeNiceTicks(dataMin, dataMax, tickCount)
        
        val actualMinY = yTicks.minOrNull() ?: dataMin
        val actualMaxY = yTicks.maxOrNull() ?: dataMax
        
        return ChartMetrics(paddingX, paddingY, chartWidth, chartHeight, actualMinY, actualMaxY, yTicks)
    }

    /**
     * 라벨 배치를 위한 8방향 후보 위치를 계산합니다.
     * 
     * @param centerX 데이터 포인트의 X 좌표
     * @param centerY 데이터 포인트의 Y 좌표  
     * @param labelWidth 라벨의 너비
     * @param labelHeight 라벨의 높이
     * @param padding 데이터 포인트로부터의 최소 거리
     * @return 8개의 후보 위치 목록 (N, NE, E, SE, S, SW, W, NW 순서)
     */
    fun calculateLabelCandidates(
        centerX: Float,
        centerY: Float,
        labelWidth: Float,
        labelHeight: Float,
        padding: Float = 15f
    ): List<Offset> {
        val w = labelWidth
        val h = labelHeight
        val pad = padding
        
        return listOf(
            Offset(centerX - w/2, centerY - h - pad),           // N (North)
            Offset(centerX + pad, centerY - h - pad),           // NE (Northeast)  
            Offset(centerX + pad, centerY - h/2),               // E (East)
            Offset(centerX + pad, centerY + pad),               // SE (Southeast)
            Offset(centerX - w/2, centerY + pad),               // S (South)
            Offset(centerX - w - pad, centerY + pad),           // SW (Southwest)
            Offset(centerX - w - pad, centerY - h/2),           // W (West)
            Offset(centerX - w - pad, centerY - h - pad)        // NW (Northwest)
        )
    }

    /**
     * 라벨 사각형이 선분과 교차하는지 확인합니다.
     * 
     * @param labelRect 라벨의 경계 사각형
     * @param lineStart 선분의 시작점
     * @param lineEnd 선분의 끝점
     * @return 교차하면 true, 아니면 false
     */
    fun doesLabelIntersectLine(
        labelRect: androidx.compose.ui.geometry.Rect,
        lineStart: Offset,
        lineEnd: Offset
    ): Boolean {
        // 선분이 사각형과 교차하는지 확인
        // 1. 선분의 양 끝점이 사각형 내부에 있는지 확인
        if (labelRect.contains(lineStart) || labelRect.contains(lineEnd)) {
            return true
        }
        
        // 2. 선분이 사각형의 각 변과 교차하는지 확인
        val rectEdges = listOf(
            Pair(labelRect.topLeft, labelRect.topRight),           // Top edge
            Pair(labelRect.topRight, labelRect.bottomRight),       // Right edge  
            Pair(labelRect.bottomRight, labelRect.bottomLeft),     // Bottom edge
            Pair(labelRect.bottomLeft, labelRect.topLeft)          // Left edge
        )
        
        return rectEdges.any { (edgeStart, edgeEnd) ->
            doLinesIntersect(lineStart, lineEnd, edgeStart, edgeEnd)
        }
    }

    /**
     * 두 선분이 교차하는지 확인합니다.
     * 
     * @param line1Start 첫 번째 선분의 시작점
     * @param line1End 첫 번째 선분의 끝점
     * @param line2Start 두 번째 선분의 시작점
     * @param line2End 두 번째 선분의 끝점
     * @return 교차하면 true, 아니면 false
     */
    private fun doLinesIntersect(
        line1Start: Offset,
        line1End: Offset,
        line2Start: Offset,
        line2End: Offset
    ): Boolean {
        val d1 = direction(line2Start, line2End, line1Start)
        val d2 = direction(line2Start, line2End, line1End)
        val d3 = direction(line1Start, line1End, line2Start)
        val d4 = direction(line1Start, line1End, line2End)
        
        return ((d1 > 0 && d2 < 0) || (d1 < 0 && d2 > 0)) && 
               ((d3 > 0 && d4 < 0) || (d3 < 0 && d4 > 0))
    }

    /**
     * 세 점의 방향을 계산합니다. (외적 계산)
     */
    private fun direction(a: Offset, b: Offset, c: Offset): Float {
        return (c.x - a.x) * (b.y - a.y) - (b.x - a.x) * (c.y - a.y)
    }

    /**
     * 스마트 라벨 위치를 계산합니다. 선분들과의 교차를 피합니다.
     * 
     * @param centerX 데이터 포인트의 X 좌표
     * @param centerY 데이터 포인트의 Y 좌표
     * @param labelWidth 라벨의 너비  
     * @param labelHeight 라벨의 높이
     * @param nearbyLines 주변 선분들의 목록
     * @param padding 데이터 포인트로부터의 최소 거리
     * @return 최적의 라벨 위치
     */
    fun calculateSmartLabelPosition(
        centerX: Float,
        centerY: Float,
        labelWidth: Float,
        labelHeight: Float,
        nearbyLines: List<Pair<Offset, Offset>> = emptyList(),
        padding: Float = 15f
    ): Offset {
        val candidates = calculateLabelCandidates(centerX, centerY, labelWidth, labelHeight, padding)
        
        // 교차하지 않는 첫 번째 후보 찾기
        for (candidate in candidates) {
            val labelRect = androidx.compose.ui.geometry.Rect(
                candidate.x, 
                candidate.y, 
                candidate.x + labelWidth, 
                candidate.y + labelHeight
            )
            
            val hasIntersection = nearbyLines.any { (lineStart, lineEnd) ->
                doesLabelIntersectLine(labelRect, lineStart, lineEnd)
            }
            
            if (!hasIntersection) {
                return candidate
            }
        }
        
        // 모든 후보가 교차하면 첫 번째 후보 반환 (North 위치)
        return candidates.first()
    }

    /**
     * 주어진 인덱스 주변의 선분들을 가져옵니다.
     * 
     * @param points 모든 데이터 포인트들
     * @param currentIndex 현재 포인트의 인덱스
     * @param radius 확인할 주변 포인트의 범위
     * @return 주변 선분들의 목록
     */
    fun getNearbyLineSegments(
        points: List<Offset>,
        currentIndex: Int,
        radius: Int = 1
    ): List<Pair<Offset, Offset>> {
        val segments = mutableListOf<Pair<Offset, Offset>>()
        
        for (i in maxOf(0, currentIndex - radius) until minOf(points.size - 1, currentIndex + radius + 1)) {
            if (i != currentIndex && i + 1 < points.size) {
                segments.add(Pair(points[i], points[i + 1]))
            }
        }
        
        return segments
    }

    /**
     * 탄젠트 벡터를 기반으로 최적의 라벨 위치를 계산합니다.
     * 라인과의 겹침을 최소화하기 위해 접선에 수직인 방향으로 라벨을 배치합니다.
     * 
     * @param pointIndex 현재 포인트의 인덱스
     * @param points 모든 데이터 포인트들
     * @param labelText 표시할 라벨 텍스트
     * @return 최적의 라벨 위치
     */
    fun calculateLabelPosition(
        pointIndex: Int,
        points: List<Offset>,
        labelText: String
    ): Offset {
        val currentPoint = points[pointIndex]
        val baseDistance = 25f
        
        // Step 1: Calculate tangent vector
        val tangent = when {
            points.size < 2 -> Offset(1f, 0f) // Default horizontal for single point
            pointIndex == 0 -> {
                // Start point: use direction to next point
                val direction = points[1] - currentPoint
                normalizeVector(direction)
            }
            pointIndex == points.size - 1 -> {
                // End point: use direction from previous point  
                val direction = currentPoint - points[pointIndex - 1]
                normalizeVector(direction)
            }
            else -> {
                // Interior point: average of incoming and outgoing directions
                val incoming = normalizeVector(currentPoint - points[pointIndex - 1])
                val outgoing = normalizeVector(points[pointIndex + 1] - currentPoint)
                normalizeVector(Offset(
                    (incoming.x + outgoing.x) / 2f,
                    (incoming.y + outgoing.y) / 2f
                ))
            }
        }
        
        // Step 2: Calculate normal vectors (perpendicular to tangent)
        val normal1 = Offset(-tangent.y, tangent.x)   // 90° counterclockwise
        val normal2 = Offset(tangent.y, -tangent.x)   // 90° clockwise
        
        // Step 3: Generate candidate positions
        val candidate1 = Offset(
            currentPoint.x + normal1.x * baseDistance,
            currentPoint.y + normal1.y * baseDistance
        )
        val candidate2 = Offset(
            currentPoint.x + normal2.x * baseDistance,
            currentPoint.y + normal2.y * baseDistance
        )
        
        // Step 4: Choose the better candidate (prefer upward direction)
        return if (candidate1.y < candidate2.y) candidate1 else candidate2
    }
    
    /**
     * 벡터를 정규화합니다.
     * 
     * @param vector 정규화할 벡터
     * @return 정규화된 벡터 (길이가 1인 단위 벡터)
     */
    fun normalizeVector(vector: Offset): Offset {
        val magnitude = sqrt(vector.x * vector.x + vector.y * vector.y)
        return if (magnitude > 0f) {
            Offset(vector.x / magnitude, vector.y / magnitude)
        } else {
            Offset(1f, 0f)
        }
    }
}