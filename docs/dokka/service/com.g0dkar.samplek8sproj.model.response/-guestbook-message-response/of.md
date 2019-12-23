[service](../../index.md) / [com.g0dkar.samplek8sproj.model.response](../index.md) / [GuestbookMessageResponse](index.md) / [of](./of.md)

# of

`suspend fun of(guestbookMessage: `[`GuestbookMessage`](../../com.g0dkar.samplek8sproj.model/-guestbook-message/index.md)`, children: Flow<`[`GuestbookMessageResponse`](index.md)`> = emptyFlow()): `[`GuestbookMessageResponse`](index.md)

Converts a [GuestbookMessage](../../com.g0dkar.samplek8sproj.model/-guestbook-message/index.md) into a [GuestbookMessageResponse](index.md). The [children](of.md#com.g0dkar.samplek8sproj.model.response.GuestbookMessageResponse.Companion$of(com.g0dkar.samplek8sproj.model.GuestbookMessage, kotlinx.coroutines.flow.Flow((com.g0dkar.samplek8sproj.model.response.GuestbookMessageResponse)))/children) argument will be used for the
response. It should be filled by the caller.

