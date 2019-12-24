[service](../../index.md) / [com.g0dkar.samplek8sproj.service](../index.md) / [GuestbookService](./index.md)

# GuestbookService

`@Service @Validated class GuestbookService`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `GuestbookService(guestbookRepository: `[`GuestbookRepository`](../../com.g0dkar.samplek8sproj.persistence/-guestbook-repository/index.md)`)` |

### Functions

| Name | Summary |
|---|---|
| [create](create.md) | `fun create(request: `[`GuestbookMessageRequest`](../../com.g0dkar.samplek8sproj.model.request/-guestbook-message-request/index.md)`): `[`GuestbookMessageResponse`](../../com.g0dkar.samplek8sproj.model.response/-guestbook-message-response/index.md) |
| [delete](delete.md) | `fun delete(id: UUID): Boolean` |
| [findById](find-by-id.md) | `fun findById(id: UUID): `[`GuestbookMessageResponse`](../../com.g0dkar.samplek8sproj.model.response/-guestbook-message-response/index.md)`?` |
| [getMessages](get-messages.md) | `fun getMessages(offset: Int = 0, max: Int = 50): List<`[`GuestbookMessageResponse`](../../com.g0dkar.samplek8sproj.model.response/-guestbook-message-response/index.md)`>` |
