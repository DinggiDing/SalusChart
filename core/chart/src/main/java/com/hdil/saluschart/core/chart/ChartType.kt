package com.hdil.saluschart.core.chart

enum class ChartType {
    LINE,
    BAR,
    RANGE_BAR,
    STACKED_BAR,
    PIE,
    SCATTERPLOT,
    SLEEPSTAGE_CHART,
    CALENDAR;

    companion object {
        fun fromString(type: String): ChartType? {
            return values().find { it.name.equals(type, ignoreCase = true) }
        }
    }
}