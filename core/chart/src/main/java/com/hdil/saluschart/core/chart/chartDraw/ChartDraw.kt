package com.hdil.saluschart.core.chart.chartDraw

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawContext
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.RangeChartPoint
import com.hdil.saluschart.core.chart.StackedChartPoint
import com.hdil.saluschart.core.chart.chartMath.CalendarChartMath
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.core.chart.chartMath.LineChartMath
import com.hdil.saluschart.core.chart.chartMath.PieChartMath
import com.hdil.saluschart.core.chart.chartMath.RangeBarChartMath
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.plusAssign

object ChartDraw {

    var Pie = PieChartDraw
    var Calendar = CalendarChartDraw
    val RangeBar = RangeBarChartDraw
    val Line = LineChartDraw
    val Bar = BarChartDraw
    val Scatter = ScatterChartDraw
    val Progress = ProgressChartDraw
    val Min = MinimalChartDraw

    /**
     * 눈금 값을 적절한 형식으로 포맷합니다.
     *
     * @param value 눈금 값
     * @return 포맷된 문자열
     */
    fun formatTickLabel(value: Float): String {
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
}

