### Documentation for **Repositories**

These are repository interfaces for performing CRUD operations on MongoDB collections related to various entities in the application. The repositories extend `ReactiveMongoRepository`, which allows them to use Spring Data's reactive support for MongoDB, returning reactive types such as `Mono` and `Flux`.

---

### 1. **FileDescriptorRepo**

#### Description:
Handles operations related to `FileDescriptor` entities in the MongoDB database.

#### Methods:

- **`Flux<FileDescriptor> findAll()`**:
    - **Description**: Retrieves all `FileDescriptor` entities where `deletedAt` is `null`, indicating that they are not soft deleted.
    - **Returns**: `Flux<FileDescriptor>`: A stream of `FileDescriptor` entities.

- **`Mono<FileDescriptor> findByPathAndDeletedAtIsNull(String path)`**:
    - **Description**: Finds a `FileDescriptor` by its `path` where `deletedAt` is `null` (i.e., not soft deleted).
    - **Parameters**:
        - `path`: The path of the file descriptor to search for.
    - **Returns**: `Mono<FileDescriptor>`: A `Mono` that contains the `FileDescriptor` if found or an empty result.

---

### 2. **NewsRepo**

#### Description:
Handles operations related to `News` entities in the MongoDB database.

#### Methods:

- **`Flux<News> findAll()`**:
    - **Description**: Retrieves all `News` entities where `deletedAt` is `null` (not soft deleted).
    - **Returns**: `Flux<News>`: A stream of `News` entities.

- **`Flux<News> findSoftDeleted()`**:
    - **Description**: Retrieves all `News` entities where `deletedAt` is not `null`, indicating that the news is soft deleted.
    - **Returns**: `Flux<News>`: A stream of soft-deleted `News` entities.

- **`Flux<News> findByAuthorIdAndDeletedAtIsNull(String authorId)`**:
    - **Description**: Retrieves `News` entities by the `authorId` where `deletedAt` is `null` (not soft deleted).
    - **Parameters**:
        - `authorId`: The ID of the author to filter the news by.
    - **Returns**: `Flux<News>`: A stream of `News` entities.

- **`Flux<News> findByAuthorIdIn(List<String> ids)`**:
    - **Description**: Retrieves `News` entities by a list of author IDs.
    - **Parameters**:
        - `ids`: A list of author IDs to filter the news by.
    - **Returns**: `Flux<News>`: A stream of `News` entities.

---

### 3. **PendingRepo**

#### Description:
Handles operations related to `PendingCodes` entities in the MongoDB database.

#### Methods:

- **`Mono<PendingCodes> findByCodeAndUsedIsFalseAndDueDateAfter(String code, Instant now)`**:
    - **Description**: Retrieves a `PendingCodes` entity by its `code` if the `used` flag is `false` and the `dueDate` is after the specified `Instant`.
    - **Parameters**:
        - `code`: The code to search for.
        - `now`: The current date-time to compare against `dueDate`.
    - **Returns**: `Mono<PendingCodes>`: A `Mono` that contains the `PendingCodes` if found or an empty result.

- **`Mono<PendingCodes> findByUserIdAndDeletedAtIsNull(String userId)`**:
    - **Description**: Retrieves a `PendingCodes` entity by the `userId` where `deletedAt` is `null` (not soft deleted).
    - **Parameters**:
        - `userId`: The ID of the user.
    - **Returns**: `Mono<PendingCodes>`: A `Mono` that contains the `PendingCodes` if found or an empty result.

---

### 4. **RolesRepo**

#### Description:
Handles operations related to `Roles` entities in the MongoDB database.

#### Methods:

- **`Mono<Boolean> existsByCodeAndDeletedAtIsNull(String code)`**:
    - **Description**: Checks if a role with the specified `code` exists and is not soft deleted.
    - **Parameters**:
        - `code`: The code of the role to check.
    - **Returns**: `Mono<Boolean>`: A `Mono` containing `true` if the role exists and is not soft deleted, otherwise `false`.

- **`Flux<Roles> findRolesByCodeInAndDeletedAtIsNull(List<String> codes)`**:
    - **Description**: Retrieves `Roles` entities by a list of role codes where `deletedAt` is `null` (not soft deleted).
    - **Parameters**:
        - `codes`: A list of role codes to filter by.
    - **Returns**: `Flux<Roles>`: A stream of `Roles` entities.

- **`Flux<Roles> findAll()`**:
    - **Description**: Retrieves all `Roles` entities where `deletedAt` is `null` (not soft deleted).
    - **Returns**: `Flux<Roles>`: A stream of `Roles` entities.

- **`Mono<Roles> findByCodeAndDeletedAtIsNull(String code)`**:
    - **Description**: Retrieves a `Roles` entity by its `code` where `deletedAt` is `null` (not soft deleted).
    - **Parameters**:
        - `code`: The code of the role to search for.
    - **Returns**: `Mono<Roles>`: A `Mono` containing the `Roles` entity if found or an empty result.

---

### 5. **UserRepo**

#### Description:
Handles operations related to `Users` entities in the MongoDB database.

#### Methods:

- **`Flux<Users> findAllByDeletedAtIsNull(Pageable pageable)`**:
    - **Description**: Retrieves a paginated list of `Users` entities where `deletedAt` is `null` (not soft deleted).
    - **Parameters**:
        - `pageable`: The pagination information.
    - **Returns**: `Flux<Users>`: A stream of `Users` entities.

- **`Mono<Users> findByLoginAndDeletedAtIsNull(String login)`**:
    - **Description**: Retrieves a `Users` entity by its `login` where `deletedAt` is `null` (not soft deleted).
    - **Parameters**:
        - `login`: The login of the user to search for.
    - **Returns**: `Mono<Users>`: A `Mono` containing the `Users` entity if found or an empty result.

- **`Flux<Users> findAll()`**:
    - **Description**: Retrieves all `Users` entities where `deletedAt` is `null` (not soft deleted).
    - **Returns**: `Flux<Users>`: A stream of `Users` entities.

- **`Flux<Users> findSoftDeleted()`**:
    - **Description**: Retrieves all `Users` entities where `deletedAt` is not `null`, indicating that the user is soft deleted.
    - **Returns**: `Flux<Users>`: A stream of soft-deleted `Users` entities.

- **`Mono<Users> findByIdAndDeletedAtIsNull(String id)`**:
    - **Description**: Retrieves a `Users` entity by its `id` where `deletedAt` is `null` (not soft deleted).
    - **Parameters**:
        - `id`: The ID of the user.
    - **Returns**: `Mono<Users>`: A `Mono` containing the `Users` entity if found or an empty result.

- **`Mono<Users> findByEmailAndDeletedAtIsNull(String email)`**:
    - **Description**: Retrieves a `Users` entity by its `email` where `deletedAt` is `null` (not soft deleted).
    - **Parameters**:
        - `email`: The email of the user.
    - **Returns**: `Mono<Users>`: A `Mono` containing the `Users` entity if found or an empty result.

- **`Mono<Users> findByUsernameAndDeletedAtIsNull(String username)`**:
    - **Description**: Retrieves a `Users` entity by its `username` where `deletedAt` is `null` (not soft deleted).
    - **Parameters**:
        - `username`: The username of the user.
    - **Returns**: `Mono<Users>`: A `Mono` containing the `Users` entity if found or an empty result.

---

