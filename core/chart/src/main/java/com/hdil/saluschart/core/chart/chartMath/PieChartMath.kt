package com.hdil.saluschart.core.chart.chartMath

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.hdil.saluschart.core.chart.ChartPoint

object PieChartMath {
    /**
     * 파이 차트의 중심점과 반지름을 계산합니다.
     *
     * @param size 캔버스 크기
     * @param padding 원 테두리 패딩 값
     * @return Pair(중심 좌표, 반지름)
     */
    fun computePieMetrics(size: Size, padding: Float = 32f): Pair<Offset, Float> {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = minOf(size.width, size.height) / 2 - padding
        return Pair(center, radius)
    }

    /**
     * 파이 차트의 각 섹션의 각도를 계산합니다.
     *
     * @param data 차트 데이터 포인트 목록
     * @return List<Triple<시작 각도, 스윕 각도, 값 비율>>
     */
    fun computePieAngles(data: List<ChartPoint>): List<Triple<Float, Float, Float>> {
        val totalValue = data.sumOf { it.y.toDouble() }.toFloat()
        if (totalValue <= 0f) return emptyList()

        var startAngle = -90f // 12시 방향에서 시작

        return data.map { point ->
            val ratio = point.y / totalValue
            val sweepAngle = ratio * 360f
            val result = Triple(startAngle, sweepAngle, ratio)
            startAngle += sweepAngle
            result
        }
    }

    /**
     * 파이 섹션의 레이블 위치를 계산합니다.
     *
     * @param center 원의 중심점
     * @param radius 원의 기본 반지름
     * @param radiusFactor 레이블 위치 조정을 위한 반지름 인수 (1보다 작으면 안쪽, 크면 바깥쪽)
     * @param angle 각도(라디안)
     * @return 레이블이 표시될 위치 좌표
     */
    fun calculateLabelPosition(center: Offset, radius: Float, radiusFactor: Float, angleInDegrees: Float): Offset {
        val angleInRadians = Math.toRadians(angleInDegrees.toDouble())
        val labelRadius = radius * radiusFactor
        val x = center.x + labelRadius * Math.cos(angleInRadians).toFloat()
        val y = center.y + labelRadius * Math.sin(angleInRadians).toFloat()
        return Offset(x, y)
    }
}