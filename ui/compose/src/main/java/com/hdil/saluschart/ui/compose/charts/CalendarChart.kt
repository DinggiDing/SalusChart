package com.hdil.saluschart.ui.compose.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hdil.saluschart.core.chart.chartDraw.ChartDraw
import com.hdil.saluschart.core.chart.chartMath.ChartMath
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import java.util.*

/**
 * 캘린더 차트에 표시할 데이터 모델
 */
data class CalendarEntry(
    val date: LocalDate,
    val value: Float,
    val color: Color? = null
)

/**
 * 캘린더 차트를 표시하는 Composable 함수
 * 만약 entries의 날짜가 2달 이상이면 자동으로 MultiMonthCalendarChart로 전환됩니다.
 */
@Composable
fun CalendarChart(
    modifier: Modifier = Modifier,
    entries: List<CalendarEntry>,
    yearMonth: YearMonth = YearMonth.now(),
    maxBubbleSize: Float = 24f,
    minBubbleSize: Float = 6f,
    defaultColor: Color = MaterialTheme.colorScheme.primary,
    showGrid: Boolean = true,
    width: Dp = Dp.Unspecified,  // 너비 파라미터 추가
    height: Dp = 350.dp,         // height 파라미터 타입을 Dp로 변경
    onMonthChanged: ((YearMonth) -> Unit)? = null
) {
    // 2달 이상의 데이터가 있는지 확인
    val distinctMonths = entries.map { YearMonth.of(it.date.year, it.date.month) }.distinct()
    val hasMultipleMonths = distinctMonths.size > 1

    if (hasMultipleMonths) {
        // 2달 이상의 데이터가 있으면 MultiMonthCalendarChart 사용
        val startMonth = distinctMonths.minOrNull() ?: yearMonth
        val months = distinctMonths.size

        MultiMonthCalendarChart(
            modifier = modifier,
            entries = entries,
            startYearMonth = startMonth,
            months = months,
            maxBubbleSize = maxBubbleSize,
            minBubbleSize = minBubbleSize,
            defaultColor = defaultColor,
            showGrid = showGrid,
            width = width,           // 추가된 width 파라미터 전달
            height = height,         // 변경된 height 파라미터 타입 적용
            onMonthChanged = onMonthChanged
        )
    } else {
        // 단일 월 데이터만 있으면 기존 CalendarChart 로직 사용
        SingleMonthCalendarChart(
            modifier = modifier,
            entries = entries,
            yearMonth = yearMonth,
            maxBubbleSize = maxBubbleSize,
            minBubbleSize = minBubbleSize,
            defaultColor = defaultColor,
            showGrid = showGrid,
            width = width,           // 추가된 width 파라미터 전달
            height = height          // 변경된 height 파라미터 타입 적용
        )
    }
}

/**
 * 단일 월을 표시하는 캘린더 차트 (기존 CalendarChart 로직)
 */
@Composable
private fun SingleMonthCalendarChart(
    modifier: Modifier = Modifier,
    entries: List<CalendarEntry>,
    yearMonth: YearMonth = YearMonth.now(),
    maxBubbleSize: Float = 24f,
    minBubbleSize: Float = 6f,
    defaultColor: Color = MaterialTheme.colorScheme.primary,
    showGrid: Boolean = true,
    width: Dp = Dp.Unspecified,
    height: Dp = 350.dp
) {
    val maxValue = entries.maxOfOrNull { it.value } ?: 1f
    val entriesByDate = entries.associateBy { it.date }

    // 요일 배열 생성
    val dayOfWeeks = DayOfWeek.values()

    // 캘린더 정보 계산
    val (firstDayOfWeek, totalDays, weeks) = ChartMath.Calendar.computeCalendarMetrics(yearMonth)

    // width가 지정된 경우 적용하고, 없으면 fillMaxWidth 사용
    val columnModifier = if (width != Dp.Unspecified) {
        modifier.width(width).height(height)
    } else {
        modifier.fillMaxWidth().height(height)
    }

    Column(modifier = columnModifier) {
        // Compose UI 부분은 그대로 유지하고 Canvas 그리기 로직만 분리
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
                    color = if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
                        MaterialTheme.colorScheme.secondary
                    } else {
                        MaterialTheme.colorScheme.onBackground
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 캘린더 그리드
        Box(modifier = Modifier.fillMaxSize()) {
            // Canvas로 달력 그리드와 데이터 표시
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellWidth = size.width / 7f
                val cellHeight = size.height / weeks

                // 그리드 그리기
                if (showGrid) {
                    ChartDraw.Calendar.drawCalendarGrid(
                        drawScope = this,
                        cellWidth = cellWidth,
                        cellHeight = cellHeight,
                        rows = weeks,
                        startX = 0f,
                        startY = 0f
                    )
                }

                // 날짜와 데이터 포인트 그리기
                for (week in 0 until weeks) {
                    for (day in 0 until 7) {
                        val dayOfMonth = week * 7 + day - firstDayOfWeek + 1

                        if (dayOfMonth in 1..totalDays) {
                            val currentDate = yearMonth.atDay(dayOfMonth)
                            val isWeekend = day == 0 || day == 6

                            // 날짜 텍스트 위치
                            val x = cellWidth * (day + 0.5f)
                            val y = cellHeight * week + 20f

                            // 날짜 텍스트 그리기
                            ChartDraw.Calendar.drawCalendarDay(
                                ctx = drawContext,
                                day = dayOfMonth,
                                x = x,
                                y = y,
                                isWeekend = isWeekend,
                                textColor = Color.Black.toArgb(),
                            )

                            // 데이터가 있으면 원 그리기
                            val entry = entriesByDate[currentDate]
                            entry?.let { dataEntry ->
                                // 원 크기 계산
                                val bubbleRadius = ChartMath.Calendar.calculateBubbleSize(
                                    value = dataEntry.value,
                                    maxValue = maxValue,
                                    minSize = minBubbleSize,
                                    maxSize = maxBubbleSize
                                )

                                // 원 위치 (날짜 아래)
                                val bubbleX = x
                                val bubbleY = y + 25f

                                // 원 그리기
                                ChartDraw.Calendar.drawCalendarDataPoint(
                                    drawScope = this,
                                    x = bubbleX,
                                    y = bubbleY,
                                    radius = bubbleRadius,
                                    color = dataEntry.color ?: defaultColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 여러 월을 표시하는 캘린더 차트
 *
 * @param modifier 커스텀 modifier
 * @param entries 표시할 데이터 리스트
 * @param startYearMonth 시작 년월 (기본값: 현재 년월)
 * @param months 표시할 월 수
 * @param maxBubbleSize 최대 원 크기
 * @param minBubbleSize 최소 원 크기
 * @param defaultColor 기본 색상
 * @param showGrid 그리드 표시 여부
 * @param width 캘린더 너비
 * @param height 캘린더 높이
 * @param showMonthIndicator 월 인디케이터 표시 여부
 * @param onMonthChanged 월 변경 이벤트 콜백
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MultiMonthCalendarChart(
    modifier: Modifier = Modifier,
    entries: List<CalendarEntry>,
    startYearMonth: YearMonth = YearMonth.now(),
    months: Int = 12,
    maxBubbleSize: Float = 24f,
    minBubbleSize: Float = 6f,
    defaultColor: Color = MaterialTheme.colorScheme.primary,
    showGrid: Boolean = true,
    width: Dp = Dp.Unspecified,
    height: Dp = 350.dp,
    showMonthIndicator: Boolean = true,
    onMonthChanged: ((YearMonth) -> Unit)? = null
) {
    require(months > 0) { "표시할 월 수는 최소 1 이상이어야 합니다." }

    // 모든 월 생성
    val yearMonths = remember(startYearMonth, months) {
        (0 until months).map { startYearMonth.plusMonths(it.toLong()) }
    }

    // 페이저 상태 관리
    val pagerState = rememberPagerState(pageCount = { months })

    // 코루틴 스코프를 상위 컴포저블에서 선언
    val coroutineScope = rememberCoroutineScope()

    // 현재 월 변경 감지하여 콜백 호출
    val currentYearMonth = yearMonths[pagerState.currentPage]
    LaunchedEffect(pagerState.currentPage) {
        onMonthChanged?.invoke(currentYearMonth)
    }

    // width가 지정된 경우 적용하고, 없으면 fillMaxWidth 사용
    val columnModifier = if (width != Dp.Unspecified) {
        modifier.width(width)
    } else {
        modifier
    }

    Column(modifier = columnModifier) {
        // 월 인디케이터 표시 (상단에 배치)
        if (showMonthIndicator && months > 1) {
            MonthIndicator(
                yearMonths = yearMonths,
                currentIndex = pagerState.currentPage,
                onSelectMonth = { index ->
                    // 미리 선언된 코루틴 스코프 사용
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        // 수평 페이저로 스와이프 가능한 캘린더 구현
        val pagerHeight = height - if (showMonthIndicator && months > 1) 50.dp else 0.dp

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(pagerHeight)
        ) { page ->
            val yearMonth = yearMonths[page]
            val monthEntries = entries.filter {
                it.date.year == yearMonth.year && it.date.monthValue == yearMonth.monthValue
            }

            // 개별 월 캘린더 표시
            SingleMonthCalendarChart(
                entries = monthEntries,
                yearMonth = yearMonth,
                maxBubbleSize = maxBubbleSize,
                minBubbleSize = minBubbleSize,
                defaultColor = defaultColor,
                showGrid = showGrid,
                width = width,
                height = pagerHeight
            )
        }
    }
}

/**
 * 월별 인디케이터 (현재 월 표시)
 */
@Composable
private fun MonthIndicator(
    yearMonths: List<YearMonth>,
    currentIndex: Int,
    onSelectMonth: (Int) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        items(yearMonths.size) { index ->
            val yearMonth = yearMonths[index]
            val isSelected = index == currentIndex

            Text(
                text = "${yearMonth.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} ${yearMonth.year}",
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .clickable { onSelectMonth(index) },
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
