package com.hdil.saluschart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hdil.saluschart.core.chart.ChartPoint
import com.hdil.saluschart.ui.compose.charts.LineChart
import com.hdil.saluschart.ui.theme.SalusChartTheme

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
    // 기본적인 raw 데이터로 차트 그리기 (FullLineChart 사용)
    val sampleData = listOf(10f, 25f, 40f, 20f, 35f, 55f, 45f)
    val weekDays = listOf("월", "화", "수", "목", "금", "토", "일")


    // ChartPoint 리스트로 변환
    val chartPoints = sampleData.mapIndexed { index, value ->
        ChartPoint(
            x = index.toFloat(),
            y = value,
            label = weekDays.getOrElse(index) { "" }
        )
    }

    LineChart(
        modifier = modifier,
        data = chartPoints,
        title = "기본 라인 차트 예제",
        yLabel = "활동량",
        xLabel = "요일"
    )

}

@Preview(showBackground = true)
@Composable
fun ChartPreview() {
    SalusChartTheme {
        SampleCharts()
    }
}