package com.hdil.saluschart.core.dsl

fun salusChart(block: ChartConfigBuilder.() -> Unit): ChartConfig {
    return ChartConfigBuilder().apply(block).build()
}