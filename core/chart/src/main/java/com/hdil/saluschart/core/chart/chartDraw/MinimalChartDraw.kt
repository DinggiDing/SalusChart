package com.hdil.saluschart.core.chart.chartDraw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas

object MinimalChartDraw {
    
    /**
     * 미니멀 범위 바를 그립니다 (컨테이너와 실제 범위 표시).
     * 
     * @param drawScope 그리기 영역
     * @param containerOffset 컨테이너 바 위치
     * @param containerSize 컨테이너 바 크기
     * @param rangeBarOffset 범위 바 위치
     * @param rangeBarSize 범위 바 크기
     * @param containerColor 컨테이너 색상 (배경)
     * @param rangeColor 범위 바 색상
     * @param cornerRadius 모서리 둥글기
     */
    fun drawMinimalRangeBar(
        drawScope: DrawScope,
        containerOffset: Offset,
        containerSize: Size,
        rangeBarOffset: Offset,
        rangeBarSize: Size,
        containerColor: Color,
        rangeColor: Color,
        cornerRadius: Float = 8f
    ) {
        // 컨테이너 (배경) 그리기
        drawScope.drawRoundRect(
            color = containerColor,
            topLeft = containerOffset,
            size = containerSize,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
        )
        
        // 범위 바 그리기
        drawScope.drawRoundRect(
            color = rangeColor,
            topLeft = rangeBarOffset,
            size = rangeBarSize,
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
        )
    }
}
