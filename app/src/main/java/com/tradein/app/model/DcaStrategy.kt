package com.tradein.app.model

data class DcaStrategy(
    val id: Long = 0,
    val assetSymbol: String,       // e.g. BTC/USDT
    val investmentAmount: Double,  // e.g. 50.0 (USD)
    val frequency: String,         // Daily / Weekly / Monthly
    val startDate: String,         // e.g. 2024-01-01
    val endDate: String,           // e.g. 2024-12-31
    val notes: String = ""
)
