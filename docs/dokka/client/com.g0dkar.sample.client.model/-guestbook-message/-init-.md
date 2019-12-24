[client](../../index.md) / [com.g0dkar.sample.client.model](../index.md) / [GuestbookMessage](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

`GuestbookMessage(id: UUID, created: OffsetDateTime, message: String, visitorType: `[`VisitorType`](../-visitor-type/index.md)`, children: List<`[`GuestbookMessage`](index.md)`>?)`

A Guestbook Message. If this message has a thread, its [children](children.md) will be populated by the responses.

### Parameters

`id` - Guestbook message ID

`created` - When was this message created

`message` - The message content

`visitorType` - The type of visitor that left the message

`children` - The responses to this message