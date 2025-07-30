package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.ui.theme.ChartColor

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,      // ChartPoint 기반
    xLabel: String = "Time",
    yLabel: String = "Value",
    title: String = "Bar Chart Example",
    barColor: androidx.compose.ui.graphics.Color = ChartColor.Default,
    width: Dp = 250.dp,
    height: Dp = 250.dp,
    minY: Float? = null,                    // 사용자 지정 최소 Y값
    maxY: Float? = null,                    // 사용자 지정 최대 Y값
    barWidthMultiplier: Float = 0.8f,       // 바 너비 배수
    labelTextSize: Float = 28f,             // X축 레이블 텍스트 크기
    tooltipTextSize: Float = 32f            // 툴팁 텍스트 크기
) {
    if (data.isEmpty()) return

    val xLabels = data.map { it.label ?: it.x.toString() }
    val yValues = data.map { it.y }

    var touchedBarValue by remember { mutableStateOf<Float?>(null) }
    var touchedPosition by remember { mutableStateOf<Offset?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Box(
            modifier = modifier
                .width(width)
                .height(height)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            // 터치 이벤트 발생 시 처리
                            touchedPosition = tapOffset
                            // 툴팁을 표시하기 위해 상태 초기화 (바를 탭하지 않았을 때)
                            touchedBarValue = null
                        }
                    }
            ) {
                val metrics = ChartMath.computeMetrics(
                    size = size, 
                    values = yValues, 
                    chartType = ChartType.BAR,
                    minY = minY,
                    maxY = maxY
                )

                ChartDraw.drawGrid(this, size, metrics)
                ChartDraw.drawXAxis(this, metrics)
                ChartDraw.drawYAxis(this, metrics)

                val hitAreas = ChartDraw.Bar.drawBars(
                    drawScope = this, 
                    values = yValues, 
                    metrics = metrics, 
                    color = Color.Transparent,
                    barWidthMultiplier = 1.0f,
                    isInteractiveBars = true,
                )
                
                ChartDraw.Bar.drawBars(
                    drawScope = this, 
                    values = yValues, 
                    metrics = metrics, 
                    color = barColor,
                    barWidthMultiplier = barWidthMultiplier,
                    isInteractiveBars = false
                )

                ChartDraw.Bar.drawBarXAxisLabels(
                    ctx = drawContext, 
                    labels = xLabels, 
                    metrics = metrics,
                    textSize = labelTextSize
                )

                touchedPosition?.let { position ->
                    hitAreas.forEachIndexed { _, (hitArea, value) ->
                        if (hitArea.contains(position)) {
                            touchedBarValue = value

                            // 바 차트의 경우 터치 위치 위쪽에 툴팁 표시
                            // TODO: 툴팁 위치 바 위쪽으로 변경
                            val tooltipPosition = Offset(
                                position.x,
                                position.y - 40f
                            )

                            ChartDraw.drawTooltip(
                                drawScope = this,
                                value = value,
                                position = tooltipPosition,
                                textSize = tooltipTextSize
                            )
                            return@forEachIndexed
                        }
                    }

                    // 터치한 위치에 바가 없으면 툴팁 숨김
                    if (touchedBarValue == null) {
                        touchedPosition = null
                    }
                }
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}
