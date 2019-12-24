[client](../../index.md) / [com.g0dkar.sample.client.api](../index.md) / [GuestbookApi](index.md) / [get](./get.md)

# get

`@Retry("guestbookApi_R4J") @Bulkhead("guestbookApi_R4J") @CircuitBreaker("guestbookApi_R4J") suspend fun get(id: UUID): `[`GuestbookMessage`](../../com.g0dkar.sample.client.model/-guestbook-message/index.md)`?`

Returns a single [GuestbookMessage](../../com.g0dkar.sample.client.model/-guestbook-message/index.md) given its ID. If not found, `null` is returned.

