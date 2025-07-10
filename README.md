# MCP Crypto Server
[![Kotlin](https://img.shields.io/badge/Kotlin-blueviolet?logo=kotlin)](https://kotlinlang.org)
[![Maven Central](https://img.shields.io/maven-central/v/io.modelcontextprotocol/kotlin-sdk.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:io.modelcontextprotocol%20a:kotlin-sdk)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

It is an MCP server written in Kotlin that provides information, analysis, news, and more related to cryptocurrency and financial using the Model Context Protocol (MCP). This project was developed to provide cryptocurrency news and coins prices to LLM surfaces in a standardized manner using the MCP protocol.



https://github.com/user-attachments/assets/7ada32ec-c918-4afb-90af-6c2ffc9d5aad



## Features
- MCP protocol-compliant server
- Presentation of crypto-related information
- Presentation economic events, stock overview, income statement, cash flow and etc. about financial -related information 
- Communication via Stdio
- Easy expandability

## Installation

Add the new repository to your build file:

```kotlin
repositories {
    mavenCentral()
}
```

## Quick Start

### Creating a Client

```kotlin
import io.modelcontextprotocol.kotlin.sdk.client.Client
import io.modelcontextprotocol.kotlin.sdk.client.StdioClientTransport
import io.modelcontextprotocol.kotlin.sdk.Implementation

val client = Client(
    clientInfo = Implementation(
        name = "example-client",
        version = "1.0.0"
    )
)

val transport = StdioClientTransport(
    inputStream = processInputStream,
    outputStream = processOutputStream
)

// Connect to server
client.connect(transport)

// List available resources
val resources = client.listResources()

// Read a specific resource
val resourceContent = client.readResource(
    ReadResourceRequest(uri = "file:///example.txt")
)
```

### Creating a Server

```kotlin
import io.modelcontextprotocol.kotlin.sdk.server.Server
import io.modelcontextprotocol.kotlin.sdk.server.ServerOptions
import io.modelcontextprotocol.kotlin.sdk.server.StdioServerTransport
import io.modelcontextprotocol.kotlin.sdk.ServerCapabilities

val server = Server(
    serverInfo = Implementation(
        name = "example-server",
        version = "1.0.0"
    ),
    options = ServerOptions(
        capabilities = ServerCapabilities(
            resources = ServerCapabilities.Resources(
                subscribe = true,
                listChanged = true
            )
        )
    )
)

// Add a resource
server.addTool(
        name = "example-tool",
        description = "Example Description",
        inputSchema = Tool.Input(
            properties = buildJsonObject { },
            required = emptyList()
        )
    ) { _ ->
        val newsList = runBlocking { httpClient.getExample(apiKey) }
        CallToolResult(
            content = exampleList.map {
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

// Start server with stdio transport
val transport = StdioServerTransport()
server.connect(transport)
```

## Usages

You need an API key to run the project. Get an API key from [RapidAPI](https://rapidapi.com/) and replace `YOUR_API_KEY` in the `McpCryptoServer.kt` file with your own key.

Run Project:

![Ekran Resmi 2025-07-06 14 20 45](https://github.com/user-attachments/assets/01a7522c-b35c-4474-8b20-81d081496013)

Click installDist ->

![Ekran Resmi 2025-07-06 14 21 38](https://github.com/user-attachments/assets/37f69dc8-cb30-4312-9800-d0848975d0a9)

 After Create build file left side -> You can integrate your mcp server into any ai tool by right-clicking on the relevant server and copying the path.

 Usage your Path any AI Tool MCP inside and add this code ->

 ```kotlin
{
    "mcpServers": {
        "mcp-crypto-service-server": {
            "command": "Your MCP Server Path",
            "args": []
        }
    }
}
```

<img width="665" alt="Ekran Resmi 2025-07-06 14 26 25" src="https://github.com/user-attachments/assets/390fce5f-6545-4867-8484-3887cfbb0e22" />

## 🚀 Contributing

Contributions are welcome! 🎉 Feel free to fork the repository and submit a Pull Request with improvements.

### **Steps to Contribute:**

1. Fork the repo 🍴
2. Create a new branch 🚀 (`git checkout -b feature-name`)
3. Commit changes 🎯 (`git commit -m 'Add feature XYZ'`)
4. Push to the branch 📤 (`git push origin feature-name`)
5. Open a Pull Request 🔥

---

## 📝 License

This project is open-source and available under the [MIT License](LICENSE).

---

💡 **Looking for feedback & collaboration!** If you're passionate about Android architecture, feel free to connect. 🚀
