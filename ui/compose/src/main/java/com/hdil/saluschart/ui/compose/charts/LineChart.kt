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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.ui.theme.ChartColor

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,      // ChartPoint 기반
    xLabel: String = "Time",
    yLabel: String = "Value",
    title: String = "Line Chart Example",
    lineColor: androidx.compose.ui.graphics.Color = ChartColor.Default,
    width: Dp = 250.dp,
    height: Dp = 250.dp,
    strokeWidth: Float = 4f,
    labelTextSize: Float = 28f,
    tooltipTextSize: Float = 32f
) {
    if (data.isEmpty()) return

    val xLabels = data.map { it.x }
    val yValues = data.map { it.y }

    var canvasPoints by remember { mutableStateOf(listOf<Offset>()) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    var touchedBarValue by remember { mutableStateOf<Float?>(null) }
    var touchedPosition by remember { mutableStateOf<Offset?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Box(
            Modifier
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

                val metrics = ChartMath.computeMetrics(size, yValues)
                val points = ChartMath.mapToCanvasPoints(data, size, metrics)

                // 포인트 위치와 캔버스 크기를 상태 변수에 저장
                canvasPoints = points
                canvasSize = size
                
                val hitAreas = ChartDraw.Bar.drawBars(
                    drawScope = this,
                    values = yValues,
                    metrics = metrics,
                    color = Color.Transparent,
                    barWidthMultiplier = 1.0f,
                    isInteractiveBars = true,
                )
                
                ChartDraw.drawGrid(this, size, metrics)
                ChartDraw.Line.drawLine(this, points, lineColor, strokeWidth)
                ChartDraw.Line.drawXAxisLabels(
                    ctx = drawContext,
                    labels = xLabels.map { it.toString() },
                    metrics = metrics,
                    textSize = labelTextSize
                )

                touchedPosition?.let { position ->
                    hitAreas.forEachIndexed { index, (hitArea, value) ->
                        if (hitArea.contains(position)) {
                            touchedBarValue = value
                            
                            // calculateLabelPosition을 사용하여 최적 툴팁 위치 계산
                            val optimalPosition = ChartMath.Line.calculateLabelPosition(index, points)
                            
                            ChartDraw.drawTooltip(
                                drawScope = this,
                                value = value,
                                position = optimalPosition,
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
            // Canvas 위에 각 포인트마다 PointMarker 배치
            canvasPoints.forEachIndexed { index, point ->
                ChartDraw.Scatter.PointMarker(
                    center = point,
                    value = yValues[index].toInt().toString(),
                    isSelected = true  // 항상 기본 색상으로 표시
                )
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}