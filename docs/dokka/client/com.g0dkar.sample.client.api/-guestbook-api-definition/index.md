[client](../../index.md) / [com.g0dkar.sample.client.api](../index.md) / [GuestbookApiDefinition](./index.md)

# GuestbookApiDefinition

`interface GuestbookApiDefinition`

Describes the API provided by the service

### Functions

| Name | Summary |
|---|---|
| [create](create.md) | `abstract suspend fun create(message: `[`GuestbookMessageRequest`](../../com.g0dkar.sample.client.model/-guestbook-message-request/index.md)`): `[`GuestbookMessage`](../../com.g0dkar.sample.client.model/-guestbook-message/index.md) |
| [delete](delete.md) | `abstract suspend fun delete(id: UUID): Any` |
| [get](get.md) | `abstract suspend fun get(id: UUID): `[`GuestbookMessage`](../../com.g0dkar.sample.client.model/-guestbook-message/index.md) |
| [getAll](get-all.md) | `abstract suspend fun getAll(offset: Int = 0, max: Int = 50): List<`[`GuestbookMessage`](../../com.g0dkar.sample.client.model/-guestbook-message/index.md)`>` |
