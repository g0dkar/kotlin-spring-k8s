[client](../../index.md) / [com.g0dkar.samplek8sproj.model.response](../index.md) / [GuestbookMessageResponse](./index.md)

# GuestbookMessageResponse

`data class GuestbookMessageResponse`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `GuestbookMessageResponse(id: UUID, active: Boolean, created: OffsetDateTime, message: String, visitorType: <ERROR CLASS>, children: List<`[`GuestbookMessageResponse`](./index.md)`>?)` |

### Properties

| Name | Summary |
|---|---|
| [active](active.md) | `val active: Boolean` |
| [children](children.md) | `val children: List<`[`GuestbookMessageResponse`](./index.md)`>?` |
| [created](created.md) | `val created: OffsetDateTime` |
| [id](id.md) | `val id: UUID` |
| [message](message.md) | `val message: String` |
| [visitorType](visitor-type.md) | `val visitorType: <ERROR CLASS>` |

### Companion Object Functions

| Name | Summary |
|---|---|
| [of](of.md) | `suspend fun of(guestbookMessage: <ERROR CLASS>, children: <ERROR CLASS><`[`GuestbookMessageResponse`](./index.md)`> = emptyFlow()): `[`GuestbookMessageResponse`](./index.md) |
