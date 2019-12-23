[service](../../index.md) / [com.g0dkar.samplek8sproj.persistence](../index.md) / [GuestbookRepository](./index.md)

# GuestbookRepository

`@Repository class GuestbookRepository`

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | `GuestbookRepository(jooq: DSLContext)` |

### Functions

| Name | Summary |
|---|---|
| [findById](find-by-id.md) | `suspend fun findById(id: UUID): `[`GuestbookMessage`](../../com.g0dkar.samplek8sproj.model/-guestbook-message/index.md)`?` |
| [findByParent](find-by-parent.md) | `suspend fun findByParent(id: UUID): Flow<`[`GuestbookMessage`](../../com.g0dkar.samplek8sproj.model/-guestbook-message/index.md)`>` |
| [getMessages](get-messages.md) | `suspend fun getMessages(offset: Int = 0, max: Int = 50): Flow<`[`GuestbookMessage`](../../com.g0dkar.samplek8sproj.model/-guestbook-message/index.md)`>` |
| [save](save.md) | `suspend fun save(message: `[`GuestbookMessage`](../../com.g0dkar.samplek8sproj.model/-guestbook-message/index.md)`): Boolean` |
| [setActive](set-active.md) | `suspend fun setActive(id: UUID, active: Boolean): Boolean` |
