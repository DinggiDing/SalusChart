package com.hdil.saluschart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.InteractionType
import com.hdil.saluschart.core.chart.chartDraw.LegendPosition
import com.hdil.saluschart.ui.compose.charts.BarChart
import com.hdil.saluschart.ui.compose.charts.BubbleType
import com.hdil.saluschart.ui.compose.charts.CalendarChart
import com.hdil.saluschart.ui.compose.charts.CalendarEntry
import com.hdil.saluschart.ui.compose.charts.LineChart
import com.hdil.saluschart.ui.compose.charts.PieChart
import com.hdil.saluschart.ui.theme.Orange
import com.hdil.saluschart.ui.theme.Primary_Purple
import com.hdil.saluschart.ui.theme.Teel
import com.hdil.saluschart.ui.theme.Yellow
import java.time.LocalDate
import java.time.YearMonth


private val sampleData = listOf(10f, 25f, 40f, 20f, 35f, 55f, 45f)
private val weekDays = listOf("월", "화", "수", "목", "금", "토", "일")

private val yearMonth = YearMonth.now()
private val startDate = LocalDate.of(yearMonth.year, 8, 1)
private val endDate = LocalDate.of(yearMonth.year, 8, 25)
private val random = java.util.Random(0)
private val entries = generateSequence(startDate) { date ->
    if (date.isBefore(endDate)) date.plusDays(1) else null
}.map { date ->
    val value = random.nextFloat() * 100
    CalendarEntry(
        date = date,
        value = value,
    )
}.toList()

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
        "PieChart 1",
        "CalendarChart 1",
        "CalendarChart 2"
    )

    var selectedChartType by remember { mutableStateOf<String?>("LineChart 1") }

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

            IconButton(
                onClick = { selectedChartType = null },
                modifier = Modifier
                    .padding(top = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.Black,
                    modifier = Modifier.size(32.dp) // 아이콘 크기 조정

                )
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
                "CalendarChart 1" -> ChalendarChart_1()
                "CalendarChart 2" -> ChalendarChart_2()
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
    PieChart(
        modifier = Modifier.fillMaxWidth().height(250.dp),
        data = chartPoints.subList(0, 4),
        title = "요일별 활동량",
        isDonut = true,
        colors = listOf(Primary_Purple, Teel, Orange, Yellow),
        showLegend = true,
        showLabel = true
    )
}

@Composable
fun LineChart_1() {
    LineChart(
        data = chartPoints,
        title = "요일별 활동량",
        yLabel = "활동량",
        xLabel = "요일",
        strokeWidth = 10f,
        interactionType = InteractionType.POINT,
    )
}

@Composable
fun LineChart_2() {
    Text("Displaying LineChart 2")
}

@Composable
fun PieChart_1() {
    PieChart(
        modifier = Modifier.fillMaxWidth().height(500.dp),
        data = chartPoints.subList(0, 4),
        title = "요일별 활동량",
        isDonut = false,
        colors = listOf(Primary_Purple, Teel, Orange, Yellow),
        showLegend = true,
        legendPosition = LegendPosition.RIGHT

    )
}

@Composable
fun ChalendarChart_1() {
    CalendarChart(
        modifier = Modifier.fillMaxWidth().height(600.dp),
        entries = entries,
        yearMonth = yearMonth,
        color = Primary_Purple,
        maxBubbleSize = 10f,
        minBubbleSize = 6f
    )
}

@Composable
fun ChalendarChart_2() {
    CalendarChart(
        modifier = Modifier.width(300.dp).height(200.dp),
        entries = entries,
        yearMonth = yearMonth,
        color = Primary_Purple,
        bubbleType = BubbleType.RECTANGLE,
        maxBubbleSize = 10f,
        minBubbleSize = 6f
    )
}