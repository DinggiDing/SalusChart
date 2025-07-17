package com.hdil.saluschart.core.chart.chartDraw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import com.hdil.saluschart.core.chart.chartMath.ChartMath

object ScatterChartDraw {
    /**
     * 각 데이터 포인트를 원으로 표시하고 값을 레이블로 표시합니다.
     *
     * @param drawScope 그리기 영역
     * @param points 화면 좌표로 변환된 데이터 포인트 목록
     * @param values 데이터 포인트의 Y축 값 목록
     */
    fun drawPoints(drawScope: DrawScope, points: List<Offset>, values: List<Float>) {
        points.forEachIndexed { i, point ->
            // Draw the point circles
            drawScope.drawCircle(color = Color.Blue, radius = 8f, center = point)
            drawScope.drawCircle(color = Color.White, radius = 4f, center = point)

            // Calculate tangent-based label position
            val labelText = values[i].toInt().toString()
            val labelPosition = ChartMath.Line.calculateLabelPosition(
                pointIndex = i,
                points = points,
                labelText = labelText
            )

            // Draw the label
            drawScope.drawContext.canvas.nativeCanvas.drawText(
                labelText,
                labelPosition.x,
                labelPosition.y,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.DKGRAY
                    textSize = 28f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }
    }
}