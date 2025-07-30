package com.hdil.saluschart.core.chart

enum class ChartType {
    LINE,
    BAR,
    RANGE_BAR,
    STACKED_BAR,
    PIE,
    PROGRESS,
    SCATTERPLOT,
    SLEEPSTAGE_CHART,
    CALENDAR,
    MINIMAL_BAR,
    MINIMAL_LINE,
    MINIMAL_RANGE_BAR;

    companion object {
        fun fromString(type: String): ChartType? {
            return values().find { it.name.equals(type, ignoreCase = true) }
        }
    }
}

enum class InteractionType {
    POINT,         // Direct point touching using PointMarker()
    NEAR_X_AXIS    // Area-based touching using BarMarker()
}