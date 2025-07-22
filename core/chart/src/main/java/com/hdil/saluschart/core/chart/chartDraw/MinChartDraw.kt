package com.hdil.saluschart.core.chart.chartDraw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.hdil.saluschart.core.chart.chartMath.MinChartMath

object MinChartDraw {
    
    /**
     * 미니멀 범위 바를 그립니다 (컨테이너와 실제 범위 표시).
     * 
     * @param drawScope 그리기 영역
     * @param container 컨테이너 바 위치
     * @param rangeBar 범위 바 위치
     * @param containerColor 컨테이너 색상 (배경)
     * @param rangeColor 범위 바 색상
     * @param cornerRadius 모서리 둥글기
     */
    fun drawMinimalRangeBar(
        drawScope: DrawScope,
        container: MinChartMath.BarPosition,
        rangeBar: MinChartMath.BarPosition,
        containerColor: Color,
        rangeColor: Color,
        cornerRadius: Float = 8f
    ) {
        // 컨테이너 (배경) 그리기
        drawScope.drawRoundRect(
            color = containerColor,
            topLeft = Offset(container.x, container.y),
            size = Size(container.width, container.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
        )
        
        // 범위 바 그리기
        drawScope.drawRoundRect(
            color = rangeColor,
            topLeft = Offset(rangeBar.x, rangeBar.y),
            size = Size(rangeBar.width, rangeBar.height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
        )
    }
    
    /**
     * 미니멀 차트 위에 텍스트를 그립니다 (범위 값 등).
     * 
     * @param drawScope 그리기 영역
     * @param text 표시할 텍스트
     * @param position 텍스트 위치
     * @param color 텍스트 색상
     * @param textSize 텍스트 크기
     * @param alignment 텍스트 정렬
     */
    fun drawMinimalText(
        drawScope: DrawScope,
        text: String,
        position: Offset,
        color: Color = Color.Black,
        textSize: Float = 24f,
        alignment: android.graphics.Paint.Align = android.graphics.Paint.Align.CENTER
    ) {
        drawScope.drawContext.canvas.nativeCanvas.drawText(
            text,
            position.x,
            position.y,
            android.graphics.Paint().apply {
                this.color = color.value.toInt()
                this.textSize = textSize
                textAlign = alignment
                isFakeBoldText = true
            }
        )
    }
    
    /**
     * 미니멀 포인트들을 그립니다 (스파크라인의 끝점 등을 강조).
     * 
     * @param drawScope 그리기 영역
     * @param points 그릴 포인트들
     * @param color 포인트 색상
     * @param radius 포인트 반지름
     */
    fun drawMinimalPoints(
        drawScope: DrawScope,
        points: List<Offset>,
        color: Color,
        radius: Float = 3f
    ) {
        points.forEach { point ->
            drawScope.drawCircle(
                color = color,
                radius = radius,
                center = point
            )
        }
    }
}
