# mcp-cyrpto-service-server

Kotlin ile yazılmış, Model Context Protocol (MCP) kullanarak kripto para haberlerini araç olarak sunan bir MCP sunucusudur. Bu proje, MCP protokolü ile LLM yüzeylerine kripto haberlerini standart bir şekilde sunmak için geliştirilmiştir.

## Özellikler
- MCP protokolü ile uyumlu sunucu
- Kripto para haberlerini iki farklı kaynaktan (genel ve The Guardian) araç olarak sunar
- Stdio üzerinden iletişim
- Kolay genişletilebilirlik

## Kurulum

### Gereksinimler
- JDK 17+
- [Gradle](https://gradle.org/) (veya proje ile gelen wrapper'ı kullanabilirsiniz)

### Bağımlılıklar
- [io.modelcontextprotocol:kotlin-sdk:0.5.0](https://github.com/modelcontextprotocol/kotlin-sdk)
- org.slf4j:slf4j-nop:2.0.9
- io.ktor:ktor-client-content-negotiation:3.1.1
- io.ktor:ktor-serialization-kotlinx-json:3.1.1

### Derleme

```bash
git clone <repo-url>
cd mcp-cyrpto-service-server
./gradlew build
```

## Kullanım

Projeyi çalıştırmak için bir API anahtarına ihtiyacınız vardır. [RapidAPI](https://rapidapi.com/) üzerinden bir API anahtarı alıp, `McpCryptoServer.kt` dosyasındaki `YOUR_API_KEY` kısmını kendi anahtarınız ile değiştirin.

Projeyi başlatmak için:

```bash
./gradlew run
```

### MCP Sunucusu Özellikleri
- **get_daily_crypto_news**: Günlük kripto para haberlerini getirir.
- **get_daily_crypto_news_from_the_guardian**: The Guardian kaynağından günlük kripto para haberlerini getirir.

### Temel Kod Örneği

```kotlin
fun main() = `run mcp server`()
```

Sunucu, stdio üzerinden MCP istemcileriyle iletişim kurar ve yukarıdaki araçları sunar.

## Katkı
Katkıda bulunmak için lütfen bir issue açın veya pull request gönderin.

## Lisans
Bu proje MIT lisansı ile lisanslanmıştır. 