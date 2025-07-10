package org.example

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.serialization.Serializable

// Extension function to fetch daily crypto news
suspend fun HttpClient.getDailyCryptoNews(apiKey: String): List<CryptoNews> {
    val response = this.get("https://cryptocurrency-news2.p.rapidapi.com/v1/cryptodaily") {
        header("X-RapidAPI-Key", apiKey)
        header("X-RapidAPI-Host", "cryptocurrency-news2.p.rapidapi.com")
    }.body<CryptoNewsResponse>()
    return response.data
}

suspend fun HttpClient.getDailyCryptoNewsFromTheGuardian(apiKey: String): List<CryptoNews> {
    val response = this.get("https://cryptocurrency-news2.p.rapidapi.com/v1/theguardian") {
        header("X-RapidAPI-Key", apiKey)
        header("X-RapidAPI-Host", "cryptocurrency-news2.p.rapidapi.com")
    }.body<CryptoNewsResponse>()
    return response.data
}

suspend fun HttpClient.getAdaPrice(apiKey: String): AdaCryptoInfo {
    val response = this.get("https://cardano-price.p.rapidapi.com/ADA.json") {
        header("X-RapidAPI-Key", apiKey)
        header("X-RapidAPI-Host", "cardano-price.p.rapidapi.com")
    }.body<AdaCryptoInfo>()
    return response
}

suspend fun HttpClient.getStockOverview(
    apiKey: String,
    symbol: String,
    language: String = "en"
): StockCompanyOverviewResponse {
    val response = this.get("https://real-time-finance-data.p.rapidapi.com/stock-overview") {
        url.parameters.append("symbol", symbol)
        url.parameters.append("language", language)
        header("X-RapidAPI-Key", apiKey)
        header("X-RapidAPI-Host", "real-time-finance-data.p.rapidapi.com")
    }.body<StockCompanyOverviewResponse>()
    return response
}


suspend fun HttpClient.getCashFlow(
    apiKey: String,
    symbol: String,
    period: String,
    language: String = "en"
): CompanyCashFlowResponse {
    val response = this.get("https://real-time-finance-data.p.rapidapi.com/company-cash-flow") {
        url.parameters.append("symbol", symbol)
        url.parameters.append("period", period)
        url.parameters.append("language", language)
        header("X-RapidAPI-Key", apiKey)
        header("X-RapidAPI-Host", "real-time-finance-data.p.rapidapi.com")
    }.body<CompanyCashFlowResponse>()
    return response
}

suspend fun HttpClient.getIncomeStatement(
    apiKey: String,
    symbol: String,
    period: String,
    language: String = "en"
): CompanyIncomeStatementResponse {
    val response = this.get("https://real-time-finance-data.p.rapidapi.com/company-income-statement") {
        url.parameters.append("symbol", symbol)
        url.parameters.append("period", period)
        url.parameters.append("language", language)
        header("X-RapidAPI-Key", apiKey)
        header("X-RapidAPI-Host", "real-time-finance-data.p.rapidapi.com")
    }.body<CompanyIncomeStatementResponse>()
    return response
}

suspend fun HttpClient.convertCurrency(
    apiKey: String,
    from: String,
    to: String,
    amount: Int
): ConvertResponse {
    val response = this.get("https://currency-conversion-and-exchange-rates.p.rapidapi.com/convert") {
        url {
            parameters.append("from", from)
            parameters.append("to", to)
            parameters.append("amount", amount.toString())
        }
        header("x-rapidapi-key", apiKey)
        header("x-rapidapi-host", "currency-conversion-and-exchange-rates.p.rapidapi.com")
    }.body<ConvertResponse>()
    return response
}

suspend fun HttpClient.getEconomicEvents(
    apiKey: String,
    from: String,
    to: String,
    countries: String
): EconomicEventsResponse {
    val response = this.get("https://ultimate-economic-calendar.p.rapidapi.com/economic-events/tradingview") {
        url {
            parameters.append("from", from)
            parameters.append("to", to)
            parameters.append("countries", countries)
        }
        header("x-rapidapi-key", apiKey)
        header("x-rapidapi-host", "ultimate-economic-calendar.p.rapidapi.com")
    }.body<EconomicEventsResponse>()
    return response
}

@Serializable
data class EconomicEventsResponse(
    val result: List<EconomicEvents> ? = null,
    val status: String? = null
)

@Serializable
data class EconomicEvents(
    val actual: Double?,
    val comment: String,
    val country: String,
    val currency: String,
    val date: String,
    val forecast: Double?,
    val id: String,
    val importance: Int,
    val indicator: String,
    val link: String,
    val period: String,
    val previous: Double?,
    val scale: String,
    val source: String,
    val title: String,
    val unit: String
)


@Serializable
data class CompanyIncomeStatementResponse(
    val request_id: String? = null,
    val status: String? = null,
    val data: CompanyIncomeStatement? = null
)

@Serializable
data class CompanyIncomeStatement(
    val income_statement: List<IncomeStatement>,
    val period: String,
    val symbol: String,
    val type: String
)

@Serializable
data class IncomeStatement(
    val EBITDA: Long,
    val currency: String,
    val date: String,
    val day: Int,
    val earnings_per_share: Double,
    val effective_task_rate_percent: Double,
    val month: Int,
    val net_income: Long,
    val net_profit_margin: Double,
    val operating_expense: Long,
    val revenue: Long,
    val year: Int
)

@Serializable
data class CompanyCashFlowResponse(
    val request_id: String,
    val status: String,
    val data: CompanyCashFlow? = null
)

@Serializable
data class CompanyCashFlow(
    val cash_flow: List<CashFlow>,
    val period: String,
    val symbol: String,
    val type: String
)

@Serializable
data class CashFlow(
    val cash_from_financing: Long,
    val cash_from_investing: Long,
    val cash_from_operations: Long,
    val currency: String,
    val date: String,
    val day: Int,
    val free_cash_flow: Long,
    val month: Int,
    val net_change_in_cash: Long,
    val net_income: Long,
    val year: Int
)

@Serializable
data class CryptoNewsResponse(
    val data: List<CryptoNews>
)

@Serializable
data class CryptoNews(
    val url: String,
    val title: String,
    val description: String,
    val thumbnail: String,
    val createdAt: String
)

@Serializable
data class AdaCryptoInfo(
    val symbol: String,
    val price: Double,
    val volume_24h: Double,
    val percent_change: Double,
    val timestamp: String
)

@Serializable
data class StockCompanyOverviewResponse(
    val status: String? = null,
    val request_id: String? = null,
    val data: StockCompanyOverview? = null
)

@Serializable
data class StockCompanyOverview(
    val about: String? = null,
    val avg_volume: Int? = null,
    val change: Double? = null,
    val change_percent: Double? = null,
    val company_ceo: String? = null,
    val company_city: String? = null,
    val company_country: String? = null,
    val company_country_code: String? = null,
    val company_dividend_yield: Double? = null,
    val company_employees: Int? = null,
    val company_founded_date: String? = null,
    val company_market_cap: Long? = null,
    val company_pe_ratio: Double? = null,
    val company_state: String? = null,
    val company_street_address: String? = null,
    val company_website: String? = null,
    val country_code: String? = null,
    val currency: String? = null,
    val exchange: String? = null,
    val exchange_close: String? = null,
    val exchange_open: String? = null,
    val google_mid: String? = null,
    val high: Double? = null,
    val last_update_utc: String? = null,
    val low: Double? = null,
    val name: String? = null,
    val open: Double? = null,
    val pre_or_post_market: Double? = null,
    val pre_or_post_market_change: Int? = null,
    val pre_or_post_market_change_percent: Int? = null,
    val previous_close: Double? = null,
    val price: Double? = null,
    val primary_exchange: String? = null,
    val symbol: String? = null,
    val timezone: String? = null,
    val type: String? = null,
    val utc_offset_sec: Int? = null,
    val volume: Int? = null,
    val wikipedia_url: String? = null,
    val year_high: Double? = null,
    val year_low: Double? = null
)

@Serializable
data class ConvertResponse(
    val date: String? = null,
    val info: Info ? = null,
    val query: Query ? = null,
    val result: Double? = null,
    val success: Boolean? = null
)

@Serializable
data class Info(
    val rate: Double,
    val timestamp: Int
)

@Serializable
data class Query(
    val amount: Int,
    val from: String,
    val to: String
)