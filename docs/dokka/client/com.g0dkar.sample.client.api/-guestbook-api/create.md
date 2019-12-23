[client](../../index.md) / [com.g0dkar.sample.client.api](../index.md) / [GuestbookApi](index.md) / [create](./create.md)

# create

`@Retry("guestbookApi_R4J") @Bulkhead("guestbookApi_R4J") @CircuitBreaker("guestbookApi_R4J") suspend fun create(message: `[`GuestbookMessageRequest`](../../com.g0dkar.sample.client.model/-guestbook-message-request/index.md)`): `[`GuestbookMessage`](../../com.g0dkar.sample.client.model/-guestbook-message/index.md)

Creates a new [GuestbookMessage](../../com.g0dkar.sample.client.model/-guestbook-message/index.md).

