### Documentation for WebSocket Handlers

These classes implement WebSocket handlers for managing real-time communication for `News` and `User` entities in a Spring WebFlux application. They use reactive streams to handle WebSocket connections, sending updates to connected clients when news or user-related data changes. Each handler handles specific types of data, such as news updates or user information, and broadcasts the data to connected WebSocket clients.

---

### 1. **NewsWebSocketHandler**

#### Description:
This class handles WebSocket connections for broadcasting updates related to `News` entities. It listens for incoming messages and sends serialized news updates to connected clients. The class is designed to broadcast updates to users based on their friendships.

#### Dependencies:
- `UserRepo`: Repository for user data.
- `JwtUtils`: Utility for handling JWT-based authentication.
- `ObjectMapper`: JSON serializer/deserializer.
- `Sinks.Many<String>`: A sink used for multicasting messages.

#### Methods:

- **`public void publishNews(News news, String type)`**:
    - **Description**: Publishes a `News` update to all connected clients. It serializes the `News` entity into a `Message` object, which includes the list of receivers (friends of the news author).
    - **Parameters**:
        - `news`: The `News` entity to be broadcasted.
        - `type`: The type of the message (e.g., "UPDATE", "DELETE").
    - **Exception Handling**: If serialization fails, an error is logged.

- **`Mono<Void> handle(WebSocketSession session)`**:
    - **Description**: Handles the WebSocket session. It sends updates (from the `newsSink`) and handles messages received from the client.
    - **Steps**:
        - The `newsSink` is a `Flux` that emits serialized messages to the WebSocket client.
        - Messages received from the client are logged but are not processed further in this handler.
    - **Returns**: A `Mono<Void>` that represents the completion of the WebSocket session's operations.

#### Key Concepts:
- **Sinks.Many**: Used for broadcasting messages to all WebSocket clients. It supports backpressure, allowing it to buffer messages if clients can't keep up with the stream.
- **ObjectMapper**: Converts `News` objects and `Message` objects into JSON for WebSocket communication.

---

### 2. **UserWebSocketHandler**

#### Description:
This class handles WebSocket connections for broadcasting updates related to `Users`. Similar to the `NewsWebSocketHandler`, it listens for incoming messages and sends serialized user data to connected clients.

#### Dependencies:
- `UserRepo`: Repository for user data.
- `JwtUtils`: Utility for handling JWT-based authentication.
- `ObjectMapper`: JSON serializer/deserializer.
- `Sinks.Many<String>`: A sink used for multicasting messages.

#### Methods:

- **`public void publishUser(Users user, String type)`**:
    - **Description**: Publishes a `User` update to all connected clients. It serializes the `Users` entity into a `Message` object.
    - **Parameters**:
        - `user`: The `Users` entity to be broadcasted.
        - `type`: The type of the message (e.g., "UPDATE", "DELETE").
    - **Exception Handling**: If serialization fails, an error is logged.

- **`Mono<Void> handle(WebSocketSession session)`**:
    - **Description**: Handles the WebSocket session. It sends updates (from the `userSink`) and handles messages received from the client.
    - **Steps**:
        - The `userSink` is a `Flux` that emits serialized messages to the WebSocket client.
        - Messages received from the client are logged but are not processed further in this handler.
    - **Returns**: A `Mono<Void>` that represents the completion of the WebSocket session's operations.

#### Key Concepts:
- **Sinks.Many**: Used for broadcasting user-related messages to all WebSocket clients. It also supports backpressure.
- **ObjectMapper**: Converts `Users` objects and `Message` objects into JSON for WebSocket communication.

---

### Common Concepts and Notes

- **WebSocket Session**: Both handlers manage a WebSocket session using the `WebSocketSession` object. This session is used to send and receive WebSocket messages asynchronously.

- **Reactive Programming**: Both handlers utilize `Mono` and `Flux`, which are part of Project Reactor, a reactive library. `Mono` represents a single value (or empty), and `Flux` represents a stream of values. These types allow the WebSocket handlers to manage asynchronous message flows.

- **Multicast Sinks**: The `Sinks.Many` is used to multicast messages to multiple WebSocket clients. It supports backpressure, meaning that if a client can't keep up with the messages, the messages are buffered.

- **JSON Serialization**: `ObjectMapper` is used to convert the Java objects (`News`, `Users`, etc.) into JSON before sending them over the WebSocket. The `JavaTimeModule` is registered to handle Java 8 `Instant` and `LocalDateTime` objects correctly.

---

### Example Usage:

1. **Publishing News**:
    - When a `News` update occurs, `publishNews(news, type)` is called to serialize the news and send it to all connected clients.

2. **Publishing User Updates**:
    - Similarly, when a `User` update occurs, `publishUser(user, type)` is called to serialize the user data and broadcast it to clients.

---

These handlers enable real-time communication and broadcasting of updates for `News` and `User` entities in a WebFlux-based application.