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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.ChartDraw
import com.hdil.saluschart.core.chart.ChartMath
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.ui.theme.ChartColor

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,      // ✅ ChartPoint 기반
    xLabel: String = "Time",
    yLabel: String = "Value",
    title: String = "Bar Chart Example",
    barColor: androidx.compose.ui.graphics.Color = ChartColor.Default,
    width: Dp = 250.dp,
    height: Dp = 250.dp
) {
    if (data.isEmpty()) return

    val xLabels = data.map { it.label ?: it.x.toString() }
    val yValues = data.map { it.y }

    // 터치한 바의 위치와 값을 저장할 상태 변수
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
                val metrics = ChartMath.computeMetrics(size, yValues, chartType = ChartType.BAR)

                ChartDraw.drawGrid(this, size, metrics)
                ChartDraw.drawXAxis(this, metrics)
                ChartDraw.drawYAxis(this, metrics)

                // 바를 그리고 히트 영역을 반환 받음
                val hitAreas = ChartDraw.drawBars(this, yValues, metrics, barColor)
                ChartDraw.drawBarXAxisLabels(drawContext, xLabels, metrics)

                // 터치한 위치가 어떤 바의 히트 영역에 있는지 확인
                touchedPosition?.let { position ->
                    for ((hitArea, value) in hitAreas) {
                        if (hitArea.contains(position)) {
                            // 터치한 바의 값을 저장하고 해당 위치에 툴팁 표시
                            touchedBarValue = value
                            ChartDraw.drawBarTooltip(this, value, position)
                            break
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
