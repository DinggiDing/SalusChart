package com.hdil.saluschart.core.chart.chartDraw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawContext
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import com.hdil.saluschart.core.chart.StackedChartPoint
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw.formatTickLabel
import com.hdil.saluschart.core.chart.chartMath.ChartMath

object BarChartDraw {
    /**
     * 바차트용 X축 레이블을 그립니다 (첫 번째 레이블이 바 너비의 절반만큼 오른쪽에서 시작).
     *
     * @param ctx 그리기 컨텍스트
     * @param labels X축에 표시할 레이블 목록
     * @param metrics 차트 메트릭 정보
     * @param centered 텍스트를 중앙 정렬할지 여부 (기본값: true)
     * @param textSize 레이블 텍스트 크기 (기본값: 28f)
     */
    fun drawBarXAxisLabels(
        ctx: DrawContext, 
        labels: List<String>, 
        metrics: ChartMath.ChartMetrics, 
        centered: Boolean = true,
        textSize: Float = 28f
    ) {
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
                    this.textSize = textSize
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
     * @param isMinimal 미니멀 차트 모드인지 여부 (기본값: false)
     * @param barWidthMultiplier 바 너비 배수 (기본값: normal=0.5, minimal=0.8)
     * @param isInteractiveBars 터치 상호작용용 바를 그릴지 여부 (true: 전체 크기 투명 바, false: 데이터 시각화 바)
     * @return 각 바의 히트 영역과 값의 쌍 목록 (isInteractiveBars=true일 때만 반환, 아니면 빈 리스트)
     */
    fun drawBars(
        drawScope: DrawScope, 
        values: List<Float>, 
        metrics: ChartMath.ChartMetrics, 
        color: Color,
        isMinimal: Boolean = false,
        barWidthMultiplier: Float = if (isMinimal) 0.8f else 0.5f,
        isInteractiveBars: Boolean = false,
    ): List<Pair<androidx.compose.ui.geometry.Rect, Float>> {
        val hitAreas = mutableListOf<Pair<androidx.compose.ui.geometry.Rect, Float>>()

        if (isInteractiveBars) {
            // 터치 상호작용용 바 그리기 (전체 너비, 전체 높이)
            val touchBarWidth = metrics.chartWidth / values.size
            val spacing = metrics.chartWidth / values.size
            
            values.forEachIndexed { i, value ->
                val touchBarX = metrics.paddingX + i * spacing
                
                // 터치 영역 바 색상 결정
                val touchBarColor = Color.Transparent
                
                drawScope.drawRect(
                    color = touchBarColor,
                    topLeft = Offset(touchBarX, 0f),
                    size = Size(touchBarWidth, metrics.chartHeight)
                )
                
                // 디버깅용 테두리 추가
                drawScope.drawRect(
                    color = Color.Red,
                    topLeft = Offset(touchBarX, 0f),
                    size = Size(touchBarWidth, metrics.chartHeight),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
                )

                // 터치 영역 저장
                val hitArea = androidx.compose.ui.geometry.Rect(
                    left = touchBarX,
                    top = 0f,
                    right = touchBarX + touchBarWidth,
                    bottom = metrics.chartHeight
                )
                hitAreas += Pair(hitArea, value)
            }
        } else {
            // 데이터 시각화용 바 그리기 (커스텀 너비, 데이터 높이)
            val barWidth = metrics.chartWidth / values.size * barWidthMultiplier
            val spacing = metrics.chartWidth / values.size
            
            values.forEachIndexed { i, value ->
                val barHeight = ((value - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
                
                // 모든 차트에서 바를 할당된 공간의 중앙에 배치
                val barX = metrics.paddingX + i * spacing + (spacing - barWidth) / 2f
                
                val barY = metrics.chartHeight - barHeight

                drawScope.drawRect(
                    color = color,
                    topLeft = Offset(barX, barY),
                    size = Size(barWidth, barHeight)
                )
            }
        }

        return hitAreas
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
}