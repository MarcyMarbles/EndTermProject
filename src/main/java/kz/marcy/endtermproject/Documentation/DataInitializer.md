### Documentation for `DataInitializer` Class

#### **Description**:
The `DataInitializer` class is a Spring component designed to initialize the application with a default user (admin) upon application startup. It uses the `CommandLineRunner` interface, which runs the `run` method when the Spring Boot application starts. The class ensures that an `admin` user with the specified credentials exists in the system, setting the `ROLE_ADMIN` role if not already present.

#### **Dependencies**:
- **`RolesService`**: A service that interacts with the `Roles` entity, allowing retrieval of roles by their code (e.g., `ROLE_ADMIN`).
- **`UserRepo`**: Repository interface for interacting with the `Users` data in the database, including checking for existing users and saving new ones.
- **`UserService`**: Service that handles operations related to `Users`, including saving new users.

#### **Constructor**:
- **`DataInitializer(RolesService rolesService, UserRepo userRepo, UserService userService)`**:
    - **Description**: Initializes the `DataInitializer` with the required services (`RolesService`, `UserRepo`, and `UserService`).
    - **Parameters**:
        - `rolesService`: Service for retrieving role data.
        - `userRepo`: Repository for interacting with user data.
        - `userService`: Service for handling user-related operations.

#### **Method**:

- **`void run(String... args)`**:
    - **Description**: This method is executed when the application starts. It checks if an `admin` user exists and creates one if necessary. It performs the following operations:
        - Creates a new `Users` object with predefined credentials (`admin` as the username, password, and login).
        - Retrieves the `ROLE_ADMIN` role using the `RolesService`.
        - Checks if the `admin` user already exists by querying the `UserRepo`. If the user does not exist, it saves the new user with the `admin` role using `UserService`.
    - **Steps**:
        1. **Create `Users` object**: A new `Users` object is created with `admin` credentials.
        2. **Find the `ROLE_ADMIN`**: The role associated with `ROLE_ADMIN` is fetched using `rolesService.findByCode("ROLE_ADMIN")`.
        3. **Check if user exists**: If no existing user with the same login is found (`userRepo.findByLoginAndDeletedAtIsNull`), the `userService.saveUser(user)` is called to save the `admin` user.
        4. **Subscription**: The entire process runs asynchronously with the help of `.subscribe()`, meaning the operation is executed as soon as the application starts.

#### **Key Concepts**:
- **CommandLineRunner**: This Spring Boot interface allows you to run specific code after the application context is loaded but before the application starts accepting requests. In this case, it ensures that the default admin user is set up on startup.
- **Reactive Programming**: The class utilizes `Mono` (part of Spring WebFlux's reactive programming model) to handle asynchronous operations such as role fetching and user saving without blocking the main application flow.
- **Role Assignment**: The admin user is assigned the `ROLE_ADMIN` role using the `rolesService`. The role is fetched and assigned before the user is saved.

#### **Example Usage**:
When the application starts, this `DataInitializer` ensures that an `admin` user with the appropriate role exists in the database. If the user already exists, no changes are made. If not, a new user is created and saved with the `ROLE_ADMIN` role.

---

This class is part of the application's startup process, ensuring that essential data (in this case, the `admin` user) is initialized in the system.