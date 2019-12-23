[client](../../index.md) / [com.g0dkar.sample.client.api](../index.md) / [GuestbookApiDefinition](./index.md)

# GuestbookApiDefinition

`interface GuestbookApiDefinition`

Describes the API provided by the service

### Functions

| Name | Summary |
|---|---|
| [create](create.md) | `abstract fun create(message: `[`GuestbookMessageRequest`](../../com.g0dkar.sample.client.model/-guestbook-message-request/index.md)`): Call<`[`GuestbookMessage`](../../com.g0dkar.sample.client.model/-guestbook-message/index.md)`>` |
| [delete](delete.md) | `abstract fun delete(id: UUID): Call<Any>` |
| [get](get.md) | `abstract fun get(id: UUID): Call<`[`GuestbookMessage`](../../com.g0dkar.sample.client.model/-guestbook-message/index.md)`>` |
| [getAll](get-all.md) | `abstract fun getAll(offset: Int = 0, max: Int = 50): Call<List<`[`GuestbookMessage`](../../com.g0dkar.sample.client.model/-guestbook-message/index.md)`>>` |
