package com.dataseer.app.model

enum class DSMethod(val value: String) {
    DESCRIBE("/describe"),
    HODRICK_PRESCOTT_FILTER("/hpf"),
    ETS_SEASONAL_DECOMPOSE("/etsd"),
    SIMPLE_MOVING_AVERAGE("/sma"),
    EXP_WEIGHTED_MOVING_AVERAGE("/ewsma"),
    SIMPLE_EXP_SMOOTHING("/sses"),
    DOUBLE_EXP_SMOOTHING("/dses"),
    TRIPLE_EXP_SMOOTHING("/tses")
}