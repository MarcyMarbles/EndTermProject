## **JacksonConfig**

### **Class Overview**
This class configures a customized `ObjectMapper` bean for the Spring application to support Java 8 Date and Time API serialization and deserialization.

### **Annotations**
- `@Configuration`: Indicates that this class declares one or more `@Bean` methods for Spring’s IoC container.

### **Methods**
- `objectMapper()`:
    - **Description**: Creates and configures an `ObjectMapper` with support for the `JavaTimeModule`.
    - **Return Type**: `ObjectMapper`
    - **Purpose**: Ensures proper handling of Java 8 time/date types in JSON serialization.

---

## **JwtAuthenticationWebFilter**

### **Class Overview**
This class implements a Spring WebFlux `WebFilter` for JWT-based authentication. It extracts and validates JWT tokens from requests.

### **Annotations**
- `@Slf4j`: Provides logging capability using Lombok.

### **Fields**
- `jwtUtils`: Utility service for JWT token operations like validation and extraction.

### **Constructor**
- **JwtAuthenticationWebFilter(JwtUtils jwtUtils)**:  
  Initializes the filter with `JwtUtils` dependency.

### **Methods**
- `filter(ServerWebExchange exchange, WebFilterChain chain)`
    - **Description**:
        - Extracts the JWT token from the request header or query parameters.
        - Validates the token and retrieves the username and roles.
        - Sets the `SecurityContext` for the authenticated user.
    - **Parameters**:
        - `ServerWebExchange exchange`: Represents the web request and response.
        - `WebFilterChain chain`: The chain of filters to execute.
    - **Return Type**: `Mono<Void>`.

---

## **MailConfig**

### **Class Overview**
This class configures the JavaMailSender for sending emails using an SMTP server.

### **Annotations**
- `@Component`: Marks the class as a Spring-managed bean.

### **Fields**
- `username`: Email username loaded from application properties.
- `password`: Email password loaded from application properties.

### **Methods**
- `getJavaMailSender()`:
    - **Description**:  
      Configures and returns a `JavaMailSender` bean with SMTP server settings.
    - **Return Type**: `JavaMailSender`.
    - **Configuration Details**:
        - Host: `smtp.gmail.com`
        - Port: `587`
        - Auth: `true`
        - TLS: Enabled

---

## **SecurityConfig**

### **Class Overview**
This class configures security settings for a Spring WebFlux application, including JWT filters, CORS policy, and password encoding.

### **Annotations**
- `@Configuration`: Declares the class as a Spring configuration class.
- `@EnableWebFluxSecurity`: Enables WebFlux security features.
- `@Slf4j`: Provides logging support.

### **Fields**
- `userDetailsService`: Custom implementation for user authentication.

### **Methods**
1. `corsConfigurationSource()`:
    - Configures CORS to allow specific origins, methods, and headers.
    - **Return Type**: `CorsConfigurationSource`.

2. `securityWebFilterChain(ServerHttpSecurity http, JwtUtils jwtUtils)`:
    - Configures security filters and rules.
    - Adds a custom `JwtAuthenticationWebFilter`.
    - Sets up role-based access control.
    - **Return Type**: `SecurityWebFilterChain`.

3. `passwordEncoder()`:
    - Provides a `BCryptPasswordEncoder` bean for secure password hashing.
    - **Return Type**: `PasswordEncoder`.

4. `reactiveAuthenticationManager(PasswordEncoder passwordEncoder)`:
    - Configures an authentication manager that uses `CustomUserDetailsService`.
    - **Return Type**: `ReactiveAuthenticationManager`.

---

## **WebSocketConfig**

### **Class Overview**
This class configures WebSocket endpoints and handlers for the application.

### **Annotations**
- `@Configuration`: Marks the class as a Spring configuration class.
- `@Slf4j`: Enables logging for WebSocket setup.

### **Methods**
1. `webSocketMapping(UserWebSocketHandler userWebSocketHandler, NewsWebSocketHandler newsWebSocketHandler)`:
    - Maps WebSocket endpoints to their respective handlers.
    - **Endpoints**:
        - `/ws/users` → `UserWebSocketHandler`
        - `/ws/news` → `NewsWebSocketHandler`.
    - **Return Type**: `HandlerMapping`.

2. `handlerAdapter()`:
    - Provides a `WebSocketHandlerAdapter` for WebSocket processing.
    - **Return Type**: `WebSocketHandlerAdapter`.

3. `userWebSocketHandler(JwtUtils jwtUtils)`:
    - Creates a `UserWebSocketHandler` bean with JWT support.
    - **Return Type**: `UserWebSocketHandler`.

---