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