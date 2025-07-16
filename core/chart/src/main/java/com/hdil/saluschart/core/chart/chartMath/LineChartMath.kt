package com.hdil.saluschart.core.chart.chartMath

import androidx.compose.ui.geometry.Offset
import kotlin.math.sqrt

object LineChartMath {
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