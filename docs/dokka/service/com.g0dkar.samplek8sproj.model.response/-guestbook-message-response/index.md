[service](../../index.md) / [com.g0dkar.samplek8sproj.model.response](../index.md) / [GuestbookMessageResponse](./index.md)

# GuestbookMessageResponse

`data class GuestbookMessageResponse`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `GuestbookMessageResponse(id: UUID, active: Boolean, created: OffsetDateTime, message: String, visitorType: `[`VisitorType`](../../com.g0dkar.samplek8sproj.model/-visitor-type/index.md)`, children: List<`[`GuestbookMessageResponse`](./index.md)`>?)` |

### Properties

| Name | Summary |
|---|---|
| [active](active.md) | `val active: Boolean` |
| [children](children.md) | `val children: List<`[`GuestbookMessageResponse`](./index.md)`>?` |
| [created](created.md) | `val created: OffsetDateTime` |
| [id](id.md) | `val id: UUID` |
| [message](message.md) | `val message: String` |
| [visitorType](visitor-type.md) | `val visitorType: `[`VisitorType`](../../com.g0dkar.samplek8sproj.model/-visitor-type/index.md) |

### Companion Object Functions

| Name | Summary |
|---|---|
| [of](of.md) | Converts a [GuestbookMessage](../../com.g0dkar.samplek8sproj.model/-guestbook-message/index.md) into a [GuestbookMessageResponse](./index.md). The [children](of.md#com.g0dkar.samplek8sproj.model.response.GuestbookMessageResponse.Companion$of(com.g0dkar.samplek8sproj.model.GuestbookMessage, kotlinx.coroutines.flow.Flow((com.g0dkar.samplek8sproj.model.response.GuestbookMessageResponse)))/children) argument will be used for the response. It should be filled by the caller.`suspend fun of(guestbookMessage: `[`GuestbookMessage`](../../com.g0dkar.samplek8sproj.model/-guestbook-message/index.md)`, children: Flow<`[`GuestbookMessageResponse`](./index.md)`> = emptyFlow()): `[`GuestbookMessageResponse`](./index.md) |
