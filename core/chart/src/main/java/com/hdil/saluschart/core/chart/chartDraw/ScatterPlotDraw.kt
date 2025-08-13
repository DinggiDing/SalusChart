package com.hdil.saluschart.core.chart.chartDraw

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.core.chart.ChartType

object ScatterPlotDraw {

    /**
     * 각 데이터 포인트를 원으로 표시합니다.
     *
     * @param points 포인트 중심 좌표 목록
     * @param values 표시할 값 목록
     * @param pointRadius 포인트 외부 반지름
     * @param innerRadius 포인트 내부 반지름
     * @param selectedPointIndex 현재 선택된 포인트 인덱스 (null이면 모든 포인트 선택됨)
     * @param onPointClick 포인트 클릭 시 호출되는 콜백 (포인트 인덱스)
     * @param interactive true이면 클릭 가능하고 툴팁 표시, false이면 순수 시각적 렌더링 (기본값: true)
     * @param chartType 차트 타입 (툴팁 위치 결정용)
     * @param showTooltipForIndex 외부에서 제어되는 툴팁 표시 인덱스 (null이면 표시 안함)
     */
    @Composable
    fun PointMarker(
        data: List<ChartPoint>,
        points: List<Offset>,
        values: List<Float>,
        color: Color = Color.Black,
        pointRadius: Dp = 8.dp,
        innerRadius: Dp = 4.dp,
        selectedPointIndex: Int? = null,
        onPointClick: ((Int) -> Unit)? = null,
        interactive: Boolean = false,
        showPoint: Boolean = true,
        chartType: ChartType,
        showTooltipForIndex: Int? = null
    ) {
        val density = LocalDensity.current
        var shouldShowTooltip = false

        points.forEachIndexed { index, center ->
            // Float 좌표를 Dp로 변환
            val xDp = with(density) { center.x.toDp() }
            val yDp = with(density) { center.y.toDp() }

            // 선택 상태 결정: selectedPointIndex가 null이면 모든 포인트 선택됨
            val isSelected = selectedPointIndex == null || selectedPointIndex == index
            val outerColor = if (showPoint) if (isSelected) color else Color.Gray else if (selectedPointIndex == index) color else Color.Transparent
            val innerColor = if (showPoint || selectedPointIndex == index) Color.White else Color.Transparent

            // 툴팁 표시 여부 결정: showTooltipForIndex 값만 사용
            shouldShowTooltip = (showTooltipForIndex == index)

            if (interactive) {
                // 상호작용 모드: 클릭 가능하고 툴팁 표시
                Box(
                    modifier = Modifier
                        .offset(x = xDp - pointRadius, y = yDp - pointRadius)
                        .size(pointRadius * 2)
                        .clickable {
                            // 외부 클릭 이벤트 처리만 수행
                            onPointClick?.invoke(index)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // 바깥쪽 원 - 선택 상태에 따라 색상 변경
                    Box(
                        modifier = Modifier
                            .size(pointRadius * 2)
                            .background(color = outerColor, shape = CircleShape)
                    )
                    // 안쪽 흰색 원
                    Box(
                        modifier = Modifier
                            .size(innerRadius * 2)
                            .background(color = innerColor, shape = CircleShape)
                    )
                }
                // 툴팁 표시
                if (shouldShowTooltip) {
                    val labelOffset = when (chartType) {
                        ChartType.LINE -> {
                            // 라인 차트의 경우 calculateLabelPosition 사용
                            val optimalPosition = ChartMath.Line.calculateLabelPosition(index, points)

                            // 각 포인트마다 relative 위치를 계산
                            val relativeDx = with(density) {
                                (optimalPosition.x - center.x).toDp()
                            }
                            val relativeDy = with(density) {
                                (optimalPosition.y - center.y).toDp()
                            }

                            // 포인트 반지름을 고려하여 위치 조정
                            val adjustedDx = if (relativeDx > 0.dp) relativeDx + pointRadius
                            else if (relativeDx == 0.dp) relativeDx
                            else relativeDx - pointRadius
                            val adjustedDy = if (relativeDy > 0.dp) relativeDy + pointRadius
                            else if (relativeDy == 0.dp) relativeDy
                            else relativeDy - pointRadius

                            Modifier.offset(x = adjustedDx, y = adjustedDy)
                        }
                        else -> {
                            // 스캐터 차트의 경우 기본 위치 (포인트 위쪽)
                            Modifier.offset(x = 0.dp, y = -(pointRadius * 4))
                        }
                    }

                    ChartTooltip(
                        chartPoint = data[index],
                        modifier = Modifier.offset(x = xDp - pointRadius, y = yDp + pointRadius),
                    )
                }
            } else {
                // 비상호작용 모드: 순수 시각적 렌더링만 (클릭 불가)
                Box(
                    modifier = Modifier
                        .offset(x = xDp - pointRadius, y = yDp - pointRadius)
                        .size(pointRadius * 2),
                    contentAlignment = Alignment.Center
                ) {
                    // 바깥쪽 원
                    Box(
                        modifier = Modifier
                            .size(pointRadius * 2)
                            .background(color = outerColor, shape = CircleShape)
                    )
                    // 안쪽 흰색 원
                    Box(
                        modifier = Modifier
                            .size(innerRadius * 2)
                            .background(color = innerColor, shape = CircleShape)
                    )

                    // 외부에서 제어되는 툴팁 표시
                    if (shouldShowTooltip) {
                        val labelOffset = when (chartType) {
                            ChartType.LINE -> {
                                // 라인 차트의 경우 calculateLabelPosition 사용
                                val optimalPosition = ChartMath.Line.calculateLabelPosition(index, points)

                                // 각 포인트마다 relative 위치를 계산
                                val relativeDx = with(density) {
                                    (optimalPosition.x - center.x).toDp()
                                }
                                val relativeDy = with(density) {
                                    (optimalPosition.y - center.y).toDp()
                                }

                                // 포인트 반지름을 고려하여 위치 조정
                                val adjustedDx = if (relativeDx > 0.dp) relativeDx + pointRadius
                                else if (relativeDx == 0.dp) relativeDx
                                else relativeDx - pointRadius
                                val adjustedDy = if (relativeDy > 0.dp) relativeDy + pointRadius
                                else if (relativeDy == 0.dp) relativeDy
                                else relativeDy - pointRadius

                                Modifier.offset(x = adjustedDx, y = adjustedDy)
                            }
                            else -> {
                                // 스캐터 차트의 경우 기본 위치 (포인트 위쪽)
                                Modifier.offset(x = 0.dp, y = -(pointRadius * 4))
                            }
                        }

                        Box(
                            modifier = labelOffset
                                .width(IntrinsicSize.Min)
                                .background(
                                    color = Color.Black.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .border(1.dp, Color.Gray, RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = values.getOrElse(index) { 0f }.toInt().toString(),
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