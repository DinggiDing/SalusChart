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
        onClick: (() -> Unit)? = null
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
                .border(1.dp, Color.Red)
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
                Box(
                    modifier = Modifier
                        .offset(x = 0.dp, y = -(pointRadius * 4))
                        .width(IntrinsicSize.Min)
                ) {
                    Text(
                        text = value,
                        color = Color.Black,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

//    @Composable
//    fun PointMarker(
//        center: Offset,
//        value: String,
//        pointRadius: Dp = 8.dp,
//        innerRadius: Dp = 4.dp,
//        onClick: (() -> Unit)? = null  // 클릭 이벤트 콜백 추가
//    ) {
//        // Float 좌표를 Dp로 변환
//        val xDp = with(LocalDensity.current) { center.x.toDp() }
//        val yDp = with(LocalDensity.current) { center.y.toDp() }
//
//        Box(
//            modifier = Modifier
//                .offset(x = xDp - pointRadius, y = yDp - pointRadius)
//                .size(pointRadius * 2),
//            contentAlignment = Alignment.Center
//        ) {
//            // 바깥쪽 파란 원
//            Box(
//                modifier = Modifier
//                    .size(pointRadius * 2)
//                    .background(color = Color.Blue, shape = CircleShape)
//            )
//            // 안쪽 흰색 원
//            Box(
//                modifier = Modifier
//                    .size(innerRadius * 2)
//                    .background(color = Color.White, shape = CircleShape)
//            )
//        }
//    }

}