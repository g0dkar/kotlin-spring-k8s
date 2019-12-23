[service](../../index.md) / [com.g0dkar.samplek8sproj.api](../index.md) / [GuestbookApi](./index.md)

# GuestbookApi

`@RestController @RequestMapping(["/guestbook"]) class GuestbookApi`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `GuestbookApi(guestbookService: `[`GuestbookService`](../../com.g0dkar.samplek8sproj.service/-guestbook-service/index.md)`)` |

### Functions

| Name | Summary |
|---|---|
| [create](create.md) | `suspend fun create(message: `[`GuestbookMessageRequest`](../../com.g0dkar.samplek8sproj.model.request/-guestbook-message-request/index.md)`): ResponseEntity<`[`GuestbookMessageResponse`](../../com.g0dkar.samplek8sproj.model.response/-guestbook-message-response/index.md)`>` |
| [delete](delete.md) | `suspend fun delete(id: UUID): ResponseEntity<Any>` |
| [get](get.md) | `suspend fun get(id: UUID): ResponseEntity<`[`GuestbookMessageResponse`](../../com.g0dkar.samplek8sproj.model.response/-guestbook-message-response/index.md)`>` |
| [getAll](get-all.md) | `suspend fun getAll(offset: Int, max: Int): Flow<`[`GuestbookMessageResponse`](../../com.g0dkar.samplek8sproj.model.response/-guestbook-message-response/index.md)`>` |
