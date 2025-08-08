package com.hdil.saluschart.core.chart.chartDraw

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object GaugeChartDraw {
    /**
     * 범위 텍스트를 표시하는 컴포저블
     */
    @Composable
    fun RangeText(
        dataMin: Float,
        dataMax: Float,
        textColor: Color
    ) {
        Text(
            text = "${dataMin.toInt()}-${dataMax.toInt()}",
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }

    /**
     * 게이지 바를 그리는 컴포저블
     */
    @Composable
    fun GaugeBar(
        dataMin: Float,
        dataMax: Float,
        containerMin: Float,
        containerMax: Float,
        containerColor: Color,
        rangeColor: Color
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
        ) {
            val containerRange = containerMax - containerMin

            // 정규화된 위치와 크기 계산 (0.0 ~ 1.0 비율)
            val startRatio = if (containerRange > 0) {
                (dataMin - containerMin) / containerRange
            } else 0f
            val widthRatio = if (containerRange > 0) {
                (dataMax - dataMin) / containerRange
            } else 0f

            // 컨테이너 바 (배경)
            ContainerBar(
                containerColor = containerColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
            )

            // 범위 바 (실제 데이터 범위)
            RangeBar(
                rangeColor = rangeColor,
                startRatio = startRatio,
                widthRatio = widthRatio,
                modifier = Modifier.height(24.dp)
            )
        }
    }

    /**
     * 컨테이너(배경) 바 컴포저블
     */
    @Composable
    private fun ContainerBar(
        containerColor: Color,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(containerColor)
        )
    }

    /**
     * 범위 바 컴포저블
     */
    @Composable
    private fun RangeBar(
        rangeColor: Color,
        startRatio: Float,
        widthRatio: Float,
        modifier: Modifier = Modifier
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth(widthRatio)
                .offset(x = (300.dp * startRatio)) // 기본 너비 기준으로 오프셋 계산
                .clip(RoundedCornerShape(8.dp))
                .background(rangeColor)
        )
    }
}
