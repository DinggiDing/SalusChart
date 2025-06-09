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
     */
    fun drawXAxisLabels(ctx: DrawContext, labels: List<String>, metrics: ChartMath.ChartMetrics) {
        val spacing = metrics.chartWidth / (labels.size - 1)
        labels.forEachIndexed { i, label ->
            val x = metrics.paddingX + i * spacing
            ctx.canvas.nativeCanvas.drawText(
                label,
                x - 20f,
                metrics.chartHeight + 50f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 28f
                }
            )
        }
    }
}