[client](../../index.md) / [com.g0dkar.sample.client.model](../index.md) / [GuestbookMessageRequest](./index.md)

# GuestbookMessageRequest

`data class GuestbookMessageRequest`

Used to create Guestbook message.

### Parameters

`message` - The Guestbook message

`visitorType` - The type of Visitor (default = [VisitorType.HUMAN](../-visitor-type/-h-u-m-a-n.md))

`parent` - ID of the Parent message (if part of a thread)

### Constructors

| Name | Summary |
|---|---|
| [&lt;init&gt;](-init-.md) | Used to create Guestbook message.`GuestbookMessageRequest(message: String, visitorType: `[`VisitorType`](../-visitor-type/index.md)` = VisitorType.HUMAN, parent: UUID?)` |

### Properties

| Name | Summary |
|---|---|
| [message](message.md) | The Guestbook message`val message: String` |
| [parent](parent.md) | ID of the Parent message (if part of a thread)`val parent: UUID?` |
| [visitorType](visitor-type.md) | The type of Visitor (default = [VisitorType.HUMAN](../-visitor-type/-h-u-m-a-n.md))`val visitorType: `[`VisitorType`](../-visitor-type/index.md) |
