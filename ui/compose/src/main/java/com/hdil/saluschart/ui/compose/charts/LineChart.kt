package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.ChartDraw
import com.hdil.saluschart.core.chart.ChartDraw.PointMarker
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.ui.theme.ChartColor
import kotlin.text.toInt

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    data: List<ChartPoint>,      // ✅ ChartPoint 기반
    xLabel: String = "Time",
    yLabel: String = "Value",
    title: String = "Line Chart Example",
    lineColor: androidx.compose.ui.graphics.Color = ChartColor.Default,
    width: Dp = 250.dp,
    height: Dp = 250.dp
) {
    if (data.isEmpty()) return

    val xLabels = data.map { it.x }
    val yValues = data.map { it.y }

    // 포인트 위치를 저장할 상태 변수
    var canvasPoints by remember { mutableStateOf(listOf<Offset>()) }
    var canvasSize by remember { mutableStateOf(Size.Zero) }

    // 선택된 포인트의 인덱스를 추적하는 상태 변수 추가
    // null일 경우 모든 포인트가 기본색상(파란색)
    var selectedPointIndex by remember { mutableStateOf<Int?>(null) }

    Column(modifier = modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Box(
            Modifier
                .width(width)
                .height(height)
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {

                val metrics = ChartMath.computeMetrics(size, yValues)
                val points = ChartMath.mapToCanvasPoints(data, size, metrics)

                // 포인트 위치와 캔버스 크기를 상태 변수에 저장
                canvasPoints = points
                canvasSize = size

                ChartDraw.drawGrid(this, size, metrics)
                ChartDraw.drawLinePath(this, points, lineColor)
//                ChartDraw.drawPoints(points, yValues)
                ChartDraw.drawXAxisLabels(drawContext, xLabels.map { it.toString() }, metrics)
            }
            // Canvas 위에 각 포인트마다 PointMarker 배치
            canvasPoints.forEachIndexed { index, point ->

                // PointMarker 컴포저블 배치
                PointMarker(
                    center = point,
                    value = yValues[index].toInt().toString(),
                    isSelected = selectedPointIndex == null || selectedPointIndex == index,
                    onClick = {
                        // 이미 선택된 포인트를 다시 클릭하면 선택 해제(null로 설정)
                        selectedPointIndex = if (selectedPointIndex == index) null else index
                    }
                )
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}
