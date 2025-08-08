package com.hdil.saluschart.ui.compose.charts

import android.graphics.Paint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

/**
 * 캘린더 차트에 표시할 데이터 모델
 */
data class CalendarEntry(
    val date: LocalDate,
    val value: Float,
    val color: Color? = null
)

enum class BubbleType {
    CIRCLE, // 단일 월 데이터
    RECTANGLE // 여러 월 데이터
}

/**
 * 캘린더 차트를 표시하는 Composable 함수
 */
@Composable
fun CalendarChart(
    modifier: Modifier = Modifier,
    entries: List<CalendarEntry>,
    yearMonth: YearMonth = YearMonth.now(),
    bubbleType: BubbleType = BubbleType.CIRCLE,
    maxBubbleSize: Float = 10f,
    minBubbleSize: Float = 6f,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    // 단일 월 데이터만 있으면 기존 CalendarChart 로직 사용
    SingleMonthCalendarChart(
        modifier = modifier,
        entries = entries,
        yearMonth = yearMonth,
        bubbleType = bubbleType,
        maxBubbleSize = maxBubbleSize,
        minBubbleSize = minBubbleSize,
        color = color,
    )
}

/**
 * 단일 월을 표시하는 캘린더 차트 (기존 CalendarChart 로직)
 */
@Composable
private fun SingleMonthCalendarChart(
    modifier: Modifier = Modifier,
    entries: List<CalendarEntry>,
    yearMonth: YearMonth = YearMonth.now(),
    bubbleType: BubbleType,
    maxBubbleSize: Float,
    minBubbleSize: Float,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    val maxValue = entries.maxOfOrNull { it.value } ?: 1f
    val entriesByDate = entries.associateBy { it.date }

    // 요일 배열 생성
    val dayOfWeeks = listOf(DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)
    // 캘린더 정보 계산
    val (firstDayOfWeek, totalDays, weeks) = ChartMath.Calendar.computeCalendarMetrics(yearMonth)

    Column(modifier = modifier) {
        // 월/년 제목
        Text(
            text = yearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()) + " " + yearMonth.year,
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // 요일 헤더
        Row(modifier = Modifier.fillMaxWidth()) {
            dayOfWeeks.forEach { dayOfWeek ->
                Text(
                    text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 캘린더 그리드 - Composable로 변환
        Column(modifier = modifier) {
            for (week in 0 until weeks) {
                Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                    for (day in 0 until 7) {
                        val dayOfMonth = week * 7 + day - firstDayOfWeek + 1

                        CalendarCellComposable(
                            modifier = Modifier
                                .weight(1f),
                            dayOfMonth = dayOfMonth,
                            totalDays = totalDays,
                            isWeekend = day == 0,
                            entry = if (dayOfMonth in 1..totalDays) {
                                entriesByDate[yearMonth.atDay(dayOfMonth)]
                            } else null,
                            maxValue = maxValue,
                            bubbleType = bubbleType,
                            minBubbleSize = minBubbleSize,
                            maxBubbleSize = maxBubbleSize,
                            color = color
                        )
                    }
                }
            }
        }
    }
}


/**
 * 개별 캘린더 셀을 Composable로 구현
 */
@Composable
private fun CalendarCellComposable(
    modifier: Modifier = Modifier,
    dayOfMonth: Int,
    totalDays: Int,
    isWeekend: Boolean,
    entry: CalendarEntry?,
    maxValue: Float,
    bubbleType: BubbleType,
    minBubbleSize: Float,
    maxBubbleSize: Float,
    color: Color
) {
    Box(
        modifier = modifier.padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (dayOfMonth in 1..totalDays) {
            if (bubbleType == BubbleType.CIRCLE) {
                Column(
                    modifier = Modifier.fillMaxSize().fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // 상단: 날짜 텍스트
                    Text(
                        text = dayOfMonth.toString(),
                        fontSize = 12.sp,
                        color = if (isWeekend) Color.Red else Color.Black,
                        textAlign = TextAlign.Center
                    )

                    // 중앙: 데이터 포인트(원)
                    entry?.let { dataEntry ->
                        val bubbleRadius = ChartMath.Calendar.calculateBubbleSize(
                            value = dataEntry.value,
                            maxValue = maxValue,
                            minSize = minBubbleSize,
                            maxSize = maxBubbleSize
                        )
                        val bubbleColor = ChartMath.Calendar.calculateBubbleColor(
                            color = dataEntry.color ?: color,
                            value = dataEntry.value,
                            maxValue = maxValue,
                            minSize = minBubbleSize,
                            maxSize = maxBubbleSize
                        )
                        Box(
                            modifier = Modifier
                                .size((bubbleRadius * 2).dp)
                                .clip(CircleShape)
                                .background(bubbleColor)
                        )
                    }
                        ?: Spacer(modifier = Modifier.height((maxBubbleSize * 2).dp)) // 데이터 없을 때 공간 확보
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize().fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    // 중앙: 데이터 포인트(사각형)
                    entry?.let { dataEntry ->
                        val bubbleRadius = ChartMath.Calendar.calculateBubbleSize(
                            value = dataEntry.value,
                            maxValue = maxValue,
                            minSize = minBubbleSize,
                            maxSize = maxBubbleSize
                        )
                        val bubbleColor = ChartMath.Calendar.calculateBubbleColor(
                            color = dataEntry.color ?: color,
                            value = dataEntry.value,
                            maxValue = maxValue,
                            minSize = minBubbleSize,
                            maxSize = maxBubbleSize
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(4.dp))
                                .background(bubbleColor)
                        )
                    }?: Spacer(modifier = Modifier.height((maxBubbleSize * 2).dp)) // 데이터 없을 때 공간 확보
                    // 상단: 날짜 텍스트
                    Text(
                        text = dayOfMonth.toString(),
                        fontSize = 12.sp,
                        color = if (isWeekend) Color.Red else Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}