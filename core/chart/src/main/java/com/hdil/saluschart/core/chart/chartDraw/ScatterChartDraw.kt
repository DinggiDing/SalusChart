package com.hdil.saluschart.core.chart.chartDraw

import android.R.attr.onClick
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import kotlin.times
import kotlin.unaryMinus
import com.hdil.saluschart.core.chart.chartMath.ChartMath

object ScatterChartDraw {

    /**
     * 각 데이터 포인트를 원으로 표시하고 값을 레이블로 표시합니다.
     *
     * @param drawScope 그리기 영역
     * @param points 화면 좌표로 변환된 데이터 포인트 목록
     * @param values 데이터 포인트의 Y축 값 목록
     */
    @Composable
    fun PointMarker(
        center: Offset,        // Box 안의 0…width, 0…height 좌표
        value: String,
        pointRadius: Dp = 8.dp,
        innerRadius: Dp = 4.dp,
        isSelected: Boolean = true,  // true면 파란색, false면 회색
        onClick: (() -> Unit)? = null,
        isLineChart: Boolean = false,
        pointIndex: Int = 0,
        allPoints: List<Offset> = emptyList()
    ) {
        // 툴팁 표시 여부를 제어하는 상태
        var showTooltip by remember { mutableStateOf(false) }
        // Float 좌표를 Dp로 변환
        val xDp = with(LocalDensity.current) { center.x.toDp() }
        val yDp = with(LocalDensity.current) { center.y.toDp() }

        // 선택 상태에 따라 색상 결정
        // isSelected가 true이면 파란색(기본색), false이면 회색
        val outerColor = if (isSelected) Color.Blue else Color.Gray

        Box(
            modifier = Modifier
                .offset(x = xDp - pointRadius, y = yDp - pointRadius)
                .size(pointRadius * 2)
                // 클릭 리스너 추가
                .clickable {
                    // 툴팁 표시 상태 토글
                    showTooltip = !showTooltip
                    // 외부 클릭 이벤트도 처리
                    onClick?.invoke()
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
                    .background(color = Color.White, shape = CircleShape)
            )
            // 툴팁 표시
            if (showTooltip) {
                val labelOffset = if (isLineChart && allPoints.isNotEmpty()) {
                    val optimalPosition = ChartMath.Line.calculateLabelPosition(pointIndex, allPoints)

                    // 각 포인트마다 relative 위치를 계산
                    val relativeDx = with(LocalDensity.current) {
                        (optimalPosition.x - center.x).toDp()
                    }
                    val relativeDy = with(LocalDensity.current) {
                        (optimalPosition.y - center.y).toDp()
                    }

                    val adjustedDx = if (relativeDx > 0.dp) relativeDx + pointRadius else if (relativeDx == 0.dp) relativeDx else relativeDx - pointRadius
                    val adjustedDy = if (relativeDy > 0.dp) relativeDy + pointRadius else if (relativeDy == 0.dp) relativeDy else relativeDy - pointRadius

                    Modifier.offset(x = adjustedDx, y = adjustedDy)
                } else {
                    // Default positioning for scatter charts (above the point)
                    Modifier.offset(x = 0.dp, y = -(pointRadius * 3))
                }

                Text(
                    text = value,
                    color = Color.Black,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = labelOffset
                        .width(IntrinsicSize.Min)
                )
            }
        }
    }
}