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
     * @param minValues 바의 최소값 목록 (일반 바 차트는 0, 범위 바 차트는 실제 최소값)
     * @param maxValues 바의 최대값 목록 (바의 상단 값)
     * @param metrics 차트 메트릭 정보
     * @param color 바 색상 (단일 바용)
     * @param barWidthRatio 바 너비 배수 (기본값: 0.8f)
     * @param interactive true이면 클릭 가능하고 툴팁 표시, false이면 순수 시각적 렌더링 (기본값: true)
     * @param useLineChartPositioning true이면 라인차트 포지셔닝 사용, false이면 바차트 포지셔닝 사용 (기본값: false)
     * @param onBarClick 바 클릭 시 호출되는 콜백 (바 인덱스, 툴팁 텍스트)
     * @param chartType 차트 타입 (툴팁 위치 결정용)
     * @param showTooltipForIndex 외부에서 제어되는 툴팁 표시 인덱스 (null이면 표시 안함)
     * @param isTouchArea true이면 터치 영역용 (투명, 전체 높이, 상호작용 가능), false이면 일반 바 (기본값: false)
     */
    @Composable
    fun BarMarker(
        minValues: List<Float>,
        maxValues: List<Float>,
        metrics: ChartMath.ChartMetrics,
        color: Color = Color.Black,
        barWidthRatio: Float = 0.8f,
        interactive: Boolean = true,
        useLineChartPositioning: Boolean = false,
        onBarClick: ((Int, String) -> Unit)? = null,
        chartType: ChartType,
        showTooltipForIndex: Int? = null,
        isTouchArea: Boolean = false
    ) {
        val density = LocalDensity.current

        // 터치 영역용인 경우 자동으로 파라미터 설정
        val actualBarWidthRatio = if (isTouchArea) 1.0f else barWidthRatio
        val actualInteractive = if (isTouchArea) true else interactive
        
        val dataSize = maxOf(minValues.size, maxValues.size)

        (0 until dataSize).forEach { index ->
            // 값 추출
            val minValue = minValues.getOrNull(index) ?: 0f
            val maxValue = maxValues.getOrNull(index) ?: 0f
            
            // 색상 결정
            val actualColor = if (isTouchArea) Color.Transparent else color
            
            val tooltipText = if (minValue == metrics.minY) maxValue.toInt().toString() else "${minValue.toInt()}-${maxValue.toInt()}" // TODO: Range Bar Chart, Stacked Bar Chart 에서 minValue가 최솟값과 일치할 때 처리 필요, 현재는 최솟값과 일치해도 최댓값만 표시

            // 바 높이와 위치 계산
            val (barHeight, barY) = if (isTouchArea) {
                // 전체 차트 높이 사용 (터치 영역용)
                Pair(metrics.chartHeight, 0f)
            } else {
                // minValue에서 maxValue까지의 바 계산
                val yMinScreen = metrics.chartHeight - ((minValue - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
                val yMaxScreen = metrics.chartHeight - ((maxValue - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
                val height = yMinScreen - yMaxScreen
                Pair(height, yMaxScreen)
            }

            // 바 X 위치 계산 - 차트 타입에 따라 다른 포지셔닝 로직 사용
            val (barWidth, barX) = if (useLineChartPositioning) {
                // 라인차트 포지셔닝: 포인트 중심에 바 배치
                val pointSpacing = if (dataSize > 1) metrics.chartWidth / (dataSize - 1) else 0f
                val pointX = metrics.paddingX + index * pointSpacing
                val barW = if (dataSize > 1) pointSpacing * actualBarWidthRatio else metrics.chartWidth * actualBarWidthRatio
                val barXPos = pointX - barW / 2f
                Pair(barW, barXPos)
            } else {
                // 바차트 포지셔닝: 할당된 공간의 중앙에 배치
                val barW = metrics.chartWidth / dataSize * actualBarWidthRatio
                val spacing = metrics.chartWidth / dataSize
                val barXPos = metrics.paddingX + index * spacing + (spacing - barW) / 2f
                Pair(barW, barXPos)
            }

            // Float 좌표를 Dp로 변환
            val barXDp = with(density) { barX.toDp() }
            val barYDp = with(density) { barY.toDp() }
            val barWidthDp = with(density) { barWidth.toDp() }
            val barHeightDp = with(density) { barHeight.toDp() }

            if (actualInteractive) {
                // 각 바의 툴팁 표시 상태
                var showTooltip by remember { mutableStateOf(false) }

                // 툴팁 표시 여부 결정: 
                // - isTouchArea = true인 경우 툴팁 표시 안함 (터치 영역용이므로)
                // - 바 차트 타입이 아닌 경우 툴팁 표시 안함 (LINE, SCATTERPLOT 등은 PointMarker 사용)
                val shouldShowTooltip = when {
                    isTouchArea -> false // 터치 영역용이므로 툴팁 표시 안함
                    chartType in listOf(ChartType.BAR, ChartType.RANGE_BAR, ChartType.STACKED_BAR) -> {
                        if (actualInteractive) showTooltip else (showTooltipForIndex == index)
                    }
                    else -> false // LINE, SCATTERPLOT 등에서는 툴팁 표시 안함
                }

                Box(
                    modifier = Modifier
                        .offset(x = barXDp, y = barYDp)
                        .size(width = barWidthDp, height = barHeightDp)
                        .background(color = actualColor)
                        .clickable {
                            // 툴팁 상태 토글
                            showTooltip = !showTooltip
                            // 외부 클릭 이벤트 처리
                            onBarClick?.invoke(index, tooltipText)
                        }
                ) {
                    // 툴팁 표시
                    if (shouldShowTooltip) {
                        val tooltipOffset = when (chartType) {
                            ChartType.BAR -> {
                                Modifier.offset(x = 0.dp, y = (-40).dp)
                            }
                            ChartType.RANGE_BAR -> {
                                Modifier.offset(x = 0.dp, y = (-40).dp)
                            }
                            ChartType.STACKED_BAR -> {
                                Modifier.offset(x = 0.dp, y = (-40).dp)
                            }
                            else -> {
                                Modifier.offset(x = 0.dp, y = (-40).dp)
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
                                text = tooltipText,
                                color = Color.White,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            } else {
                // 비상호작용 모드: 순수 시각적 렌더링만 (클릭 불가)
                // 툴팁 표시 여부 결정:
                // - isTouchArea = true인 경우 툴팁 표시 안함 (터치 영역용이므로)
                // - 바 차트 타입이 아닌 경우 툴팁 표시 안함 (LINE, SCATTERPLOT 등은 PointMarker 사용)
                val shouldShowTooltip = when {
                    isTouchArea -> false // 터치 영역용이므로 툴팁 표시 안함
                    chartType in listOf(ChartType.BAR, ChartType.RANGE_BAR, ChartType.STACKED_BAR) -> {
                        showTooltipForIndex == index
                    }
                    else -> false // LINE, SCATTERPLOT 등에서는 툴팁 표시 안함
                }
                
                Box(
                    modifier = Modifier
                        .offset(x = barXDp, y = barYDp)
                        .size(width = barWidthDp, height = barHeightDp)
                        .background(color = actualColor)
                ) {
                    // 외부에서 제어되는 툴팁 표시
                    if (shouldShowTooltip) {
                        val tooltipOffset = when (chartType) {
                            ChartType.BAR -> {
                                // 바 차트 툴팁: 바 위에 표시
                                Modifier.offset(x = 0.dp, y = (-40).dp)
                            }
                            ChartType.LINE -> {
                                // 라인 차트 툴팁: 바 위에 표시
                                Modifier.offset(x = 0.dp, y = (-40).dp)
                            }
                            ChartType.STACKED_BAR -> {
                                // 스택 바 차트 툴팁: 바 위에 표시
                                Modifier.offset(x = 0.dp, y = (-40).dp)
                            }
                            else -> {
                                // 기본 툴팁 위치: 바 위에 표시
                                Modifier.offset(x = 0.dp, y = (-40).dp)
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
                                text = tooltipText,
                                color = Color.White,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }


}