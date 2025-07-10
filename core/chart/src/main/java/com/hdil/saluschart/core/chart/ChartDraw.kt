package com.hdil.saluschart.core.chart

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawContext
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.plusAssign

object ChartDraw {
    /**
     * 눈금 값을 적절한 형식으로 포맷합니다.
     *
     * @param value 눈금 값
     * @return 포맷된 문자열
     */
    private fun formatTickLabel(value: Float): String {
        return when {
            value == 0f -> "0"
            value >= 1000000 -> "%.1fM".format(value / 1000000)
            value >= 1000 -> "%.1fK".format(value / 1000)
            value % 1 == 0f -> "%.0f".format(value)
            else -> "%.1f".format(value)
        }
    }

    /**
     * Y축 그리드와 레이블을 그립니다.
     *
     * @param drawScope 그리기 영역
     * @param size Canvas의 전체 크기
     * @param metrics 차트 메트릭 정보
     */
    fun drawGrid(drawScope: DrawScope, size: Size, metrics: ChartMath.ChartMetrics) {
        metrics.yTicks.forEach { yVal ->
            val y = metrics.chartHeight - ((yVal - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
            drawScope.drawLine(
                color = Color.LightGray,
                start = Offset(metrics.paddingX, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            
            val labelText = formatTickLabel(yVal)
            drawScope.drawContext.canvas.nativeCanvas.drawText(
                labelText,
                10f,
                y + 10f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 28f
                }
            )
        }
    }

    /**
     * 데이터 포인트를 연결하는 라인을 그립니다.
     *
     * @param drawScope 그리기 영역
     * @param points 화면 좌표로 변환된 데이터 포인트 목록
     * @param color 라인 색상
     */
    fun drawLinePath(drawScope: DrawScope, points: List<Offset>, color: Color) {
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(points.first().x, points.first().y)
            points.drop(1).forEach { lineTo(it.x, it.y) }
        }
        drawScope.drawPath(path, color = color, style = Stroke(width = 4f))
    }

    /**
     * 각 데이터 포인트를 원으로 표시하고 값을 레이블로 표시합니다.
     *
     * @param drawScope 그리기 영역
     * @param points 화면 좌표로 변환된 데이터 포인트 목록
     * @param values 데이터 포인트의 Y축 값 목록
     */
    fun drawPoints(drawScope: DrawScope, points: List<Offset>, values: List<Float>) {
        points.forEachIndexed { i, point ->
            drawScope.drawCircle(color = Color.Blue, radius = 8f, center = point)
            drawScope.drawCircle(color = Color.White, radius = 4f, center = point)
            drawScope.drawContext.canvas.nativeCanvas.drawText(
                values[i].toInt().toString(),
                point.x, point.y - 12f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 28f
                }
            )
        }
    }

    /**
     * X축 레이블을 그립니다 (라인차트용 - 첫 번째 레이블이 왼쪽 끝에서 시작).
     *
     * @param ctx 그리기 컨텍스트
     * @param labels X축에 표시할 레이블 목록
     * @param metrics 차트 메트릭 정보
     * @param centered 텍스트를 중앙 정렬할지 여부 (기본값: true)
     */
    fun drawXAxisLabels(ctx: DrawContext, labels: List<String>, metrics: ChartMath.ChartMetrics, centered: Boolean = true) {
        val spacing = metrics.chartWidth / (labels.size - 1)
        labels.forEachIndexed { i, label ->
            val x = metrics.paddingX + i * spacing
            ctx.canvas.nativeCanvas.drawText(
                label,
                x,
                metrics.chartHeight + 50f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 28f
                    if (centered) {
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                }
            )
        }
    }

    /**
     * 바차트용 X축 레이블을 그립니다 (첫 번째 레이블이 바 너비의 절반만큼 오른쪽에서 시작).
     *
     * @param ctx 그리기 컨텍스트
     * @param labels X축에 표시할 레이블 목록
     * @param metrics 차트 메트릭 정보
     * @param centered 텍스트를 중앙 정렬할지 여부 (기본값: true)
     */
    fun drawBarXAxisLabels(ctx: DrawContext, labels: List<String>, metrics: ChartMath.ChartMetrics, centered: Boolean = true) {
        val barWidth = metrics.chartWidth / labels.size / 2
        val spacing = metrics.chartWidth / labels.size
        labels.forEachIndexed { i, label ->
            val x = metrics.paddingX + barWidth + i * spacing  // 바 너비의 절반만큼 오른쪽으로 시프트
            ctx.canvas.nativeCanvas.drawText(
                label,
                x,
                metrics.chartHeight + 50f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 28f
                    if (centered) {
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                }
            )
        }
    }

    /**
     * 바차트의 막대들을 그립니다.
     *
     * @param drawScope 그리기 영역
     * @param values 원본 데이터 값 목록
     * @param metrics 차트 메트릭 정보
     * @param color 바 색상
     * @return 각 바의 히트 영역과 값의 쌍 목록 (터치 이벤트 처리용)
     */
    fun drawBars(drawScope: DrawScope, values: List<Float>, metrics: ChartMath.ChartMetrics, color: Color): List<Pair<androidx.compose.ui.geometry.Rect, Float>> {
        val barWidth = metrics.chartWidth / values.size / 2
        val spacing = metrics.chartWidth / values.size
        val hitAreas = mutableListOf<Pair<androidx.compose.ui.geometry.Rect, Float>>()

        values.forEachIndexed { i, value ->
            val barHeight = ((value - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
            val barX = metrics.paddingX + barWidth / 2 + i * spacing  // Y축과 겹치지 않도록 시프트
            val barY = metrics.chartHeight - barHeight
            
            drawScope.drawRect(
                color = color,
                topLeft = Offset(barX, barY),
                size = Size(barWidth, barHeight)
            )

            // 히트 영역을 저장 (바 주변에 약간의 여백 추가)
            val hitArea = androidx.compose.ui.geometry.Rect(
                left = barX - 10f,
                top = barY - 10f,
                right = barX + barWidth + 10f,
                bottom = metrics.chartHeight + 10f
            )
            hitAreas += Pair(hitArea, value)
        }

        return hitAreas
    }

    /**
     * 바 차트의 툴팁을 그립니다.
     *
     * @param drawScope 그리기 영역
     * @param value 표시할 값
     * @param position 툴팁이 표시될 위치
     * @param backgroundColor 툴팁 배경 색상
     * @param textColor 텍스트 색상
     */
    fun drawBarTooltip(
        drawScope: DrawScope,
        value: Float,
        position: Offset,
        backgroundColor: Color = Color(0xE6333333), // 반투명 다크 그레이
        textColor: Int = android.graphics.Color.WHITE
    ) {
        val tooltipText = formatTickLabel(value)
        val textPaint = android.graphics.Paint().apply {
            color = textColor
            textSize = 32f
            textAlign = android.graphics.Paint.Align.CENTER
        }

        // 텍스트 크기 측정
        val textBounds = android.graphics.Rect()
        textPaint.getTextBounds(tooltipText, 0, tooltipText.length, textBounds)

        // 툴팁 크기 계산 (패딩 포함)
        val padding = 16f
        val tooltipWidth = textBounds.width() + padding * 2
        val tooltipHeight = textBounds.height() + padding * 2

        // 툴팁이 화면 밖으로 나가지 않도록 위치 조정
        val tooltipX = position.x.coerceIn(tooltipWidth / 2, drawScope.size.width - tooltipWidth / 2)
        val tooltipY = (position.y - tooltipHeight - 10f).coerceAtLeast(10f) // 바 위에 표시

        // 배경 그리기
        drawScope.drawRoundRect(
            color = backgroundColor,
            topLeft = Offset(tooltipX - tooltipWidth / 2, tooltipY),
            size = Size(tooltipWidth, tooltipHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f)
        )

        // 텍스트 그리기
        drawScope.drawContext.canvas.nativeCanvas.drawText(
            tooltipText,
            tooltipX,
            tooltipY + tooltipHeight - padding - textBounds.bottom,
            textPaint
        )
    }

    /**
     * X축 라인을 그립니다.
     *
     * @param drawScope 그리기 영역
     * @param metrics 차트 메트릭 정보
     */
    fun drawXAxis(drawScope: DrawScope, metrics: ChartMath.ChartMetrics) {
        drawScope.drawLine(
            color = Color.Black,
            start = Offset(metrics.paddingX, metrics.chartHeight),
            end = Offset(metrics.paddingX + metrics.chartWidth, metrics.chartHeight),
            strokeWidth = 2f
        )
    }

    /**
     * Y축 라인을 그립니다.
     *
     * @param drawScope 그리기 영역
     * @param metrics 차트 메트릭 정보
     */
    fun drawYAxis(drawScope: DrawScope, metrics: ChartMath.ChartMetrics) {
        drawScope.drawLine(
            color = Color.Black,
            start = Offset(metrics.paddingX, 0f),
            end = Offset(metrics.paddingX, metrics.chartHeight),
            strokeWidth = 2f
        )
    }

    /**
     * 파이 차트의 개별 섹션을 그립니다.
     *
     * @param drawScope 그리기 영역
     * @param center 원의 중심점
     * @param radius 원의 반지름
     * @param startAngle 시작 각도
     * @param sweepAngle 호의 각도
     * @param color 섹션 색상
     * @param isDonut 도넛 형태로 그릴지 여부
     * @param strokeWidth 도넛일 경우 테두리 두께
     */
     fun drawPieSection(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        startAngle: Float,
        sweepAngle: Float,
        color: Color,
        isDonut: Boolean,
        strokeWidth: Float
    ) {
        if (isDonut) {
            // 도넛 형태로 그리기
            drawScope.drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2),
                style = Stroke(width = strokeWidth)
            )
        } else {
            // 일반 파이 차트로 그리기
            drawScope.drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2, radius * 2)
            )
        }
    }

    /**
     * 파이 차트의 라벨을 그립니다.
     *
     * @param drawScope 그리기 영역
     * @param center 원의 중심점
     * @param radius 원의 반지름
     * @param data 차트 데이터 포인트 목록
     * @param sections 계산된 섹션 정보 목록
     */
     fun drawPieLabels(
        drawScope: DrawScope,
        center: Offset,
        radius: Float,
        data: List<ChartPoint>,
        sections: List<Triple<Float, Float, Float>>
    ) {
        sections.forEachIndexed { i, (startAngle, sweepAngle, _) ->
            val point = data[i]
            // 레이블이 있는 경우, 파이 차트 조각 가운데에 레이블 표시
            if (point.label != null) {
                // 현재 조각의 중앙 각도
                val midAngle = startAngle + sweepAngle / 2

                // 레이블 위치 계산
                val labelPos = ChartMath.calculateLabelPosition(center, radius, 0.7f, midAngle)

                // 레이블 그리기
                drawScope.drawContext.canvas.nativeCanvas.drawText(
                    point.label,
                    labelPos.x,
                    labelPos.y,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        textSize = 30f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        }
    }

    /**
     * 범례를 그립니다 (스케일링 지원).
     *
     * @param drawScope 그리기 영역
     * @param labels 범례 항목 레이블 목록
     * @param colors 색상 목록
     * @param position 범례가 표시될 위치 좌표
     * @param chartSize 차트 전체 크기 (스케일링 계산용)
     * @param title 범례 제목 (null인 경우 제목 없음)
     * @param baseItemHeight 기본 항목 간 세로 간격 (스케일링 적용됨)
     */
    fun drawLegend(
        drawScope: DrawScope,
        labels: List<String>,
        colors: List<Color>,
        position: Offset,
        chartSize: androidx.compose.ui.geometry.Size,
        title: String? = null,
        baseItemHeight: Float = 20f 
    ) {
        // 차트 크기에 따른 스케일 팩터 계산 (기준: 250x250)
        val scaleFactor = minOf(chartSize.width, chartSize.height) / 250f
        val clampedScale = scaleFactor.coerceIn(0.5f, 2.0f) 
        
        val colorBoxSize = (8f * clampedScale).coerceAtLeast(4f) 
        val padding = (4f * clampedScale).coerceAtLeast(2f) 
        val itemHeight = baseItemHeight * clampedScale
        val titleTextSize = (14f * clampedScale).coerceAtLeast(10f) 
        val labelTextSize = (12f * clampedScale).coerceAtLeast(8f)

        Log.e("ChartDraw", "Legend scale factor: $clampedScale, itemHeight: $itemHeight, colorBoxSize: $colorBoxSize, labelTextSize: $labelTextSize")
        
        var yOffset = position.y

        // 범례 제목 그리기 (제공된 경우)
        title?.let {
            drawScope.drawContext.canvas.nativeCanvas.drawText(
                it,
                position.x,
                yOffset,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = titleTextSize
                    isFakeBoldText = true
                }
            )
            yOffset += itemHeight * 0.8f
        }

        // 각 범례 항목 그리기
        labels.forEachIndexed { index, label ->
            if (index < colors.size) {
                drawLegendItem(
                    drawScope,
                    colors[index],
                    label,
                    Offset(position.x, yOffset),
                    colorBoxSize,
                    padding,
                    labelTextSize
                )
                yOffset += itemHeight * 0.7f
            }
        }
    }

    /**
     * 차트의 범례를 그립니다 (통합된 범례 시스템, 스케일링 지원).
     * 
     * 파이 차트와 스택 바 차트 모두에서 사용할 수 있는 통합된 범례 시스템입니다.
     * 레이블을 직접 제공하거나 차트 데이터에서 추출할 수 있습니다.
     *
     * @param drawScope 그리기 영역
     * @param labels 범례 항목 레이블 목록 (직접 제공된 경우)
     * @param chartData 차트 데이터 포인트 목록 (레이블을 추출할 경우)
     * @param colors 각 항목에 사용한 색상 목록
     * @param position 범례가 표시될 위치 좌표
     * @param chartSize 차트 전체 크기 (스케일링 계산용)
     * @param title 범례 제목 (기본값: null)
     * @param itemHeight 항목 간 세로 간격
     */
    fun drawChartLegend(
        drawScope: DrawScope,
        labels: List<String>? = null,
        chartData: List<ChartPoint>? = null,
        colors: List<Color>,
        position: Offset,
        chartSize: androidx.compose.ui.geometry.Size,
        title: String? = null,
        itemHeight: Float = 40f
    ) {
        val legendLabels = labels ?: chartData?.mapIndexed { i, point ->
            point.label ?: "항목 ${i+1}"
        } ?: emptyList()
        
        drawLegend(drawScope, legendLabels, colors, position, chartSize, title, itemHeight)
    }

    /**
     * 범례의 개별 항목을 그립니다 (스케일링 지원).
     *
     * @param drawScope 그리기 영역
     * @param color 색상
     * @param label 레이블 텍스트
     * @param position 항목이 표시될 위치
     * @param boxSize 색상 상자 크기 (이미 스케일링 적용됨)
     * @param padding 상자와 텍스트 사이 간격 (이미 스케일링 적용됨)
     * @param textSize 텍스트 크기 (이미 스케일링 적용됨)
     */
    fun drawLegendItem(
        drawScope: DrawScope,
        color: Color,
        label: String,
        position: Offset,
        boxSize: Float,
        padding: Float,
        textSize: Float = 30f
    ) {
        // 색상 상자 그리기
        drawScope.drawRect(
            color = color,
            topLeft = position,
            size = Size(boxSize, boxSize)
        )

        // 레이블 그리기
        drawScope.drawContext.canvas.nativeCanvas.drawText(
            label,
            position.x + boxSize + padding,
            position.y + boxSize,
            android.graphics.Paint().apply {
                this.color = android.graphics.Color.DKGRAY
                this.textSize = textSize
            }
        )
    }

    /**
     * 스택 바 차트의 막대들을 그립니다.
     * 각 막대는 여러 세그먼트가 수직으로 쌓인 형태입니다.
     *
     * @param drawScope 그리기 영역
     * @param data 스택 차트 데이터 포인트 목록
     * @param metrics 차트 메트릭 정보
     * @param colors 각 세그먼트의 기본 색상 팔레트
     * @param barWidthRatio 바 너비 비율 (0.0 ~ 1.0, 기본값 0.6)
     */
    fun drawStackedBars(
        drawScope: DrawScope,
        data: List<StackedChartPoint>,
        metrics: ChartMath.ChartMetrics,
        colors: List<Color>,
        barWidthRatio: Float = 0.6f
    ) {
        val barWidth = (metrics.chartWidth / data.size) * barWidthRatio
        val spacing = metrics.chartWidth / data.size

        data.forEachIndexed { i, stackedPoint ->
            val barX = metrics.paddingX + (spacing - barWidth) / 2 + i * spacing
            var currentY = metrics.chartHeight

            // 각 세그먼트를 아래에서 위로 쌓아 올림
            stackedPoint.values.forEachIndexed { segmentIndex, value ->
                if (value > 0) { // 0보다 큰 값만 그리기
                    val segmentHeight = (value / (metrics.maxY - metrics.minY)) * metrics.chartHeight
                    val segmentY = currentY - segmentHeight
                    
                    // 색상 결정: 개별 색상이 있으면 사용, 없으면 기본 팔레트 사용
                    val segmentColor = stackedPoint.segmentColors?.getOrNull(segmentIndex)?.let { 
                        Color(it) 
                    } ?: colors.getOrElse(segmentIndex % colors.size) { colors.first() }
                    
                    // 세그먼트 그리기
                    drawScope.drawRect(
                        color = segmentColor,
                        topLeft = Offset(barX, segmentY),
                        size = Size(barWidth, segmentHeight)
                    )
                    
                    currentY = segmentY // 다음 세그먼트를 위해 Y 위치 업데이트
                }
            }
        }
    }

    /**
     * 캘린더 차트의 월 헤더를 그립니다.
     *
     * @param ctx 그리기 컨텍스트
     * @param monthName 월 이름
     * @param year 년도
     * @param width 너비
     * @param textColor 텍스트 색상
     */
    fun drawCalendarHeader(
        ctx: DrawContext,
        monthName: String,
        year: Int,
        width: Float,
        textColor: Int = android.graphics.Color.BLACK
    ) {
        val headerText = "$monthName $year"
        ctx.canvas.nativeCanvas.drawText(
            headerText,
            width / 2,
            50f,
            android.graphics.Paint().apply {
                color = textColor
                textSize = 40f
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
            }
        )
    }

    /**
     * 캘린더 차트의 요일 헤더를 그립니다.
     *
     * @param ctx 그리기 컨텍스트
     * @param dayOfWeeks 요일 목록
     * @param width 너비
     * @param cellWidth 각 셀 너비
     * @param y Y 좌표 위치
     * @param weekendColor 주말 텍스트 색상
     * @param weekdayColor 평일 텍스트 색상
     * @param locale 로케일
     */
    fun drawCalendarDayHeaders(
        ctx: DrawContext,
        dayOfWeeks: Array<DayOfWeek>,
        width: Float,
        cellWidth: Float,
        y: Float = 80f,
        weekendColor: Int = android.graphics.Color.RED,
        weekdayColor: Int = android.graphics.Color.BLACK,
        locale: Locale = Locale.getDefault()
    ) {
        dayOfWeeks.forEachIndexed { index, dayOfWeek ->
            val x = cellWidth * (index + 0.5f)
            val isWeekend = dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY

            ctx.canvas.nativeCanvas.drawText(
                dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
                x,
                y,
                android.graphics.Paint().apply {
                    color = if (isWeekend) weekendColor else weekdayColor
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                }
            )
        }
    }

    /**
     * 캘린더 차트의 날짜를 그립니다.
     *
     * @param ctx 그리기 컨텍스트
     * @param day 날짜
     * @param x X 좌표
     * @param y Y 좌표
     * @param isWeekend 주말 여부
     * @param textColor 텍스트 색상
     * @param weekendColor 주말 텍스트 색상
     */
    fun drawCalendarDay(
        ctx: DrawContext,
        day: Int,
        x: Float,
        y: Float,
        isWeekend: Boolean = false,
        textColor: Int = android.graphics.Color.BLACK,
        weekendColor: Int = android.graphics.Color.RED
    ) {
        ctx.canvas.nativeCanvas.drawText(
            day.toString(),
            x,
            y,
            android.graphics.Paint().apply {
                color = if (isWeekend) weekendColor else textColor
                textSize = 28f
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }

    /**
     * 캘린더 차트의 데이터 포인트를 원으로 표시합니다.
     *
     * @param drawScope 그리기 영역
     * @param x X 좌표
     * @param y Y 좌표
     * @param radius 원 반지름
     * @param color 원 색상
     */
    fun drawCalendarDataPoint(
        drawScope: DrawScope,
        x: Float,
        y: Float,
        radius: Float,
        color: Color
    ) {
        drawScope.drawCircle(
            color = color,
            radius = radius,
            center = Offset(x, y)
        )
    }

    /**
     * 캘린더 그리드를 그립니다.
     *
     * @param drawScope 그리기 영역
     * @param cellWidth 셀 너비
     * @param cellHeight 셀 높이
     * @param rows 행 수
     * @param columns 열 수
     * @param startX 시작 X 좌표
     * @param startY 시작 Y 좌표
     * @param gridColor 그리드 선 색상
     */
    fun drawCalendarGrid(
        drawScope: DrawScope,
        cellWidth: Float,
        cellHeight: Float,
        rows: Int,
        columns: Int = 7,
        startX: Float = 0f,
        startY: Float = 100f,
        gridColor: Color = Color.LightGray
    ) {
        // 수평선 그리기
        for (row in 0..rows) {
            val y = startY + row * cellHeight
            drawScope.drawLine(
                color = gridColor,
                start = Offset(startX, y),
                end = Offset(startX + columns * cellWidth, y),
                strokeWidth = 1f
            )
        }

        // 수직선 그리기
        for (col in 0..columns) {
            val x = startX + col * cellWidth
            drawScope.drawLine(
                color = gridColor,
                start = Offset(x, startY),
                end = Offset(x, startY + rows * cellHeight),
                strokeWidth = 1f
            )
        }
    }

    /**
     * 범위 바 차트의 막대들을 그립니다.
     * 각 막대는 yMin에서 yMax까지의 범위를 표시합니다.
     *
     * @param drawScope 그리기 영역
     * @param data 범위 차트 데이터 포인트 목록
     * @param metrics 차트 메트릭 정보
     * @param color 바 색상
     * @param barWidthRatio 바 너비 비율 (0.0 ~ 1.0, 기본값 0.6)
     */
    fun drawRangeBars(
        drawScope: DrawScope, 
        data: List<RangeChartPoint>, 
        metrics: ChartMath.ChartMetrics, 
        color: Color,
        barWidthRatio: Float = 0.6f
    ) {
        val barWidth = (metrics.chartWidth / data.size) * barWidthRatio
        val spacing = metrics.chartWidth / data.size
        
        data.forEachIndexed { i, rangePoint ->
            val yMinScreen = metrics.chartHeight - ((rangePoint.yMin - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
            val yMaxScreen = metrics.chartHeight - ((rangePoint.yMax - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
            
            val barHeight = yMinScreen - yMaxScreen // 범위의 높이
            val barX = metrics.paddingX + (spacing - barWidth) / 2 + i * spacing

            drawScope.drawRect(
                color = color,
                topLeft = Offset(barX, yMaxScreen),
                size = Size(barWidth, barHeight)
            )
        }
    }

}

