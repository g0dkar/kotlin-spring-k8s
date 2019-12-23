[service](../../index.md) / [com.g0dkar.samplek8sproj.service](../index.md) / [GuestbookService](./index.md)

# GuestbookService

`@Service class GuestbookService`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `GuestbookService(guestbookRepository: `[`GuestbookRepository`](../../com.g0dkar.samplek8sproj.persistence/-guestbook-repository/index.md)`)` |

### Functions

| Name | Summary |
|---|---|
| [create](create.md) | `suspend fun create(request: `[`GuestbookMessageRequest`](../../com.g0dkar.samplek8sproj.model.request/-guestbook-message-request/index.md)`): `[`GuestbookMessageResponse`](../../com.g0dkar.samplek8sproj.model.response/-guestbook-message-response/index.md) |
| [delete](delete.md) | `suspend fun delete(id: UUID): Boolean` |
| [findById](find-by-id.md) | `suspend fun findById(id: UUID): `[`GuestbookMessageResponse`](../../com.g0dkar.samplek8sproj.model.response/-guestbook-message-response/index.md)`?` |
| [getMessages](get-messages.md) | `suspend fun getMessages(offset: Int = 0, max: Int = 50): Flow<`[`GuestbookMessageResponse`](../../com.g0dkar.samplek8sproj.model.response/-guestbook-message-response/index.md)`>` |
