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
| [findById](find-by-id.md) | `fun findById(id: UUID): `[`GuestbookMessage`](../../com.g0dkar.samplek8sproj.model/-guestbook-message/index.md)`?` |
| [findByParent](find-by-parent.md) | `fun findByParent(id: UUID): List<`[`GuestbookMessage`](../../com.g0dkar.samplek8sproj.model/-guestbook-message/index.md)`>` |
| [getMessages](get-messages.md) | `fun getMessages(offset: Int = 0, max: Int = 50): List<`[`GuestbookMessage`](../../com.g0dkar.samplek8sproj.model/-guestbook-message/index.md)`>` |
| [save](save.md) | `fun save(message: `[`GuestbookMessage`](../../com.g0dkar.samplek8sproj.model/-guestbook-message/index.md)`): Boolean` |
| [setActive](set-active.md) | `fun setActive(id: UUID, active: Boolean): Boolean` |
