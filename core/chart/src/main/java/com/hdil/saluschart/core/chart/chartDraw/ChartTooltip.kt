package com.hdil.saluschart.core.chart.chartDraw

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hdil.saluschart.core.chart.BaseChartPoint

/**
 * 차트 툴팁을 표시하는 컴포저블
 *
 * @param chartPoint 표시할 데이터 포인트
 * @param backgroundColor 툴팁 배경색
 * @param textColor 텍스트 색상
 * @param modifier 모디파이어
 */
@Composable
fun ChartTooltip(
    chartPoint: BaseChartPoint,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(8.dp)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp)
    ) {
        Column {
            chartPoint.label?.let { label ->
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            }
            when(chartPoint) {
                is com.hdil.saluschart.core.chart.StackedChartPoint -> {
                    chartPoint.values.forEachIndexed { index, value ->
                        Text(
                            text = "값 $index: $value",
                            fontSize = 12.sp,
                            color = textColor
                        )
                    }
                } else -> {
                    Text(
                        text = "값: ${chartPoint.y}",
                        fontSize = 12.sp,
                        color = textColor
                    )
                }
            }
        }
    }
}
