package com.hdil.saluschart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.ui.compose.charts.LineChart
import com.hdil.saluschart.ui.compose.charts.ScatterPlot
import com.hdil.saluschart.ui.theme.SalusChartTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow


import kotlin.text.toInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SalusChartTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    SampleCharts(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun SampleCharts(modifier: Modifier = Modifier) {
    // 차트 타입 선택 상태 관리
    var selectedChartType by remember { mutableStateOf("Line") }

    // 기본적인 raw 데이터로 차트 그리기
    val sampleData = listOf(10f, 25f, 40f, 20f, 35f, 55f, 45f)
    val weekDays = listOf("월", "화", "수", "목", "금", "토", "일")

    // 드롭다운 메뉴를 위한 상태 관리
    var widthExpanded by remember { mutableStateOf(false) }
    var heightExpanded by remember { mutableStateOf(false) }

    val widthOptions = listOf(150.dp, 200.dp, 250.dp, 300.dp, 350.dp)
    val heightOptions = listOf(150.dp, 200.dp, 250.dp, 300.dp, 350.dp)

    var selectedWidth by remember { mutableStateOf(widthOptions[1]) }
    var selectedHeight by remember { mutableStateOf(heightOptions[1]) }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 차트 타입 선택 토글 버튼
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(
                    selected = selectedChartType == "Line",
                    onClick = { selectedChartType = "Line" },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                    label = { Text("Line Chart") }
                )
                SegmentedButton(
                    selected = selectedChartType == "Scatter",
                    onClick = { selectedChartType = "Scatter" },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                    label = { Text("Scatter Plot") }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Width 드롭다운
            Column {
                Box {
                    OutlinedButton(
                        onClick = { widthExpanded = !widthExpanded },
                    ) {
                        Text("차트 너비: ${selectedWidth.value.toInt()}dp")
                    }

                    DropdownMenu(
                        expanded = widthExpanded,
                        onDismissRequest = { widthExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        widthOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text("${option.value.toInt()}dp") },
                                onClick = {
                                    selectedWidth = option
                                    widthExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Height 드롭다운
            Column {
                Box {
                    OutlinedButton(
                        onClick = { heightExpanded = !heightExpanded },
                    ) {
                        Text("차트 높이: ${selectedHeight.value.toInt()}dp")
                    }

                    DropdownMenu(
                        expanded = heightExpanded,
                        onDismissRequest = { heightExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.7f)
                    ) {
                        heightOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text("${option.value.toInt()}dp") },
                                onClick = {
                                    selectedHeight = option
                                    heightExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // ChartPoint 리스트로 변환
        val chartPoints = sampleData.mapIndexed { index, value ->
            ChartPoint(
                x = index.toFloat(),
                y = value,
                label = weekDays.getOrElse(index) { "" }
            )
        }

        // 선택된 차트 타입에 따라 다른 차트 표시
        when (selectedChartType) {
            "Line" -> {
                LineChart(
                    data = chartPoints,
                    title = "요일별 활동량",
                    yLabel = "활동량",
                    xLabel = "요일",
                    width = selectedWidth,
                    height = selectedHeight
                )
            }
            "Scatter" -> {
                ScatterPlot(
                    data = chartPoints,
                    title = "요일별 활동량",
                    yLabel = "활동량",
                    xLabel = "요일",
                    width = selectedWidth,
                    height = selectedHeight
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChartPreview() {
    SalusChartTheme {
        SampleCharts()
    }
}