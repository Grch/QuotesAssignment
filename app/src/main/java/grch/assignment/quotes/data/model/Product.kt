package grch.assignment.quotes.data.model

enum class Product(
    val code: String,
    val displayCode: String
) {
    BTCUSD("BTCUSD", "BTC/USD"),
    EURUSD("EURUSD", "EUR/USD"),
    EURGBP("EURGBP", "EUR/GBP"),
    USDJPY("USDJPY", "USD/JPY"),
    GBPUSD("GBPUSD", "GBP/USD"),
    USDCHF("USDCHF", "USD/CHF"),
    USDCAD("USDCAD", "USD/CAD"),
    AUDUSD("AUDUSD", "AUD/USD"),
    EURJPY("EURJPY", "EUR/JPY"),
    EURCHF("EURCHF", "EUR/CHF")
}