package com.hdil.saluschart.core.chart

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawContext
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas

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
     * X축 레이블을 그립니다.
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
     * 바차트의 막대들을 그립니다.
     *
     * @param drawScope 그리기 영역
     * @param centerPoints 바의 중심점 목록 (mapToCanvasPoints 결과)
     * @param values 원본 데이터 값 목록
     * @param metrics 차트 메트릭 정보
     * @param color 바 색상
     */
    fun drawBars(drawScope: DrawScope, centerPoints: List<Offset>, values: List<Float>, metrics: ChartMath.ChartMetrics, color: Color) {
        val barWidth = metrics.chartWidth / centerPoints.size / 2
        
        centerPoints.forEachIndexed { i, centerPoint ->
            val barHeight = ((values[i] - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
            val barX = centerPoint.x - barWidth / 2  // Center점에서 너비의 절반만큼 왼쪽으로
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
}