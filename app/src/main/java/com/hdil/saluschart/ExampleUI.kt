package com.hdil.saluschart

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.InteractionType
import com.hdil.saluschart.ui.compose.charts.BarChart
import com.hdil.saluschart.ui.theme.Primary_Purple


private val sampleData = listOf(10f, 25f, 40f, 20f, 35f, 55f, 45f)
private val weekDays = listOf("월", "화", "수", "목", "금", "토", "일")

// ChartPoint 리스트로 변환
private val chartPoints = sampleData.mapIndexed { index, value ->
    ChartPoint(
        x = index.toFloat(),
        y = value,
        label = weekDays.getOrElse(index) { "" }
    )
}


@Composable
fun ExampleUI(modifier: Modifier = Modifier) {
    val chartType = listOf(
        "BarChart 1",
        "BarChart 2",
        "BarChart 3",
        "BarChart 4",
        "DonutChart 1",
        "LineChart 1",
        "LineChart 2",
        "PieChart 1"
    )

    var selectedChartType by remember { mutableStateOf<String?>(null) }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        if (selectedChartType == null) {
            chartType.forEach { type ->
                Text(
                    text = type,
                    modifier = Modifier
                        .padding(12.dp)
                        .clickable { selectedChartType = type }
                )
            }
        } else {
            Button(
                onClick = { selectedChartType = null },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Back to list")
            }

            when (selectedChartType) {
                "BarChart 1" -> BarChart_1()
                "BarChart 2" -> BarChart_2()
                "BarChart 3" -> BarChart_3()
                "BarChart 4" -> BarChart_4()
                "DonutChart 1" -> DonutChart_1()
                "LineChart 1" -> LineChart_1()
                "LineChart 2" -> LineChart_2()
                "PieChart 1" -> PieChart_1()
            }
        }
    }
}

@Composable
fun BarChart_1() {
    BarChart(
        modifier = Modifier.fillMaxWidth().height(250.dp),
        data = chartPoints,
        xLabel = "Week",
        yLabel = "Value",
        title = "Weekly Data",
        barColor = Primary_Purple,
//        width = 250.dp,
//        height = 250.dp,
        minY = 2f,
        maxY = 60f,
        barWidthRatio = 0.5f,
        labelTextSize = 28f,
        tooltipTextSize = 32f,
        interactionType = InteractionType.BAR
    )
}

@Composable
fun BarChart_2() {
    Text("Displaying BarChart 2")
}

@Composable
fun BarChart_3() {
    Text("Displaying BarChart 3")
}

@Composable
fun BarChart_4() {
    Text("Displaying BarChart 4")
}

@Composable
fun DonutChart_1() {
    Text("Displaying DonutChart 1")
}

@Composable
fun LineChart_1() {
    Text("Displaying LineChart 1")
}

@Composable
fun LineChart_2() {
    Text("Displaying LineChart 2")
}

@Composable
fun PieChart_1() {
    Text("Displaying PieChart 1")
}
