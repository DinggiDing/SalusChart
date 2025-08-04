package com.hdil.saluschart.core.chart.chartDraw

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawContext
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hdil.saluschart.core.chart.ChartType
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
     * 바 차트 막대들을 Composable로 생성합니다.
     * 상호작용 여부를 제어할 수 있습니다.
     *
     * @param values 원본 데이터 값 목록
     * @param metrics 차트 메트릭 정보
     * @param color 바 색상
     * @param barWidthRatio 바 너비 배수 (기본값: 0.8f)
     * @param useFullHeight true이면 전체 차트 높이, false이면 데이터에 맞는 높이 (기본값: false)
     * @param interactive true이면 클릭 가능하고 툴팁 표시, false이면 순수 시각적 렌더링 (기본값: true)
     * @param useLineChartPositioning true이면 라인차트 포지셔닝 사용, false이면 바차트 포지셔닝 사용 (기본값: false)
     * @param onBarClick 바 클릭 시 호출되는 콜백 (바 인덱스, 값)
     * @param chartType 차트 타입 (툴팁 위치 결정용)
     */
    @Composable
    fun BarMarker(
        values: List<Float>,
        metrics: ChartMath.ChartMetrics,
        color: Color,
        barWidthRatio: Float = 0.8f,
        useFullHeight: Boolean = false,
        interactive: Boolean = true,
        useLineChartPositioning: Boolean = false,
        onBarClick: ((Int, Float) -> Unit)? = null,
        chartType: ChartType
    ) {
        val density = LocalDensity.current

        values.forEachIndexed { index, value ->
            // 바 높이와 위치 계산
            val (barHeight, barY) = if (useFullHeight) {
                // 전체 차트 높이 사용 (터치 영역용)
                Pair(metrics.chartHeight, 0f)
            } else {
                // 데이터에 맞는 높이 사용 (시각화용)
                val height = ((value - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
                Pair(height, metrics.chartHeight - height)
            }

            // 바 X 위치 계산 - 차트 타입에 따라 다른 포지셔닝 로직 사용
            val (barWidth, barX) = if (useLineChartPositioning) {
                // 라인차트 포지셔닝: 포인트 중심에 바 배치
                val pointSpacing = if (values.size > 1) metrics.chartWidth / (values.size - 1) else 0f
                val pointX = metrics.paddingX + index * pointSpacing
                val barW = if (values.size > 1) pointSpacing * barWidthRatio else metrics.chartWidth * barWidthRatio
                val barXPos = pointX - barW / 2f
                Pair(barW, barXPos)
            } else {
                // 바차트 포지셔닝: 할당된 공간의 중앙에 배치
                val barW = metrics.chartWidth / values.size * barWidthRatio
                val spacing = metrics.chartWidth / values.size
                val barXPos = metrics.paddingX + index * spacing + (spacing - barW) / 2f
                Pair(barW, barXPos)
            }

            // Float 좌표를 Dp로 변환
            val barXDp = with(density) { barX.toDp() }
            val barYDp = with(density) { barY.toDp() }
            val barWidthDp = with(density) { barWidth.toDp() }
            val barHeightDp = with(density) { barHeight.toDp() }

            if (interactive) {
                // 각 바의 툴팁 표시 상태
                var showTooltip by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .offset(x = barXDp, y = barYDp)
                        .size(width = barWidthDp, height = barHeightDp)
                        .background(color = color)
                        .clickable {
                            // 툴팁 상태 토글
                            showTooltip = !showTooltip
                            // 외부 클릭 이벤트 처리
                            onBarClick?.invoke(index, value)
                        }
                ) {
                    // 툴팁 표시
                    if (showTooltip) {
                        val tooltipOffset = when (chartType) {
                            ChartType.BAR -> {
                                // 바 차트 툴팁: 바 위에 표시
                                Modifier.offset(x = 0.dp, y = -(barHeightDp + 40.dp))
                            }
                            ChartType.LINE -> {
                                // 라인 차트 툴팁: 바 위에 표시
                                Modifier.offset(x = 0.dp, y = -(barHeightDp + 40.dp))
                            }
                            ChartType.STACKED_BAR -> {
                                // 스택 바 차트 툴팁: 바 위에 표시
                                Modifier.offset(x = 0.dp, y = -(barHeightDp + 40.dp))
                            }
                            else -> {
                                // 기본 툴팁 위치: 바 위에 표시
                                Modifier.offset(x = 0.dp, y = -(barHeightDp + 40.dp))
                            }
                        }

                        Box(
                            modifier = tooltipOffset
                                .width(IntrinsicSize.Min)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                text = value.toInt().toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            } else {
                // 비상호작용 모드: 순수 시각적 렌더링만
                Box(
                    modifier = Modifier
                        .offset(x = barXDp, y = barYDp)
                        .size(width = barWidthDp, height = barHeightDp)
                        .background(color = color)
                )
            }
        }
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