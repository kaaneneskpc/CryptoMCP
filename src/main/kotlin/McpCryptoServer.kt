package org.example

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.streams.*
import io.modelcontextprotocol.kotlin.sdk.*
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.io.asSink
import kotlinx.io.buffered
import kotlinx.serialization.json.*

fun `run mcp server`() {
    val apiKey = "YOUR_API_KEY"

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    val server = Server(
        Implementation(
            name = "crypto_news",
            version = "1.0.0"
        ),
        ServerOptions(
            capabilities = ServerCapabilities(tools = ServerCapabilities.Tools(listChanged = true))
        )
    )

    server.addTool(
        name = "get_daily_crypto_news",
        description = "Get the latest daily crypto news articles.",
        inputSchema = Tool.Input(
            properties = buildJsonObject { },
            required = emptyList()
        )
    ) { _ ->
        val newsList = runBlocking { httpClient.getDailyCryptoNews(apiKey) }
        CallToolResult(
            content = newsList.map {
                TextContent("""
                    Title: ${it.title}
                    Description: ${it.description}
                    URL: ${it.url}
                    Thumbnail: ${it.thumbnail}
                    Created At: ${it.createdAt}
                """.trimIndent())
            }
        )
    }

    server.addTool(
        name = "get_daily_crypto_news_from_the_guardian",
        description = "Get the latest daily crypto news articles from the guardian",
        inputSchema = Tool.Input(
            properties = buildJsonObject { },
            required = emptyList()
        )
    ) { _ ->
        val newsList = runBlocking { httpClient.getDailyCryptoNewsFromTheGuardian(apiKey) }
        CallToolResult(
            content = newsList.map {
                TextContent("""
                    Title: ${it.title}
                    Description: ${it.description}
                    URL: ${it.url}
                    Thumbnail: ${it.thumbnail}
                    Created At: ${it.createdAt}
                """.trimIndent())
            }
        )
    }

    server.addTool(
        name = "get_ada_price",
        description = "Get the latest ADA coin price",
        inputSchema = Tool.Input(
            properties = buildJsonObject { },
            required = emptyList()
        )
    ) { _ ->
        val adaInfo = runBlocking { httpClient.getAdaPrice(apiKey) }
        CallToolResult(
            content = listOf(
                TextContent(
                    """
                    symbol: ${adaInfo.symbol}
                    price: ${adaInfo.price}
                    volume_24h: ${adaInfo.volume_24h}
                    percent_change: ${adaInfo.percent_change}
                    timestamp: ${adaInfo.timestamp}
                    """.trimIndent()
                )
            )
        )
    }

    server.addTool(
        name = "get_stock_overview",
        description = "Get stock company overview by symbol (e.g., AAPL:NASDAQ)",
        inputSchema = Tool.Input(
            properties = buildJsonObject {
                put("symbol", buildJsonObject {
                    put("type", JsonPrimitive("string"))
                    put("description", JsonPrimitive("Stock symbol, e.g., AAPL:NASDAQ"))
                })
                put("language", buildJsonObject {
                    put("type", JsonPrimitive("string"))
                    put("description", JsonPrimitive("Language code, e.g., en"))
                })
            },
            required = listOf("symbol")
        )
    ) { input ->
        println("DEBUG: input class = ${input?.javaClass}, value = $input")
        val jsonInput = try {
            Json.parseToJsonElement(input as String).jsonObject
        } catch (e: Exception) {
            null
        }
        val symbol = jsonInput?.get("symbol")?.jsonPrimitive?.content ?: "AAPL:NASDAQ"
        val language = jsonInput?.get("language")?.jsonPrimitive?.content ?: "en"
        val overviewResponse = runBlocking { httpClient.getStockOverview(apiKey, symbol, language) }
        val overview = overviewResponse.data
        CallToolResult(
            content = listOf(
                TextContent(
                    if (overview != null) {
                        """
                        Name: ${overview.name}
                        Symbol: ${overview.symbol}
                        Price: ${overview.price}
                        Change: ${overview.change} (${overview.change_percent}%)
                        Market Cap: ${overview.company_market_cap}
                        CEO: ${overview.company_ceo}
                        Website: ${overview.company_website}
                        Country: ${overview.company_country}
                        Exchange: ${overview.exchange}
                        Year High: ${overview.year_high}
                        Year Low: ${overview.year_low}
                        About: ${overview.about}
                        """.trimIndent()
                    } else {
                        "No data returned from API."
                    }
                )
            )
        )
    }

    val transport = StdioServerTransport(
        System.`in`.asInput(),
        System.out.asSink().buffered()
    )

    runBlocking {
        server.connect(transport)
        val done = Job()
        server.onClose {
            done.complete()
        }
        done.join()
    }
}