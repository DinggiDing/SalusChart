package com.hdil.saluschart.core.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawContext
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.plusAssign

object ChartDraw {
    /**
     * 데이터 포인트를 화면 좌표로 변환합니다.
     *
     * @param data 차트 데이터 포인트 목록
     * @param size Canvas의 전체 크기
     * @param metrics 차트 메트릭 정보
     * @return 화면 좌표로 변환된 Offset 목록
     */
    fun drawGrid(drawScope: DrawScope, size: Size, metrics: ChartMath.ChartMetrics) {
        val step = (metrics.maxY - metrics.minY) / 5
        for (i in 0..5) {
            val yVal = metrics.minY + i * step
            val y = metrics.chartHeight - ((yVal - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
            drawScope.drawLine(
                color = Color.LightGray,
                start = Offset(metrics.paddingX, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            drawScope.drawContext.canvas.nativeCanvas.drawText(
                "%.0f".format(yVal),
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
     */
    fun drawBars(drawScope: DrawScope, values: List<Float>, metrics: ChartMath.ChartMetrics, color: Color) {
        val barWidth = metrics.chartWidth / values.size / 2
        val spacing = metrics.chartWidth / values.size
        
        values.forEachIndexed { i, value ->
            val barHeight = ((value - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
            val barX = metrics.paddingX + barWidth / 2 + i * spacing  // Y축과 겹치지 않도록 시프트
            val barY = metrics.chartHeight - barHeight
            
            drawScope.drawRect(
                color = color,
                topLeft = Offset(barX, barY),
                size = Size(barWidth, barHeight)
            )
        }
    }

    /**
     * X축과 Y축 라인을 그립니다.
     *
     * @param drawScope 그리기 영역
     * @param metrics 차트 메트릭 정보
     */
    fun drawAxes(drawScope: DrawScope, metrics: ChartMath.ChartMetrics) {
        // Y축 (세로줄)
        drawScope.drawLine(
            color = Color.Black,
            start = Offset(metrics.paddingX, 0f),
            end = Offset(metrics.paddingX, metrics.chartHeight),
            strokeWidth = 2f
        )
        
        // X축 (가로줄)
        drawScope.drawLine(
            color = Color.Black,
            start = Offset(metrics.paddingX, metrics.chartHeight),
            end = Offset(metrics.paddingX + metrics.chartWidth, metrics.chartHeight),
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
     * 파이 차트의 범례를 그립니다.
     *
     * @param drawScope 그리기 영역
     * @param data 차트 데이터 포인트 목록
     * @param colors 각 조각에 사용한 색상 목록
     * @param position 범례가 표시될 위치 좌표
     * @param itemHeight 항목 간 세로 간격
     */
    fun drawPieLegend(
        drawScope: DrawScope,
        data: List<ChartPoint>,
        colors: List<Color>,
        position: Offset,
        itemHeight: Float = 40f
    ) {
        val colorBoxSize = 16f
        val padding = 8f
        var yOffset = position.y

        data.forEachIndexed { i, point ->
            val colorIndex = i % colors.size
            drawLegendItem(
                drawScope,
                colors[colorIndex],
                point.label ?: "항목 ${i+1}",
                Offset(position.x, yOffset),
                colorBoxSize,
                padding
            )
            yOffset += itemHeight
        }
    }

    /**
     * 범례의 개별 항목을 그립니다.
     *
     * @param drawScope 그리기 영역
     * @param color 색상
     * @param label 레이블 텍스트
     * @param position 항목이 표시될 위치
     * @param boxSize 색상 상자 크기
     * @param padding 상자와 텍스트 사이 간격
     */
    fun drawLegendItem(
        drawScope: DrawScope,
        color: Color,
        label: String,
        position: Offset,
        boxSize: Float,
        padding: Float
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
                textSize = 30f
            }
        )
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



    /**
     * 범위 바 차트용 Y축 그리드를 그립니다.
     * X축 라인은 그리지 않고 Y축 그리드와 레이블만 표시합니다.
     *
     * @param drawScope 그리기 영역
     * @param size Canvas의 전체 크기
     * @param metrics 차트 메트릭 정보
     */
    fun drawRangeGrid(drawScope: DrawScope, size: Size, metrics: ChartMath.ChartMetrics) {
        val step = (metrics.maxY - metrics.minY) / 5
        for (i in 0..5) {
            val yVal = metrics.minY + i * step
            val y = metrics.chartHeight - ((yVal - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
            drawScope.drawLine(
                color = Color.LightGray,
                start = Offset(metrics.paddingX, y),
                end = Offset(size.width, y),
                strokeWidth = 1f
            )
            drawScope.drawContext.canvas.nativeCanvas.drawText(
                "%.0f".format(yVal),
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
     * 범위 바 차트용 Y축을 그립니다.
     *
     * @param drawScope 그리기 영역
     * @param metrics 차트 메트릭 정보
     */
    fun drawRangeYAxis(drawScope: DrawScope, metrics: ChartMath.ChartMetrics) {
        // Y축 (세로줄)만 그리기
        drawScope.drawLine(
            color = Color.Black,
            start = Offset(metrics.paddingX, 0f),
            end = Offset(metrics.paddingX, metrics.chartHeight),
            strokeWidth = 2f
        )
    }
}