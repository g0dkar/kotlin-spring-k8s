[client](../../index.md) / [com.g0dkar.sample.client.api](../index.md) / [GuestbookApiConfig](./index.md)

# GuestbookApiConfig

`data class GuestbookApiConfig`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `GuestbookApiConfig(baseUrl: String = "http://localhost:8080", connectTimeout: Duration = Duration.of(1, ChronoUnit.SECONDS), writeTimeout: Duration = Duration.of(1, ChronoUnit.SECONDS), readTimeout: Duration = Duration.of(1, ChronoUnit.SECONDS), callTimeout: Duration = Duration.of(5, ChronoUnit.SECONDS), objectMapper: ObjectMapper = ObjectMapper(), logger: Logger = LogManager.getLogger(GuestbookApi::class.java))` |

### Properties

| Name | Summary |
|---|---|
| [baseUrl](base-url.md) | `val baseUrl: String` |
| [callTimeout](call-timeout.md) | `val callTimeout: Duration` |
| [connectTimeout](connect-timeout.md) | `val connectTimeout: Duration` |
| [logger](logger.md) | `val logger: Logger` |
| [objectMapper](object-mapper.md) | `val objectMapper: ObjectMapper` |
| [readTimeout](read-timeout.md) | `val readTimeout: Duration` |
| [writeTimeout](write-timeout.md) | `val writeTimeout: Duration` |
