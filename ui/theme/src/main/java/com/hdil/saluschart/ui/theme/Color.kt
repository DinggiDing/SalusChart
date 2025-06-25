package com.hdil.saluschart.ui.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.abs

object ChartColor {
    val Default = Color(0xFF3F51B5)
}

object ColorUtils {
    /**
     * D3의 quantize + interpolateRainbow 방식을 흉내 낸 HSL 기반 무지개 색상 생성
     *
     * @param count 생성할 색상 개수
     * @return Compose UI Color 리스트
     */
    fun ColorUtils(count: Int): List<Color> {
        if (count <= 0) return emptyList()
        return List(count) { i ->
            val t = if (count > 1) (i+0.5f) / count else 0f
            val hue = 360f * t                // 0..360
            hslToColor(hue, 1f, 0.5f)
        }
    }

    /** HSL → Compose Color 변환 */
    private fun hslToColor(h: Float, s: Float, l: Float): Color {
        val c = (1f - abs(2 * l - 1f)) * s
        val x = c * (1f - abs((h / 60f) % 2f - 1f))
        val m = l - c / 2f

        val (r1, g1, b1) = when {
            h < 60f   -> Triple(c, x, 0f)
            h < 120f  -> Triple(x, c, 0f)
            h < 180f  -> Triple(0f, c, x)
            h < 240f  -> Triple(0f, x, c)
            h < 300f  -> Triple(x, 0f, c)
            else      -> Triple(c, 0f, x)
        }

        val r = (r1 + m).coerceIn(0f, 1f)
        val g = (g1 + m).coerceIn(0f, 1f)
        val b = (b1 + m).coerceIn(0f, 1f)
        return Color(r, g, b, 1f)
    }
}