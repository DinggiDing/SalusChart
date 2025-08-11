package com.hdil.saluschart.core.util

enum class TimeUnitGroup {
    HOUR,
    DAY,
    WEEK,
    MONTH,
    YEAR;

    fun isSmallerThan(other: TimeUnitGroup): Boolean {
        return this.ordinal < other.ordinal
    }

    fun isBiggerThan(other: TimeUnitGroup): Boolean {
        return this.ordinal > other.ordinal
    }
}
