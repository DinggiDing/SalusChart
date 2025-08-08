package com.hdil.saluschart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.sp
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.InteractionType
import com.hdil.saluschart.core.chart.chartDraw.LegendPosition
import com.hdil.saluschart.ui.compose.charts.BarChart
import com.hdil.saluschart.ui.compose.charts.BubbleType
import com.hdil.saluschart.ui.compose.charts.CalendarChart
import com.hdil.saluschart.ui.compose.charts.CalendarEntry
import com.hdil.saluschart.ui.compose.charts.LineChart
import com.hdil.saluschart.ui.compose.charts.MinimalBarChart
import com.hdil.saluschart.ui.compose.charts.PieChart
import com.hdil.saluschart.ui.compose.charts.ScatterPlot
import com.hdil.saluschart.ui.theme.Orange
import com.hdil.saluschart.ui.theme.Primary_Purple
import com.hdil.saluschart.ui.theme.Teel
import com.hdil.saluschart.ui.theme.Yellow
import java.time.LocalDate
import java.time.YearMonth


private val sampleData = listOf(10f, 25f, 40f, 20f, 35f, 55f, 45f)
private val sampleData2 = listOf(5f, 15f, 60f, 45f, 35f, 25f, 10f)
private val sampleData3 = listOf(8f, 22f, 10f, 40f, 18f, 32f, 12f)
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

private val chartPoint2 = sampleData2.mapIndexed { index, value ->
    ChartPoint(
        x = index.toFloat(),
        y = value,
        label = weekDays.getOrElse(index) { "" }
    )
}

private val chartPoint3 = sampleData3.mapIndexed { index, value ->
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
        "DonutChart 1",
        "LineChart 1",
        "LineChart 2",
        "PieChart 1",
        "CalendarChart 1",
        "CalendarChart 2",
        "ScatterPlot 1",
        "Minimal Bar Chart",
        "Minimal Line Chart",
    )

    var selectedChartType by remember { mutableStateOf<String?>("Minimal Bar Chart") }

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
                "DonutChart 1" -> DonutChart_1()
                "LineChart 1" -> LineChart_1()
                "LineChart 2" -> LineChart_2()
                "PieChart 1" -> PieChart_1()
                "CalendarChart 1" -> ChalendarChart_1()
                "CalendarChart 2" -> ChalendarChart_2()
                "ScatterPlot 1" -> ScatterPlot_1()
                "Minimal Bar Chart" -> Minimal_BarChart() // Placeholder for minimal bar chart
                "Minimal Line Chart" -> Minimal_LineChart() // Placeholder for minimal line chart
                else -> Text("Unknown Chart Type")
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
    Box(modifier = Modifier.fillMaxWidth().height(250.dp)) {

        LineChart(
            modifier = Modifier.fillMaxWidth().height(250.dp),
            data = chartPoints,
            title = "요일별 활동량",
            yLabel = "활동량",
            xLabel = "요일",
            lineColor = Primary_Purple,
            strokeWidth = 4f,
            minY = 0f,
            maxY = 60f,
            interactionType = InteractionType.POINT,
        )
        LineChart(
            modifier = Modifier.fillMaxWidth().height(250.dp),
            data = chartPoint2,
            title = "요일별 활동량",
            yLabel = "활동량",
            xLabel = "요일",
            lineColor = Teel,
            strokeWidth = 4f,
            minY = 0f,
            maxY = 60f,
            interactionType = InteractionType.POINT,
        )
        LineChart(
            modifier = Modifier.fillMaxWidth().height(250.dp),
            data = chartPoint3,
            title = "요일별 활동량",
            yLabel = "활동량",
            xLabel = "요일",
            lineColor = Orange,
            strokeWidth = 4f,
            minY = 0f,
            maxY = 60f,
            interactionType = InteractionType.POINT,
        )
    }
}

@Composable
fun LineChart_2() {
    LineChart(
        modifier = Modifier.fillMaxWidth().height(250.dp),
        data = chartPoints,
        title = "요일별 활동량",
        yLabel = "활동량",
        xLabel = "요일",
        lineColor = Primary_Purple,
        showPoint = true,
        strokeWidth = 4f,
        minY = 0f,
        maxY = 60f,
        interactionType = InteractionType.NEAR_X_AXIS,
    )
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

@Composable
fun ScatterPlot_1() {
    ScatterPlot(
        modifier = Modifier.fillMaxWidth().height(500.dp),
        data = chartPoints,
        title = "요일별 활동량",
        yLabel = "활동량",
        xLabel = "요일",
        interactionType = InteractionType.NEAR_X_AXIS
    )
}

@Composable
fun Minimal_BarChart() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
        elevation = androidx.compose.material3.CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.White
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
                ,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "오늘 운동",
                    color = Color.Black,
                    letterSpacing = 0.2.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "1시간 20분",
                    color = Primary_Purple,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .height(36.dp)
                    .align(androidx.compose.ui.Alignment.CenterVertically)
            ) {
                MinimalBarChart(
                    data = sampleData,
                    color = Primary_Purple,
                    width = 110.dp,
                    height = 36.dp,
                )
            }
        }
    }
}

@Composable
fun Minimal_LineChart() {
    // Placeholder for minimal line chart
}