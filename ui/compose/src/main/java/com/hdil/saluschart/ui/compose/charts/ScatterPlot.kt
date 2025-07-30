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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.core.chart.ChartPoint

@Composable
fun ScatterPlot(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,
    xLabel: String = "X Axis",
    yLabel: String = "Y Axis",
    title: String = "Scatter Plot Example",
    pointColor: Color = com.hdil.saluschart.ui.theme.ChartColor.Default,
    width: androidx.compose.ui.unit.Dp = 250.dp,
    height: androidx.compose.ui.unit.Dp = 250.dp,
    tooltipTextSize: Float = 32f        // 툴팁 텍스트 크기

) {
    if (data.isEmpty()) return

    val xLabels = data.map { it.x }
    val yValues = data.map { it.y }

    var touchedBarValue by remember { mutableStateOf<Float?>(null) }
    var touchedPosition by remember { mutableStateOf<Offset?>(null) }

    var canvasPoints by remember { mutableStateOf(listOf<Offset>()) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            Modifier
                .width(width)
                .height(height)
        ) {
            Canvas(modifier = Modifier
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

                canvasPoints = points
                canvasSize = size

                ChartDraw.drawGrid(this, size, metrics)
                ChartDraw.Line.drawXAxisLabels(drawContext, xLabels.map { it.toString() }, metrics)
                val hitAreas = ChartDraw.Bar.drawBars(
                    drawScope = this,
                    values = yValues,
                    metrics = metrics,
                    color = Color.Transparent,
                    barWidthMultiplier = 1.0f,
                    isInteractiveBars = true,
                )

                touchedPosition?.let { position ->
                    hitAreas.forEachIndexed { index, (hitArea, value) ->
                        if (hitArea.contains(position)) {
                            touchedBarValue = value

                            // 포인트 마커 위에 툴팁 위치 계산 (몇 픽셀 위로)
                            val pointPosition = points[index]
                            val optimalPosition = Offset(
                                x = pointPosition.x,
                                y = pointPosition.y - 40f  // 40 픽셀 위로
                            )

                            ChartDraw.drawTooltip(
                                drawScope = this,
                                value = value,
                                position = optimalPosition,
                                textSize = tooltipTextSize
                            )
                            return@forEachIndexed  // 첫 번째 매치에서 중단
                        }
                    }

                    // 터치한 위치에 바가 없으면 툴팁 숨김
                    if (touchedBarValue == null) {
                        touchedPosition = null
                    }
                }
            }

            canvasPoints.forEachIndexed { index, point ->
                ChartDraw.Scatter.PointMarker(
                    center = point,
                    value = yValues[index].toInt().toString(),
                    isSelected = true  // 항상 기본 색상으로 표시
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}