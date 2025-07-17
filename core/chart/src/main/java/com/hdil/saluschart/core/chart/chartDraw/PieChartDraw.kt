package com.hdil.saluschart.core.chart.chartDraw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.chartMath.ChartMath

object PieChartDraw {

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
                val labelPos = ChartMath.Pie.calculateLabelPosition(center, radius, 0.7f, midAngle)

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

}