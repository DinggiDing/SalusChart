package com.hdil.saluschart.core.chart.chartDraw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawContext
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

object CalendarChartDraw {
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
}