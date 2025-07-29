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
     * 각 데이터 포인트를 원으로 표시합니다 (시각적 표시용, 비상호작용).
     *
     * @param center 포인트 중심 좌표
     * @param pointRadius 포인트 외부 반지름
     * @param innerRadius 포인트 내부 반지름 
     * @param isSelected 선택 상태 (색상 결정용)
     * @param isLineChart 라인 차트 여부 (미사용, 호환성용)
     * @param pointIndex 포인트 인덱스 (미사용, 호환성용)
     * @param allPoints 모든 포인트 (미사용, 호환성용)
     */
    @Composable
    fun PointMarker(
        center: Offset,        // Box 안의 0…width, 0…height 좌표
        value: String,
        pointRadius: Dp = 8.dp,
        innerRadius: Dp = 4.dp,
        isSelected: Boolean = true,  // true면 파란색, false면 회색
        onClick: (() -> Unit)? = null,  // 호환성용, 무시됨
        isLineChart: Boolean = false,   // 호환성용, 무시됨
        pointIndex: Int = 0,            // 호환성용, 무시됨
        allPoints: List<Offset> = emptyList()  // 호환성용, 무시됨
    ) {
        // Float 좌표를 Dp로 변환
        val xDp = with(LocalDensity.current) { center.x.toDp() }
        val yDp = with(LocalDensity.current) { center.y.toDp() }

        // 선택 상태에 따라 색상 결정
        val outerColor = if (isSelected) Color.Blue else Color.Gray

        Box(
            modifier = Modifier
                .offset(x = xDp - pointRadius, y = yDp - pointRadius)
                .size(pointRadius * 2),
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
        }
    }
}