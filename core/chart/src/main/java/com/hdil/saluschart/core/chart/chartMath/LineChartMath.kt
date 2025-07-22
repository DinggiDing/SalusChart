package com.hdil.saluschart.core.chart.chartMath

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.hdil.saluschart.core.chart.ChartPoint
import kotlin.math.sqrt

object LineChartMath {
    
    /**
     * 라인 차트용 포인트들을 계산합니다.
     *
     * @param data 차트 데이터 포인트 목록
     * @param size Canvas의 전체 크기
     * @param metrics 차트 메트릭 정보
     * @param isMinimal 미니멀 차트 모드인지 여부
     * @return 화면 좌표로 변환된 Offset 목록
     */
    fun computeLinePoints(
        data: List<ChartPoint>, 
        size: Size, 
        metrics: ChartMath.ChartMetrics,
        isMinimal: Boolean = false
    ): List<Offset> {
        if (data.isEmpty()) return emptyList()
        
        return if (isMinimal) {
            // 미니멀: 간단한 좌표 변환
            val spacing = if (data.size > 1) metrics.chartWidth / (data.size - 1) else 0f
            data.mapIndexed { index, point ->
                val normalizedValue = if (metrics.maxY == metrics.minY) 0.5f 
                    else (point.y - metrics.minY) / (metrics.maxY - metrics.minY)
                
                val x = metrics.paddingX + index * spacing
                val y = size.height - metrics.paddingY - (metrics.chartHeight * normalizedValue)
                
                Offset(x, y)
            }
        } else {
            // 일반: 기존 ChartMath.mapToCanvasPoints 사용
            ChartMath.mapToCanvasPoints(data, size, metrics)
        }
    }

    /**
     * 탄젠트 벡터를 기반으로 최적의 라벨 위치를 계산합니다.
     * 라인과의 겹침을 최소화하기 위해 접선에 수직인 방향으로 라벨을 배치합니다.
     *
     * @param pointIndex 현재 포인트의 인덱스
     * @param points 모든 데이터 포인트들
     * @param labelText 표시할 라벨 텍스트
     * @return 최적의 라벨 위치
     */
    fun calculateLabelPosition(
        pointIndex: Int,
        points: List<Offset>,
        labelText: String
    ): Offset {
        val currentPoint = points[pointIndex]
        val baseDistance = 25f

        // Step 1: Calculate tangent vector
        val tangent = when {
            points.size < 2 -> Offset(1f, 0f) // Default horizontal for single point
            pointIndex == 0 -> {
                // Start point: use direction to next point
                val direction = points[1] - currentPoint
                normalizeVector(direction)
            }
            pointIndex == points.size - 1 -> {
                // End point: use direction from previous point
                val direction = currentPoint - points[pointIndex - 1]
                normalizeVector(direction)
            }
            else -> {
                // Interior point: average of incoming and outgoing directions
                val incoming = normalizeVector(currentPoint - points[pointIndex - 1])
                val outgoing = normalizeVector(points[pointIndex + 1] - currentPoint)
                normalizeVector(
                    Offset(
                        (incoming.x + outgoing.x) / 2f,
                        (incoming.y + outgoing.y) / 2f
                    )
                )
            }
        }

        // Step 2: Calculate normal vectors (perpendicular to tangent)
        val normal1 = Offset(-tangent.y, tangent.x)   // 90° counterclockwise
        val normal2 = Offset(tangent.y, -tangent.x)   // 90° clockwise

        // Step 3: Generate candidate positions
        val candidate1 = Offset(
            currentPoint.x + normal1.x * baseDistance,
            currentPoint.y + normal1.y * baseDistance
        )
        val candidate2 = Offset(
            currentPoint.x + normal2.x * baseDistance,
            currentPoint.y + normal2.y * baseDistance
        )

        // Step 4: Choose the better candidate (prefer upward direction)
        return if (candidate1.y < candidate2.y) candidate1 else candidate2
    }

    /**
     * 벡터를 정규화합니다.
     *
     * @param vector 정규화할 벡터
     * @return 정규화된 벡터 (길이가 1인 단위 벡터)
     */
    fun normalizeVector(vector: Offset): Offset {
        val magnitude = sqrt(vector.x * vector.x + vector.y * vector.y)
        return if (magnitude > 0f) {
            Offset(vector.x / magnitude, vector.y / magnitude)
        } else {
            Offset(1f, 0f)
        }
    }
}