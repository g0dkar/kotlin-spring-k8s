[client](../../index.md) / [com.g0dkar.sample.client.api](../index.md) / [GuestbookApi](./index.md)

# GuestbookApi

`@Component @Validated class GuestbookApi`

Implements an API to interact with the Guestbook Service.

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Implements an API to interact with the Guestbook Service.`GuestbookApi(retrofit: Retrofit, guestbookApiConfig: `[`GuestbookApiConfig`](../-guestbook-api-config/index.md)`)` |

### Functions

| Name | Summary |
|---|---|
| [create](create.md) | Creates a new [GuestbookMessage](../../com.g0dkar.sample.client.model/-guestbook-message/index.md).`suspend fun create(message: `[`GuestbookMessageRequest`](../../com.g0dkar.sample.client.model/-guestbook-message-request/index.md)`): `[`GuestbookMessage`](../../com.g0dkar.sample.client.model/-guestbook-message/index.md) |
| [delete](delete.md) | Delets a [GuestbookMessage](../../com.g0dkar.sample.client.model/-guestbook-message/index.md) given its ID.`suspend fun delete(id: UUID): Unit`<br>Deletes a [GuestbookMessage](../../com.g0dkar.sample.client.model/-guestbook-message/index.md).`suspend fun delete(guestbookMessage: `[`GuestbookMessage`](../../com.g0dkar.sample.client.model/-guestbook-message/index.md)`): Unit` |
| [get](get.md) | Returns a single [GuestbookMessage](../../com.g0dkar.sample.client.model/-guestbook-message/index.md) given its ID. If not found, `null` is returned.`suspend fun get(id: UUID): `[`GuestbookMessage`](../../com.g0dkar.sample.client.model/-guestbook-message/index.md)`?` |
| [getAll](get-all.md) | Returns a list of messages from the API. Accepts an offset and max parameters.`suspend fun getAll(offset: Int = 0, max: Int = 50): Flow<`[`GuestbookMessage`](../../com.g0dkar.sample.client.model/-guestbook-message/index.md)`>` |
