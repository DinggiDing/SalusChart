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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.core.chart.InteractionType
import com.hdil.saluschart.core.chart.ProgressChartPoint
import com.hdil.saluschart.core.chart.RangeChartPoint
import com.hdil.saluschart.core.chart.StackedChartPoint
import com.hdil.saluschart.core.chart.TimeDataPoint
import com.hdil.saluschart.core.chart.toChartPoints
import com.hdil.saluschart.core.chart.chartDraw.LegendPosition
import com.hdil.saluschart.core.util.TimeUnitGroup
import com.hdil.saluschart.core.transform.transform
import com.hdil.saluschart.ui.compose.charts.BarChart
import com.hdil.saluschart.ui.compose.charts.BubbleType
import com.hdil.saluschart.ui.compose.charts.CalendarChart
import com.hdil.saluschart.ui.compose.charts.CalendarEntry
import com.hdil.saluschart.ui.compose.charts.LineChart
import com.hdil.saluschart.ui.compose.charts.MinimalBarChart
import com.hdil.saluschart.ui.compose.charts.MinimalGaugeChart
import com.hdil.saluschart.ui.compose.charts.MinimalLineChart
import com.hdil.saluschart.ui.compose.charts.MinimalRangeBarChart
import com.hdil.saluschart.ui.compose.charts.PieChart
import com.hdil.saluschart.ui.compose.charts.ProgressChart
import com.hdil.saluschart.ui.compose.charts.RangeBarChart
import com.hdil.saluschart.ui.compose.charts.ScatterPlot
import com.hdil.saluschart.ui.compose.charts.StackedBarChart
import com.hdil.saluschart.ui.theme.Orange
import com.hdil.saluschart.ui.theme.Primary_Purple
import com.hdil.saluschart.ui.theme.Teel
import com.hdil.saluschart.ui.theme.Yellow
import java.time.LocalDate
import java.time.YearMonth

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
        "Minimal Chart",
        "Stacked Bar Chart",
        "Range Bar Chart",
        "Progress Bar Chart",
        "BarChart Timestep Transformation"
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
                "DonutChart 1" -> DonutChart_1()
                "LineChart 1" -> LineChart_1()
                "LineChart 2" -> LineChart_2()
                "PieChart 1" -> PieChart_1()
                "CalendarChart 1" -> ChalendarChart_1()
                "CalendarChart 2" -> ChalendarChart_2()
                "ScatterPlot 1" -> ScatterPlot_1()
                "Minimal Chart" -> Minimal_BarChart() // Placeholder for minimal bar chart
                "Stacked Bar Chart" -> StackedBarChart_1()
                "Range Bar Chart" -> RangeBarChart_1()
                "Progress Bar Chart" -> ProgressBarChart_1()
                "BarChart Timestep Transformation" -> TimeStepBarChart()
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
        maxY = 80f,
        barWidthRatio = 0.5f,
        labelTextSize = 28f,
        tooltipTextSize = 32f,
        interactionType = InteractionType.TOUCH_AREA
    )
}

@Composable
fun BarChart_2() {
    BarChart(
        modifier = Modifier.fillMaxWidth().height(250.dp),
        data = chartPoints,
        xLabel = "Week",
        yLabel = "Value",
        title = "Weekly Data",
        barColor = Primary_Purple,
        maxY = 60f,
        barWidthRatio = 0.5f,
        labelTextSize = 28f,
        tooltipTextSize = 32f,
        interactionType = InteractionType.BAR,
        showLabel = true
    )
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
            showPoint = false,
            minY = 0f,
            maxY = 60f,
            interactionType = InteractionType.TOUCH_AREA,
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
        interactionType = InteractionType.POINT,
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
        modifier = Modifier.width(300.dp).height(500.dp),
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
        pointColor = Primary_Purple,
        title = "요일별 활동량",
        yLabel = "활동량",
        xLabel = "요일",
        interactionType = InteractionType.POINT
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

                )
            }
        }
    }
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
                MinimalLineChart(
                    data = chartPoints,
                    color = Primary_Purple,
                )
            }
        }
    }
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
                    text = "일일 심박수",
                    color = Color.Black,
                    letterSpacing = 0.2.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "76 ~ 104 bpm",
                    color = Orange,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .height(52.dp),
                contentAlignment = Alignment.Center
            ) {
                MinimalRangeBarChart(
                    data = rangeData,
                    color = Orange,
                )
            }
        }
    }
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
                    text = "심박수",
                    color = Color.Black,
                    letterSpacing = 0.2.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "76 ~ 104 bpm",
                    color = Orange,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .height(52.dp)
                    .align(androidx.compose.ui.Alignment.CenterVertically)
            ) {
                val singleRangeData = RangeChartPoint(
                    x = 0f,
                    yMin = 78f,
                    yMax = 104f,
                    label = "Heart Rate"
                )
                MinimalGaugeChart(
                    data = singleRangeData,
                    containerMin = 60f,  // 정상 심박수 범위 시작
                    containerMax = 180f, // 정상 심박수 범위 끝
                    containerColor = Color.LightGray,
                    rangeColor = Orange,
                )
            }
        }
    }
}

@Composable
fun StackedBarChart_1() {
    StackedBarChart(
        modifier = Modifier.fillMaxWidth().height(500.dp),
        data = stackedData,
        segmentLabels = segmentLabels,
        title = "요일별 영양소 섭취량",
        yLabel = "영양소 (g)",
        xLabel = "요일",
        showLegend = true,
        colors = listOf(
            Color(0xFF2196F3), // 파랑 (단백질)
            Color(0xFFFF9800), // 주황 (지방)
            Color(0xFF4CAF50)  // 초록 (탄수화물)
        ),
        interactionType = InteractionType.STACKED_BAR
    )
}

@Composable
fun RangeBarChart_1() {
    RangeBarChart(
        modifier = Modifier.fillMaxWidth().height(500.dp),
        data = rangeData,
        title = "일별 심박수 범위",
        yLabel = "심박수 (bpm)",
        xLabel = "날짜",
        barColor = Color(0xFFFF9800),
        interactionType = InteractionType.TOUCH_AREA
    )
}

@Composable
fun ProgressBarChart_1() {
    val progressData = listOf(
        ProgressChartPoint(
            x = 0f,
            current = 1200f,
            max = 2000f,
            label = "Move",
            unit = "KJ"
        ),
        ProgressChartPoint(
            x = 1f,
            current = 20f,
            max = 60f,
            label = "Exercise",
            unit = "min"
        ),
        ProgressChartPoint(
            x = 2f,
            current = 7f,
            max = 10f,
            label = "Stand",
            unit = "h"
        )
    )
    ProgressChart(
        data = progressData,
        title = "일일 활동 진행률",
        isDonut = true,
        isPercentage = true,
        colors = listOf(
            Color(0xFF00C7BE), // 청록색 (Move)
            Color(0xFFFF6B35), // 주황색 (Exercise)
            Color(0xFF3A86FF)  // 파란색 (Stand)
        ),
        strokeWidth = 80f
    )
}

@Composable
fun TimeStepBarChart() {
    var expanded by remember { mutableStateOf(false) }
    var selectedOption by remember { mutableStateOf("시간대별") }

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "시간대별 걸음 수",
                modifier = Modifier.weight(1f),
                fontSize = 20.sp,
                color = Color.Black
            )
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown",
                    tint = Color.Black
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownMenuItem(
                text = { Text("시간대별") },
                onClick = {
                    selectedOption = "시간대별"
                    expanded = false
                },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenuItem(
                text = { Text("일별") },
                onClick = {
                    selectedOption = "일별"
                    expanded = false
                },
                modifier = Modifier.fillMaxWidth()
            )
            DropdownMenuItem(
                text = { Text("주별") },
                onClick = {
                    selectedOption = "주별"
                    expanded = false
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        BarChart(
            modifier = Modifier.fillMaxWidth().height(500.dp),
            data = timeDataPoint.transform(
                timeUnit = when (selectedOption) {
                    "시간대별" -> TimeUnitGroup.HOUR
                    "일별" -> TimeUnitGroup.DAY
                    "주별" -> TimeUnitGroup.WEEK
                    else -> TimeUnitGroup.HOUR
                },
            ).toChartPoints(),
            title = "걸음 수 (${selectedOption})",
            barColor = Primary_Purple,
            barWidthRatio = 0.5f,
            labelTextSize = when (selectedOption) {
                "시간대별" -> 0f
                "일별" -> 20f
                "주별" -> 28f
                else -> 0f
            } // X축 레이블 텍스트 비활성화 (겹침 방지)
        )
    }
}


// 범위 차트용 샘플 데이터 (심박수 범위 예시)
private val rangeData = listOf(
    RangeChartPoint(x = 0f, yMin = 54f, yMax = 160f, label = "2일"),
    RangeChartPoint(x = 1f, yMin = 65f, yMax = 145f, label = "3일"),
    RangeChartPoint(x = 2f, yMin = 58f, yMax = 125f, label = "4일"),
    RangeChartPoint(x = 3f, yMin = 75f, yMax = 110f, label = "6일"),
    RangeChartPoint(x = 4f, yMin = 68f, yMax = 162f, label = "7일"),
    RangeChartPoint(x = 5f, yMin = 72f, yMax = 168f, label = "8일"),
    RangeChartPoint(x = 6f, yMin = 65f, yMax = 138f, label = "9일"),
    RangeChartPoint(x = 7f, yMin = 85f, yMax = 105f, label = "10일")
)

// 스택 바 차트용 샘플 데이터 (일별 영양소 섭취량 예시)
private val stackedData = listOf(
    StackedChartPoint(
        x = 0f,
        values = listOf(80f, 45f, 120f), // 단백질, 지방, 탄수화물 (g)
        label = "월"
    ),
    StackedChartPoint(
        x = 1f,
        values = listOf(75f, 38f, 110f),
        label = "화"
    ),
    StackedChartPoint(
        x = 2f,
        values = listOf(90f, 52f, 140f),
        label = "수"
    ),
    StackedChartPoint(
        x = 3f,
        values = listOf(85f, 41f, 135f),
        label = "목"
    ),
    StackedChartPoint(
        x = 4f,
        values = listOf(95f, 58f, 150f),
        label = "금"
    ),
    StackedChartPoint(
        x = 5f,
        values = listOf(70f, 35f, 100f),
        label = "토"
    ),
    StackedChartPoint(
        x = 6f,
        values = listOf(88f, 48f, 125f),
        label = "일"
    )
)

// 스택 바 차트용 세그먼트 레이블 (한 번만 정의)
private val segmentLabels = listOf("단백질", "지방", "탄수화물")
private val sampleData = listOf(10f, 25f, 40f, 20f, 35f, 55f, 45f)
private val sampleData2 = listOf(5f, 15f, 60f, 45f, 35f, 25f, 10f)
private val sampleData3 = listOf(8f, 22f, 10f, 40f, 18f, 32f, 12f)
private val weekDays = listOf("월", "화", "수", "목", "금", "토", "일")
private val isoTime = listOf(
    "2025-05-05T00:00:00Z", "2025-05-05T06:00:00Z", "2025-05-05T12:00:00Z", "2025-05-05T18:00:00Z",
    "2025-05-06T00:00:00Z", "2025-05-06T06:00:00Z", "2025-05-06T12:00:00Z", "2025-05-06T18:00:00Z",
    "2025-05-07T00:00:00Z", "2025-05-07T06:00:00Z", "2025-05-07T12:00:00Z", "2025-05-07T18:00:00Z",
    "2025-05-08T00:00:00Z", "2025-05-08T06:00:00Z", "2025-05-08T12:00:00Z", "2025-05-08T18:00:00Z",
    "2025-05-09T00:00:00Z", "2025-05-09T06:00:00Z", "2025-05-09T12:00:00Z", "2025-05-09T18:00:00Z",
    "2025-05-10T00:00:00Z", "2025-05-10T06:00:00Z", "2025-05-10T12:00:00Z", "2025-05-10T18:00:00Z",
    "2025-05-11T00:00:00Z", "2025-05-11T06:00:00Z", "2025-05-11T12:00:00Z", "2025-05-11T18:00:00Z",
    "2025-05-12T00:00:00Z", "2025-05-12T06:00:00Z", "2025-05-12T12:00:00Z", "2025-05-12T18:00:00Z",
    "2025-05-13T00:00:00Z", "2025-05-13T06:00:00Z", "2025-05-13T12:00:00Z", "2025-05-13T18:00:00Z",
    "2025-05-14T00:00:00Z", "2025-05-14T06:00:00Z", "2025-05-14T12:00:00Z", "2025-05-14T18:00:00Z",
    "2025-05-15T00:00:00Z", "2025-05-15T06:00:00Z", "2025-05-15T12:00:00Z", "2025-05-15T18:00:00Z",
    "2025-05-16T00:00:00Z", "2025-05-16T06:00:00Z", "2025-05-16T12:00:00Z", "2025-05-16T18:00:00Z",
    "2025-05-17T00:00:00Z", "2025-05-17T06:00:00Z", "2025-05-17T12:00:00Z", "2025-05-17T18:00:00Z",
    "2025-05-18T00:00:00Z", "2025-05-18T06:00:00Z", "2025-05-18T12:00:00Z", "2025-05-18T18:00:00Z"
)


private val stepCounts = listOf(
    8123f, 523f, 9672f, 7540f,
    6453f, 984f, 8732f, 6891f,
    7215f, 642f, 9321f, 8990f,
    8320f, 885f, 7124f, 9983f,
    6152f, 751f, 8023f, 7654f,
    9472f, 934f, 8820f, 5932f,
    6723f, 653f, 9021f, 7114f,
    5987f, 752f, 8653f, 9411f,
    7840f, 801f, 9192f, 6833f,
    8794f, 912f, 7364f, 9950f,
    9332f, 891f, 9045f, 6021f,
    7981f, 912f, 6740f, 8942f,
    8024f, 992f, 9684f, 7782f,
    6875f, 864f, 8550f, 9333f,
    7121f, 941f, 9821f, 8732f
)

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

private val timeDataPoint = TimeDataPoint(
    x = isoTime,
    y = stepCounts,
    timeUnit = TimeUnitGroup.HOUR,
)
