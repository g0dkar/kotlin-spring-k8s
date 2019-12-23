[client](../../index.md) / [com.g0dkar.sample.client.api](../index.md) / [GuestbookApi](index.md) / [getAll](./get-all.md)

# getAll

`@Retry("guestbookApi_R4J") @Bulkhead("guestbookApi_R4J") @CircuitBreaker("guestbookApi_R4J") suspend fun getAll(offset: Int = 0, @Min(1) @Max(100) max: Int = 50): Flow<`[`GuestbookMessage`](../../com.g0dkar.sample.client.model/-guestbook-message/index.md)`>`

Returns a list of messages from the API. Accepts an offset and max parameters.

