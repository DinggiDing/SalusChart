package com.hdil.saluschart.core.chart.chartDraw

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.hdil.saluschart.core.chart.ProgressChartPoint
import com.hdil.saluschart.core.chart.chartMath.ChartMath

object ProgressChartDraw {
    
    /**
     * 프로그레스 마크를 그립니다 (도넛 또는 바 형태).
     * PieChart의 drawPieSection과 같은 패턴을 따릅니다.
     * 
     * @param drawScope 그리기 영역
     * @param data 프로그레스 차트 데이터 리스트
     * @param size 캔버스 크기
     * @param colors 각 프로그레스 인스턴스에 사용할 색상 목록
     * @param isDonut 도넛 형태로 그릴지 여부 (true: 도넛, false: 바)
     * @param strokeWidth 도넛일 경우 링의 두께
     * @param barHeight 바일 경우 각 바의 높이
     * @param backgroundAlpha 배경의 투명도
     */
    fun drawProgressMarks(
        drawScope: DrawScope,
        data: List<ProgressChartPoint>,
        size: Size,
        colors: List<Color>,
        isDonut: Boolean,
        strokeWidth: Float = 40f,
        barHeight: Float = 30f,
        backgroundAlpha: Float = 0.1f
    ) {
        if (isDonut) {
            // 도넛 차트 그리기
            val (center, maxRadius, ringRadii) = ChartMath.Progress.computeProgressDonutMetrics(
                size = size,
                data = data,
                strokeWidth = strokeWidth
            )
            
            data.forEachIndexed { index, point ->
                if (index < ringRadii.size) {
                    val radius = ringRadii[index]
                    val color = colors.getOrElse(index) { colors.first() }
                    val angles = ChartMath.Progress.computeProgressAngles(listOf(point))
                    val (startAngle, sweepAngle) = angles.first()
                    
                    // 배경 링 그리기 (전체 원)
                    drawScope.drawArc(
                        color = color.copy(alpha = backgroundAlpha),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth)
                    )
                    
                    // 프로그레스 호 그리기
                    drawScope.drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth)
                    )
                }
            }
        } else {
            // 바 차트 그리기
            val (barWidth, barYPositions) = ChartMath.Progress.computeProgressBarMetrics(
                size = size,
                data = data,
                barHeight = barHeight
            )
            val padding = 40f
            
            data.forEachIndexed { index, point ->
                if (index < barYPositions.size) {
                    val y = barYPositions[index]
                    val color = colors.getOrElse(index) { colors.first() }
                    val progressWidth = barWidth * point.progress
                    
                    // 배경 바 그리기 (전체 바)
                    drawScope.drawRect(
                        color = color.copy(alpha = backgroundAlpha),
                        topLeft = Offset(padding, y),
                        size = Size(barWidth, barHeight)
                    )
                    
                    // 프로그레스 바 그리기
                    drawScope.drawRect(
                        color = color,
                        topLeft = Offset(padding, y),
                        size = Size(progressWidth, barHeight)
                    )
                }
            }
        }
    }
    
    /**
     * 프로그레스 라벨을 그립니다.
     * 
     * @param drawScope 그리기 영역
     * @param data 프로그레스 차트 데이터 리스트
     * @param size 캔버스 크기
     * @param isDonut 도넛 차트 여부
     * @param strokeWidth 도넛일 경우 링의 두께
     * @param barHeight 바일 경우 각 바의 높이
     * @param textSize 텍스트 크기
     */
    fun drawProgressLabels(
        drawScope: DrawScope,
        data: List<ProgressChartPoint>,
        size: Size,
        isDonut: Boolean,
        strokeWidth: Float = 40f,
        barHeight: Float = 30f,
        textSize: Float = 32f
    ) {
        if (isDonut) {
            val (center, maxRadius, ringRadii) = ChartMath.Progress.computeProgressDonutMetrics(
                size = size,
                data = data,
                strokeWidth = strokeWidth
            )
            
            data.forEachIndexed { index, point ->
                val radius = ringRadii.getOrElse(index) { 0f }
                val labelPosition = ChartMath.Progress.computeLabelPosition(
                    center = center,
                    radius = radius,
                    isDonut = true,
                    point = point,
                )
                
                // 라벨 텍스트 그리기
                point.label?.let { label ->
                    drawScope.drawContext.canvas.nativeCanvas.drawText(
                        label,
                        labelPosition.x,
                        labelPosition.y,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.DKGRAY
                            this.textSize = textSize
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                    )
                }
            }
        } else {
            val (barWidth, barYPositions) = ChartMath.Progress.computeProgressBarMetrics(
                size = size,
                data = data,
                barHeight = barHeight
            )
            val center = Offset(size.width / 2f, size.height / 2f)
            
            data.forEachIndexed { index, point ->
                val barY = barYPositions.getOrElse(index) { 0f }
                val labelPosition = ChartMath.Progress.computeLabelPosition(
                    center = center,
                    isDonut = false,
                    point = point,
                    barY = barY,
                    barWidth = barWidth
                )
                
                // 라벨 텍스트 그리기
                point.label?.let { label ->
                    drawScope.drawContext.canvas.nativeCanvas.drawText(
                        label,
                        labelPosition.x,
                        labelPosition.y,
                        android.graphics.Paint().apply {
                            color = android.graphics.Color.DKGRAY
                            this.textSize = textSize
                            textAlign = android.graphics.Paint.Align.RIGHT
                            isFakeBoldText = true
                        }
                    )
                }
            }
        }
    }
    
    /**
     * 프로그레스 값을 그립니다.
     * 
     * @param drawScope 그리기 영역
     * @param data 프로그레스 차트 데이터 리스트
     * @param size 캔버스 크기
     * @param isDonut 도넛 차트 여부
     * @param strokeWidth 도넛일 경우 링의 두께
     * @param barHeight 바일 경우 각 바의 높이
     * @param textSize 텍스트 크기
     */
    fun drawProgressValues(
        drawScope: DrawScope,
        data: List<ProgressChartPoint>,
        size: Size,
        isDonut: Boolean,
        strokeWidth: Float = 40f,
        barHeight: Float = 30f,
        textSize: Float = 28f,
        isPercentage: Boolean
    ) {
        if (isDonut) {
            val (center, maxRadius, ringRadii) = ChartMath.Progress.computeProgressDonutMetrics(
                size = size,
                data = data,
                strokeWidth = strokeWidth
            )
            
            data.forEachIndexed { index, point ->
                val radius = ringRadii.getOrElse(index) { 0f }
                val valuePosition = ChartMath.Progress.computeValuePosition(
                    center = center,
                    radius = radius,
                    isDonut = true,
                    point = point
                )
                
                // 값 텍스트 생성
                val valueText = if (isPercentage) {
                    "${(point.percentage).toInt()}%"
                } else {
                    buildString {
                        append("${point.current.toInt()}")
                        point.unit?.let { append(" $it") }
                        append(" / ${point.max.toInt()}")
                        point.unit?.let { append(" $it") }
                    }
                }

                // 값 텍스트 그리기
                drawScope.drawContext.canvas.nativeCanvas.drawText(
                    valueText,
                    valuePosition.x,
                    valuePosition.y,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.BLACK
                        this.textSize = textSize
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                )
            }
        } else {
            val (barWidth, barYPositions) = ChartMath.Progress.computeProgressBarMetrics(
                size = size,
                data = data,
                barHeight = barHeight
            )
            val center = Offset(size.width / 2f, size.height / 2f)
            
            data.forEachIndexed { index, point ->
                val barY = barYPositions.getOrElse(index) { 0f }
                val valuePosition = ChartMath.Progress.computeValuePosition(
                    center = center,
                    isDonut = false,
                    point = point,
                    barY = barY,
                    barWidth = barWidth
                )

                // 값 텍스트 생성
                val valueText = if (isPercentage) {
                    "${(point.percentage).toInt()}%"
                } else {
                    buildString {
                        append("${point.current.toInt()}")
                        point.unit?.let { append(" $it") }
                        append(" / ${point.max.toInt()}")
                        point.unit?.let { append(" $it") }
                    }
                }
                
                // 값 텍스트 그리기
                drawScope.drawContext.canvas.nativeCanvas.drawText(
                    valueText,
                    valuePosition.x,
                    valuePosition.y,
                    android.graphics.Paint().apply {
                        color = android.graphics.Color.GRAY
                        this.textSize = textSize
                        textAlign = android.graphics.Paint.Align.LEFT
                    }
                )
            }
        }
    }
    
    /**
     * 프로그레스 중앙 요약 정보를 그립니다 (도넛 차트 전용).
     * 
     * @param drawScope 그리기 영역
     * @param center 중심점
     * @param title 제목 텍스트
     * @param subtitle 부제목 텍스트
     * @param titleSize 제목 텍스트 크기
     * @param subtitleSize 부제목 텍스트 크기
     */
    fun drawProgressCenterInfo(
        drawScope: DrawScope,
        center: Offset,
        title: String = "Activity",
        subtitle: String = "Progress",
        titleSize: Float = 36f,
        subtitleSize: Float = 24f
    ) {
        // 제목 그리기
        drawScope.drawContext.canvas.nativeCanvas.drawText(
            title,
            center.x,
            center.y - 10f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = titleSize
                textAlign = android.graphics.Paint.Align.CENTER
                isFakeBoldText = true
            }
        )
        
        // 부제목 그리기
        drawScope.drawContext.canvas.nativeCanvas.drawText(
            subtitle,
            center.x,
            center.y + 20f,
            android.graphics.Paint().apply {
                color = android.graphics.Color.GRAY
                textSize = subtitleSize
                textAlign = android.graphics.Paint.Align.CENTER
            }
        )
    }
}
