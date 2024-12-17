### Documentation for **EntityEventListener**

#### Overview

The `EntityEventListener` class is a custom Spring component that listens to various MongoDB lifecycle events (before and after save, delete, etc.) for entities extending `AbstractSuperClass`. It reacts to events and performs actions such as setting timestamps, confirming user actions, and logging information for audit and troubleshooting purposes.

This class is implemented as an **ApplicationListener** of the generic `MongoMappingEvent<?>` type, handling events such as `BeforeConvertEvent`, `BeforeSaveEvent`, `AfterSaveEvent`, `BeforeDeleteEvent`, and `AfterDeleteEvent`.

#### Dependencies

- **UserService**: A service that handles operations related to user management, including confirming user actions when certain conditions are met.
- **MongoDB Events**: The class listens for MongoDB-specific lifecycle events to interact with entities before and after CRUD operations.

#### Constructor

- **`EntityEventListener(UserService userService)`**:
    - **Parameters**:
        - `userService`: Injected service for handling user-related operations.
    - **Purpose**: Initializes the listener with a `UserService` instance to interact with user-related functionality.

#### Methods

1. **`onApplicationEvent(MongoMappingEvent<?> event)`**
    - **Description**: This method is triggered when any MongoDB lifecycle event occurs. It checks the type of the event and calls the appropriate handler method based on the event type.
    - **Parameters**:
        - `event`: The event that occurred (could be any MongoDB lifecycle event such as before saving, after saving, before deleting, etc.).
    - **Process**:
        - Checks if the event source is an instance of `AbstractSuperClass`.
        - Depending on the type of event (e.g., `BeforeSaveEvent`, `AfterSaveEvent`), delegates the processing to the respective handler method.
    - **Event Types Handled**:
        - `BeforeConvertEvent`
        - `BeforeSaveEvent`
        - `AfterSaveEvent`
        - `BeforeDeleteEvent`
        - `AfterDeleteEvent`

2. **`handleBeforeConvert(AbstractSuperClass entity)`**
    - **Description**: This method is triggered before an entity is converted into its database representation.
    - **Parameters**:
        - `entity`: The entity being processed, which is an instance of `AbstractSuperClass`.
    - **Process**:
        - Logs the entity being converted.
        - If the `createdAt` field of the entity is `null`, sets it to the current timestamp (`Instant.now()`).
        - If `createdAt` is already present, it updates the `updatedAt` timestamp to the current time.

3. **`handleBeforeSave(AbstractSuperClass entity)`**
    - **Description**: This method is triggered before an entity is saved to the database.
    - **Parameters**:
        - `entity`: The entity being processed, which is an instance of `AbstractSuperClass`.
    - **Process**:
        - Logs the entity before it is saved to the database.
        - No additional changes are made to the entity in this method (empty logic).

4. **`handleAfterSave(AbstractSuperClass entity)`**
    - **Description**: This method is triggered after an entity has been saved to the database.
    - **Parameters**:
        - `entity`: The entity that has been saved, which is an instance of `AbstractSuperClass`.
    - **Process**:
        - Logs the entity after it has been saved.
        - If the entity is an instance of `PendingCodes`, checks if the code has been marked as `used`. If it has, it triggers the `userService.confirmUser(code)` method to confirm the user associated with the code.

5. **`handleBeforeDelete(AbstractSuperClass entity)`**
    - **Description**: This method is triggered before an entity is deleted from the database.
    - **Parameters**:
        - `entity`: The entity being deleted, which is an instance of `AbstractSuperClass`.
    - **Process**:
        - Logs the entity before it is deleted.
        - No further actions are performed in this method.

6. **`handleAfterDelete(AbstractSuperClass entity)`**
    - **Description**: This method is triggered after an entity has been deleted from the database.
    - **Parameters**:
        - `entity`: The entity that has been deleted, which is an instance of `AbstractSuperClass`.
    - **Process**:
        - Logs the entity after it has been deleted.
        - No further actions are performed in this method.

#### Logging

- The class uses **SLF4J logging** to log the various stages of MongoDB lifecycle events, such as before converting, saving, and deleting entities. These logs are useful for debugging and tracking changes to entities in the database.

#### Event Handling Flow

- **Before Convert**: Sets or updates the `createdAt` and `updatedAt` timestamps for entities.
- **Before Save**: Logs the entity before it is saved but does not modify it.
- **After Save**: Confirms a user if the saved entity is a `PendingCode` that has been marked as used.
- **Before Delete**: Logs the entity being deleted, with no further actions.
- **After Delete**: Logs the entity after it has been deleted, with no further actions.

#### Use Cases

- **Entity Lifecycle Management**: This listener is helpful for managing timestamps (e.g., `createdAt`, `updatedAt`) for MongoDB documents automatically, which is important for auditing and tracking changes.
- **User Confirmation**: After saving a `PendingCode` entity, the listener triggers a user confirmation action, ensuring that users who take actions (such as email verification) are processed automatically.

#### Benefits

- **Centralized Event Handling**: All MongoDB lifecycle events for entities are handled in one place, making the codebase more modular and reducing redundancy.
- **Improved Auditing**: By automatically setting and updating timestamps and logging entity lifecycle events, it improves traceability and auditing of database changes.
- **Automatic User Confirmation**: Automatically handles user confirmation based on `PendingCode` entities, improving the overall user experience for actions like email verification.

#### Error Handling

- The class currently logs the operations but does not handle errors explicitly in the event handlers. If any exception occurs, it would need to be addressed at the service layer (e.g., within `UserService` or other service classes).

---

