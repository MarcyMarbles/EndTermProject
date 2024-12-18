## **AbstractSuperService**

### **Class Overview**
An abstract service layer that provides common functionality for soft-deleting entities.

### **Methods**
1. `saveEntity(T entity)`:
    - **Description**: Abstract method to save an entity. Implementation is provided in subclasses.
2. `softDelete(T entity)`:
    - **Description**: Marks an entity as deleted by setting the `deletedAt` timestamp.
3. `softDelete(Iterable<T> entities)`:
    - **Description**: Soft deletes multiple entities.

---

## **CustomUserDetailsService**

### **Class Overview**
Implements Spring Security's `ReactiveUserDetailsService` for reactive user authentication.

### **Methods**
1. `findByUsername(String username)`:
    - **Description**: Fetches a user by login and maps it to `UserDetails`.
    - **Return Type**: `Mono<UserDetails>`.

---

## **EmailService**

### **Class Overview**
Service to send email confirmations asynchronously.

### **Fields**
- `authLink`: Base URL for confirmation links.

### **Methods**
1. `sendConfirmationEmail(PendingCodes pendingCode)`:
    - **Description**: Sends an email with a confirmation link if the pending code is valid.
    - **Return Type**: `Mono<Void>`.

---

## **FileDescriptorService**

### **Class Overview**
Handles file storage and metadata management.

### **Methods**
1. `saveEntity(FileDescriptor entity)`: Saves the file metadata.
2. `saveFile(FIleDescriptorDTO dto)`:
    - **Description**: Saves the file content to disk and persists its metadata in the database.
    - **Return Type**: `Mono<FileDescriptor>`.
3. `softDelete(FileDescriptor entity)`: Marks a file as deleted.

---

## **JwtUtils**

### **Class Overview**
Utility class for handling JWT generation, validation, and extraction.

### **Methods**
1. `generateToken(String username, String role, String id)`: Generates a JWT token.
2. `validateToken(String token, String username)`: Validates the token and checks expiration.
3. `extractUsername(String token)`: Extracts the username from the token.
4. `extractRoles(String token)`: Extracts the role from the token.
5. `getAuthentication(String username, String role)`: Constructs an `Authentication` object.

---

## **NewsService**

### **Class Overview**
Manages `News` entities, including storage, retrieval, and real-time updates.

### **Methods**
1. `findAll(PageWrapper pageWrapper)`: Returns paginated news items.
2. `speciallyForYou(String userId, PageWrapper pageWrapper)`: Fetches news from friends.
3. `findAllByUser(String userId, PageWrapper pageWrapper)`: Fetches user-specific news.
4. `saveNews(News news, String userId, List<String> paths)`:
    - Saves a news post and its attachments.

---

## **PendingService**

### **Class Overview**
Handles email confirmation logic for pending users.

### **Methods**
1. `createPendingCode(Users users)`:
    - Generates a confirmation code and sends a confirmation email.
2. `confirmUser(String code)`:
    - Validates the confirmation code and activates the user.
3. `findByUserId(String userId)`: Fetches pending confirmation codes by user ID.

---

## **RolesService**

### **Class Overview**
Manages roles for users and initializes default roles during startup.

### **Methods**
1. `initRoles()`: Initializes default roles if they don’t exist.
2. `getDefaultRoleCodes()`: Returns the role codes of default roles.
3. `areDefaultRolesCreated()`: Checks if default roles are created.
4. `findByCode(String code)`: Fetches a role by its code.

---

## **UserService**

### **Class Overview**
Provides user management, including CRUD operations, authentication, and confirmation.

### **Methods**
1. `saveUser(Users user)`: Saves a new user with encoded password.
2. `updateUser(Users user)`: Updates user profile information.
3. `validateUserByLoginAndPassword(String login, String password)`: Validates login credentials.
4. `getUserRole(String login)`: Fetches the role of a user by login.
5. `deleteUser(String id)`: Soft deletes a user.
6. `findUserByEmail(String email)`: Fetches a user by email.
7. `confirmUser(PendingCodes code)`: Confirms the user using a pending code.
8. `findByToken(String token)`: Extracts user information from a token.

---