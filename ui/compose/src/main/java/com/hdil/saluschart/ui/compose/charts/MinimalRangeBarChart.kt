package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.RangeChartPoint
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath

/**
 * 미니멀 범위 바 차트 - 위젯이나 스마트워치 등 작은 화면용
 * 단일 범위 데이터를 컨테이너와 범위 바로 표시하며, 상단에 범위 텍스트 표시
 * 
 * @param modifier 모디파이어
 * @param data 범위 차트 데이터 (단일 데이터 포인트)
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
    containerColor: Color = Color.LightGray,
    rangeColor: Color = Color(0xFFFF9500), // 오렌지색
    textColor: Color = Color.Black,
    width: Dp = 120.dp,
    height: Dp = 50.dp,
    padding: Float = 8f,
    showRangeText: Boolean = true,
    cornerRadius: Float = 8f
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
            val (container, rangeBar) = ChartMath.Min.computeMinimalRangeBarPosition(
                size = size,
                rangePoint = data,
                padding = padding
            )
            
            // 범위 바 그리기
            ChartDraw.Min.drawMinimalRangeBar(
                drawScope = this,
                container = container,
                rangeBar = rangeBar,
                containerColor = containerColor,
                rangeColor = rangeColor,
                cornerRadius = cornerRadius
            )
            
            // 범위 텍스트 표시
            if (showRangeText) {
                val rangeText = "${data.yMin.toInt()} - ${data.yMax.toInt()}"
                val textPosition = Offset(
                    x = size.width / 2f,
                    y = container.y - 8f // 바 위쪽에 위치
                )
                
                ChartDraw.Min.drawMinimalText(
                    drawScope = this,
                    text = rangeText,
                    position = textPosition,
                    color = textColor,
                    textSize = 16f,
                    alignment = android.graphics.Paint.Align.CENTER
                )
            }
        }
    }
}

