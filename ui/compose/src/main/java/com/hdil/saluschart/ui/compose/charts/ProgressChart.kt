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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.ChartType
import com.hdil.saluschart.core.chart.ProgressChartPoint
import com.hdil.saluschart.ui.theme.ColorUtils

/**
 * 프로그레스 차트를 표시하는 컴포저블 함수입니다.
 * 
 * @param modifier 모디파이어
 * @param data 프로그레스 차트에 표시할 데이터 리스트
 * @param title 차트 제목
 * @param isDonut 도넛 차트로 표시할지 여부 (true: 도넛, false: 바)
 * @param colors 각 프로그레스 인스턴스에 사용할 색상 목록
 * @param width 차트의 너비
 * @param height 차트의 높이
 * @param strokeWidth 도넛 차트의 링 두께 (도넛 모드일 때만)
 * @param barHeight 바 차트의 각 바 높이 (바 모드일 때만)
 * @param showLabels 라벨을 표시할지 여부
 * @param showValues 값을 표시할지 여부
 * @param showCenterInfo 중앙 정보를 표시할지 여부 (도넛 모드일 때만)
 * @param centerTitle 중앙 제목 텍스트 (도넛 모드일 때만)
 * @param centerSubtitle 중앙 부제목 텍스트 (도넛 모드일 때만)
 * @param chartType 차트 타입 (툴팁 위치 결정용)
 */
@Composable
fun ProgressChart(
    modifier: Modifier = Modifier,
    data: List<ProgressChartPoint>,
    title: String = "Progress Chart",
    isDonut: Boolean = true,
    isPercentage: Boolean = true,
    colors: List<Color> = ColorUtils.ColorUtils(data.size.coerceAtLeast(1)),
    width: Dp = 300.dp,
    height: Dp = 300.dp,
    strokeWidth: Float = 40f,
    barHeight: Float = 30f,
    showLabels: Boolean = true,
    showValues: Boolean = true,
    showCenterInfo: Boolean = true,
    centerTitle: String = "Activity",
    centerSubtitle: String = "Progress",
    chartType: ChartType = ChartType.PROGRESS // 차트 타입 (툴팁 위치 결정용)
) {
    if (data.isEmpty()) return

    Column(modifier = modifier.padding(16.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Box(
            Modifier
                .width(width)
                .height(height),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 프로그레스 마크 그리기 (도넛 또는 바)
                ChartDraw.Progress.drawProgressMarks(
                    drawScope = this,
                    data = data,
                    size = size,
                    colors = colors,
                    isDonut = isDonut,
                    strokeWidth = strokeWidth,
                    barHeight = barHeight
                )
                
                // 중앙 정보 표시 (도넛 모드일 때만)
                if (isDonut && showCenterInfo) {
                    val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height / 2f)
                    ChartDraw.Progress.drawProgressCenterInfo(
                        drawScope = this,
                        center = center,
                        title = centerTitle ?: "",
                        subtitle = centerSubtitle ?: ""
                    )
                }
                
                // 라벨 표시
                if (showLabels) {
                    ChartDraw.Progress.drawProgressLabels(
                        drawScope = this,
                        data = data,
                        size = size,
                        isDonut = isDonut,
                        strokeWidth = strokeWidth,
                        barHeight = barHeight
                    )
                }
                
                // 값 표시
                if (showValues) {
                    ChartDraw.Progress.drawProgressValues(
                        drawScope = this,
                        data = data,
                        size = size,
                        isDonut = isDonut,
                        strokeWidth = strokeWidth,
                        barHeight = barHeight,
                        isPercentage = isPercentage
                    )
                }
            }
        }

        Spacer(Modifier.height(4.dp))
    }
}
