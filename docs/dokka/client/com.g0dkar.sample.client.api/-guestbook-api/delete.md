[client](../../index.md) / [com.g0dkar.sample.client.api](../index.md) / [GuestbookApi](index.md) / [delete](./delete.md)

# delete

`@Retry("guestbookApi_R4J") @Bulkhead("guestbookApi_R4J") @CircuitBreaker("guestbookApi_R4J") suspend fun delete(id: UUID): Unit`

Delets a [GuestbookMessage](../../com.g0dkar.sample.client.model/-guestbook-message/index.md) given its ID.

`@Retry("guestbookApi_R4J") @Bulkhead("guestbookApi_R4J") @CircuitBreaker("guestbookApi_R4J") suspend fun delete(guestbookMessage: `[`GuestbookMessage`](../../com.g0dkar.sample.client.model/-guestbook-message/index.md)`): Unit`

Deletes a [GuestbookMessage](../../com.g0dkar.sample.client.model/-guestbook-message/index.md).

