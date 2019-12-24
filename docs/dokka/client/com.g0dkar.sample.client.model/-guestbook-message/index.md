[client](../../index.md) / [com.g0dkar.sample.client.model](../index.md) / [GuestbookMessage](./index.md)

# GuestbookMessage

`data class GuestbookMessage`

A Guestbook Message. If this message has a thread, its [children](children.md) will be populated by the responses.

### Parameters

`id` - Guestbook message ID

`created` - When was this message created

`message` - The message content

`visitorType` - The type of visitor that left the message

`children` - The responses to this message

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | A Guestbook Message. If this message has a thread, its [children](children.md) will be populated by the responses.`GuestbookMessage(id: UUID, created: OffsetDateTime, message: String, visitorType: `[`VisitorType`](../-visitor-type/index.md)`, children: List<`[`GuestbookMessage`](./index.md)`>?)` |

### Properties

| Name | Summary |
|---|---|
| [children](children.md) | The responses to this message`val children: List<`[`GuestbookMessage`](./index.md)`>?` |
| [created](created.md) | When was this message created`val created: OffsetDateTime` |
| [id](id.md) | Guestbook message ID`val id: UUID` |
| [message](message.md) | The message content`val message: String` |
| [visitorType](visitor-type.md) | The type of visitor that left the message`val visitorType: `[`VisitorType`](../-visitor-type/index.md) |
