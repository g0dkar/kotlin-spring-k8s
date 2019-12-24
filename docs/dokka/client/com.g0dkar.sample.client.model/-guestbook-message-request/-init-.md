[client](../../index.md) / [com.g0dkar.sample.client.model](../index.md) / [GuestbookMessageRequest](index.md) / [&lt;init&gt;](./-init-.md)

# &lt;init&gt;

`GuestbookMessageRequest(@NotBlank @Size(1, 1024) message: String, @NotNull visitorType: `[`VisitorType`](../-visitor-type/index.md)` = VisitorType.HUMAN, parent: UUID?)`

Used to create Guestbook message.

### Parameters

`message` - The Guestbook message

`visitorType` - The type of Visitor (default = [VisitorType.HUMAN](../-visitor-type/-h-u-m-a-n.md))

`parent` - ID of the Parent message (if part of a thread)