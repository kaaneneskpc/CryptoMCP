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
            json()
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