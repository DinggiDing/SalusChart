package com.hdil.saluschart.core.chart.chartDraw

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hdil.saluschart.core.chart.RangeChartPoint
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.chartMath.ChartMath

object RangeBarChartDraw {
    /**
     * 범위 바 차트의 막대들을 Composable로 생성합니다.
     * 상호작용 여부를 제어할 수 있습니다.
     *
     * @param data 범위 차트 데이터 포인트 목록
     * @param metrics 차트 메트릭 정보
     * @param color 바 색상
     * @param barWidthRatio 바 너비 배수 (기본값: 0.8f)
     * @param interactive true이면 클릭 가능하고 툴팁 표시, false이면 순수 시각적 렌더링 (기본값: true)
     * @param onBarClick 바 클릭 시 호출되는 콜백 (바 인덱스, 범위값)
     * @param chartType 차트 타입 (툴팁 위치 결정용)
     * @param showTooltipForIndex 외부에서 제어되는 툴팁 표시 인덱스 (null이면 표시 안함)
     */
    @Composable
    fun RangeBarMarker(
        data: List<RangeChartPoint>,
        metrics: ChartMath.ChartMetrics,
        color: Color,
        barWidthRatio: Float = 0.8f,
        interactive: Boolean = true,
        onBarClick: ((Int, RangeChartPoint) -> Unit)? = null,
        chartType: ChartType,
        showTooltipForIndex: Int? = null
    ) {
        val density = LocalDensity.current

        data.forEachIndexed { index, rangePoint ->
            // 범위 바 높이와 위치 계산
            val yMinScreen = metrics.chartHeight - ((rangePoint.yMin - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
            val yMaxScreen = metrics.chartHeight - ((rangePoint.yMax - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
            val barHeight = yMinScreen - yMaxScreen // 범위의 높이

            // 바 X 위치 계산
            val barWidth = metrics.chartWidth / data.size * barWidthRatio
            val spacing = metrics.chartWidth / data.size
            val barX = metrics.paddingX + (spacing - barWidth) / 2 + index * spacing

            // Float 좌표를 Dp로 변환
            val barXDp = with(density) { barX.toDp() }
            val barYDp = with(density) { yMaxScreen.toDp() }
            val barWidthDp = with(density) { barWidth.toDp() }
            val barHeightDp = with(density) { barHeight.toDp() }

            if (interactive) {
                // 각 바의 툴팁 표시 상태
                var showTooltip by remember { mutableStateOf(false) }

                // 툴팁 표시 여부 결정
                val shouldShowTooltip = if (interactive) showTooltip else (showTooltipForIndex == index)

                Box(
                    modifier = Modifier
                        .offset(x = barXDp, y = barYDp)
                        .size(width = barWidthDp, height = barHeightDp)
                        .background(color = color)
                        .clickable {
                            // 툴팁 상태 토글
                            showTooltip = !showTooltip
                            // 외부 클릭 이벤트 처리
                            onBarClick?.invoke(index, rangePoint)
                        }
                ) {
                    // 툴팁 표시
                    if (shouldShowTooltip) {
                        val tooltipOffset = Modifier.offset(x = 0.dp, y = (-40).dp)

                        Box(
                            modifier = tooltipOffset
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                text = "${rangePoint.yMin.toInt()}-${rangePoint.yMax.toInt()}",
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
                // 툴팁 표시 여부 결정
                val shouldShowTooltip = (showTooltipForIndex == index)
                
                Box(
                    modifier = Modifier
                        .offset(x = barXDp, y = barYDp)
                        .size(width = barWidthDp, height = barHeightDp)
                        .background(color = color)
                ) {
                    // 외부에서 제어되는 툴팁 표시
                    if (shouldShowTooltip) {
                        val tooltipOffset = Modifier.offset(x = 0.dp, y = (-40).dp)

                        Box(
                            modifier = tooltipOffset
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .background(
                                    color = Color.Black.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                        ) {
                            Text(
                                text = "${rangePoint.yMin.toInt()}-${rangePoint.yMax.toInt()}",
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

//    /**
//     * 범위 바 차트의 막대들을 그립니다.
//     * 각 막대는 yMin에서 yMax까지의 범위를 표시합니다.
//     *
//     * @param drawScope 그리기 영역
//     * @param data 범위 차트 데이터 포인트 목록
//     * @param metrics 차트 메트릭 정보
//     * @param color 바 색상
//     * @param barWidthRatio 바 너비 비율 (0.0 ~ 1.0, 기본값 0.6)
//     */
//    fun drawRangeBars(
//        drawScope: DrawScope,
//        data: List<RangeChartPoint>,
//        metrics: ChartMath.ChartMetrics,
//        color: Color,
//        barWidthRatio: Float = 0.6f
//    ) {
//        val barWidth = (metrics.chartWidth / data.size) * barWidthRatio
//        val spacing = metrics.chartWidth / data.size
//
//        data.forEachIndexed { i, rangePoint ->
//            val yMinScreen = metrics.chartHeight - ((rangePoint.yMin - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
//            val yMaxScreen = metrics.chartHeight - ((rangePoint.yMax - metrics.minY) / (metrics.maxY - metrics.minY)) * metrics.chartHeight
//
//            val barHeight = yMinScreen - yMaxScreen // 범위의 높이
//            val barX = metrics.paddingX + (spacing - barWidth) / 2 + i * spacing
//
//            drawScope.drawRect(
//                color = color,
//                topLeft = Offset(barX, yMaxScreen),
//                size = Size(barWidth, barHeight)
//            )
//        }
//    }
}