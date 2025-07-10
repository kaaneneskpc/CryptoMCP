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

    server.addTool(
        name = "get_cash_flow",
        description = "Get cash flow overview by symbol (e.g., AAPL:NASDAQ)",
        inputSchema = Tool.Input(
            properties = buildJsonObject {
                put("symbol", buildJsonObject {
                    put("type", JsonPrimitive("string"))
                    put("description", JsonPrimitive("Stock symbol, e.g., AAPL:NASDAQ"))
                })
                put("period", buildJsonObject {
                    put("type", JsonPrimitive("string"))
                    put("description", JsonPrimitive("Period for cash flow data, e.g., QUARTERLY or ANNUAL"))
                })
                put("language", buildJsonObject {
                    put("type", JsonPrimitive("string"))
                    put("description", JsonPrimitive("Language code for the response, e.g., en"))
                })
            },
            required = listOf("symbol", "period")
        )
    ) { input ->
        val jsonInput = try {
            Json.parseToJsonElement(input as String).jsonObject
        } catch (e: Exception) {
            null
        }
        val symbol = jsonInput?.get("symbol")?.jsonPrimitive?.content ?: "AAPL:NASDAQ"
        val period = jsonInput?.get("period")?.jsonPrimitive?.content ?: "QUARTERLY"
        val language = jsonInput?.get("language")?.jsonPrimitive?.content ?: "en"
        val cashFlowResponse = runBlocking { httpClient.getCashFlow(apiKey, symbol, period, language) }
        val companyCashFlow = cashFlowResponse.data
        CallToolResult(
            content = if (companyCashFlow != null && companyCashFlow.cash_flow.isNotEmpty()) {
                companyCashFlow.cash_flow.map { cf ->
                    TextContent(
                        """
                        Symbol: ${companyCashFlow.symbol}
                        Period: ${companyCashFlow.period}
                        Type: ${companyCashFlow.type}
                        Date: ${cf.date} (Year: ${cf.year}, Month: ${cf.month}, Day: ${cf.day})
                        Currency: ${cf.currency}
                        Cash from Operations: ${cf.cash_from_operations}
                        Cash from Investing: ${cf.cash_from_investing}
                        Cash from Financing: ${cf.cash_from_financing}
                        Net Income: ${cf.net_income}
                        Free Cash Flow: ${cf.free_cash_flow}
                        Net Change in Cash: ${cf.net_change_in_cash}
                        """.trimIndent()
                    )
                }
            } else {
                listOf(TextContent("No cash flow data returned from API."))
            }
        )
    }

    server.addTool(
        name = "get_income_statement",
        description = "Get income statement overview by symbol (e.g., AAPL:NASDAQ)",
        inputSchema = Tool.Input(
            properties = buildJsonObject {
                put("symbol", buildJsonObject {
                    put("type", JsonPrimitive("string"))
                    put("description", JsonPrimitive("Stock symbol, e.g., AAPL:NASDAQ"))
                })
                put("period", buildJsonObject {
                    put("type", JsonPrimitive("string"))
                    put("description", JsonPrimitive("Period for income statement data, e.g., QUARTERLY or ANNUAL"))
                })
                put("language", buildJsonObject {
                    put("type", JsonPrimitive("string"))
                    put("description", JsonPrimitive("Language code for the response, e.g., en"))
                })
            },
            required = listOf("symbol", "period")
        )
    ) { input ->
        val jsonInput = try {
            Json.parseToJsonElement(input as String).jsonObject
        } catch (e: Exception) {
            null
        }
        val symbol = jsonInput?.get("symbol")?.jsonPrimitive?.content ?: "AAPL:NASDAQ"
        val period = jsonInput?.get("period")?.jsonPrimitive?.content ?: "QUARTERLY"
        val language = jsonInput?.get("language")?.jsonPrimitive?.content ?: "en"
        val incomeStatementResponse = runBlocking { httpClient.getIncomeStatement(apiKey, symbol, period, language) }
        val incomeStatementList = incomeStatementResponse.data
        CallToolResult(
            content = if (incomeStatementList != null && incomeStatementList.income_statement.isNotEmpty()) {
                incomeStatementList.income_statement.map { isItem ->
                    TextContent(
                        """
                        Symbol: ${incomeStatementList.symbol}
                        Period: ${incomeStatementList.period}
                        Type: ${incomeStatementList.type}
                        Date: ${isItem.date} (Year: ${isItem.year}, Month: ${isItem.month}, Day: ${isItem.day})
                        Currency: ${isItem.currency}
                        Revenue: ${isItem.revenue}
                        Net Income: ${isItem.net_income}
                        EBITDA: ${isItem.EBITDA}
                        Earnings Per Share: ${isItem.earnings_per_share}
                        Net Profit Margin: ${isItem.net_profit_margin}
                        Operating Expense: ${isItem.operating_expense}
                        Effective Tax Rate (%): ${isItem.effective_task_rate_percent}
                        """.trimIndent()
                    )
                }
            } else {
                listOf(TextContent("No income statement data returned from API."))
            }
        )
    }
    server.addTool(
        name = "convert_currency",
        description = "Convert currency from one to another",
        inputSchema = Tool.Input(
            properties = buildJsonObject {
                put("from", buildJsonObject {
                    put("type", JsonPrimitive("string"))
                    put("description", JsonPrimitive("Source currency code, e.g., USD"))
                })
                put("to", buildJsonObject {
                    put("type", JsonPrimitive("string"))
                    put("description", JsonPrimitive("Target currency code, e.g., EUR"))
                })
                put("amount", buildJsonObject {
                    put("type", JsonPrimitive("integer"))
                    put("description", JsonPrimitive("Amount to convert"))
                })
            },
            required = listOf("from", "to", "amount")
        )
    ) { input ->
        val jsonInput = try {
            Json.parseToJsonElement(input as String).jsonObject
        } catch (e: Exception) {
            null
        }
        val from = jsonInput?.get("from")?.jsonPrimitive?.content ?: "USD"
        val to = jsonInput?.get("to")?.jsonPrimitive?.content ?: "EUR"
        val amount = jsonInput?.get("amount")?.jsonPrimitive?.int ?: 1

        val result = runBlocking { httpClient.convertCurrency(apiKey, from, to, amount) }
        CallToolResult(
            content = listOf(
                TextContent(
                    """
                From: $from
                To: $to
                Amount: $amount
                Result: ${result.result}
                Rate: ${result.info?.rate}
                Date: ${result.date}
                Success: ${result.success}
                """.trimIndent()
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