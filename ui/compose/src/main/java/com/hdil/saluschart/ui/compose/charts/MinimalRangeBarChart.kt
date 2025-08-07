package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.RangeChartPoint
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath

/**
 * 미니멀 범위 바 차트 - 위젯이나 스마트워치 등 작은 화면용
 * 범위 데이터를 컨테이너 범위 내에서 표시하며, 상단에 범위 텍스트 표시
 * 
 * @param modifier 모디파이어
 * @param data 범위 차트 데이터 (yMin, yMax 포함)
 * @param containerMin 컨테이너의 최소값 (전체 범위 시작)
 * @param containerMax 컨테이너의 최대값 (전체 범위 끝)
 * @param containerColor 컨테이너(배경) 바 색상
 * @param rangeColor 범위 바 색상
 * @param textColor 범위 텍스트 색상
 * @param width 차트 너비
 * @param height 차트 높이
 * @param padding 차트 주변 패딩
 * @param showRangeText 범위 텍스트를 표시할지 여부
 * @param cornerRadius 바의 모서리 둥글기
 */
@Composable
fun MinimalRangeBarChart(
    modifier: Modifier = Modifier,
    data: RangeChartPoint,
    containerMin: Float,
    containerMax: Float,
    containerColor: Color = Color.LightGray,
    rangeColor: Color = Color(0xFFFF9500),
    textColor: Color = Color.Black,
    width: Dp = 120.dp,
    height: Dp = 50.dp,
    padding: Float = 8f,
    showRangeText: Boolean = true,
    cornerRadius: Float = 8f,
    chartType: ChartType = ChartType.MINIMAL_RANGE_BAR // 차트 타입 (툴팁 위치 결정용)
) {
    Box(
        modifier = modifier
            .then(
                if (width != Dp.Unspecified && height != Dp.Unspecified) {
                    Modifier.size(width, height)
                } else {
                    Modifier.fillMaxSize()
                }
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 범위 바 위치 계산
            val result = ChartMath.Min.computeMinimalRangeBarPosition(
                size = size,
                rangePoint = data,
                containerMin = containerMin,
                containerMax = containerMax,
                padding = padding
            )
            val (containerOffset, containerSize) = result.first
            val (rangeBarOffset, rangeBarSize) = result.second
            
            // 범위 바 그리기
            ChartDraw.Min.drawMinimalRangeBar(
                drawScope = this,
                containerOffset = containerOffset,
                containerSize = containerSize,
                rangeBarOffset = rangeBarOffset,
                rangeBarSize = rangeBarSize,
                containerColor = containerColor,
                rangeColor = rangeColor,
                cornerRadius = cornerRadius
            )
            
            // 범위 텍스트 표시
            if (showRangeText) {
                val rangeText = "${data.yMin.toInt()}-${data.yMax.toInt()}"
                val textY = padding + 16f // Position text in the reserved space at the top
                val textX = rangeBarOffset.x + (rangeBarSize.width / 2f)

                // Use Compose's native text drawing instead of Android Paint
                drawContext.canvas.nativeCanvas.apply {
                    val paint = android.graphics.Paint().apply {
                        color = textColor.value.toInt()
                        textSize = 16f
                        textAlign = android.graphics.Paint.Align.CENTER
                        isAntiAlias = true
                    }

                    // Calculate text bounds to ensure it's within canvas
                    val textBounds = android.graphics.Rect()
                    paint.getTextBounds(rangeText, 0, rangeText.length, textBounds)

                    // Ensure text doesn't go outside canvas bounds
                    val adjustedY = maxOf(textBounds.height().toFloat() + padding, textY)
                    val adjustedX = textX.coerceIn(textBounds.width() / 2f, size.width - textBounds.width() / 2f)

                    drawText(rangeText, adjustedX, adjustedY, paint)
                }
            }
        }
    }
}
