### Documentation for **AuthController**

#### Class Description: `AuthController`

This controller handles requests for user authentication and email verification. It includes two main endpoints:
- User login with JWT token generation.
- Email verification using a confirmation code.

#### Constructor

The controller constructor accepts three dependencies:
- **`JwtUtils`** — A service for working with JWT tokens (generation and validation).
- **`UserService`** — A service for handling user-related operations (e.g., validating user data, retrieving roles, and user IDs).
- **`PendingService`** — A service for working with unverified users (email confirmation).

#### Methods

1. **`/login` (POST)**
    - **Description**: This method handles the user login request. The user must provide their login and password.
    - **Parameters**: The request body should contain an object with the following fields:
        - `login`: The user's login.
        - `password`: The user's password.
    - **Process**:
        1. First, it checks if the user exists and if the password is correct.
        2. If the credentials are valid, a JWT token is generated and returned in the response.
        3. In case of invalid credentials or a missing user, a **401 Unauthorized** error is returned.
    - **Response**:
        - **Success**: Returns a **200 OK** with the response body containing the token, user's role, and ID.
        - **Error**: Returns a **401 Unauthorized** with an error message.

2. **`/confirm` (POST)**
    - **Description**: This method handles email confirmation for a user using the provided code.
    - **Parameters**: The request body should contain the string parameter `code`, which is the confirmation code.
    - **Process**:
        1. The method calls the confirmation service (`PendingService`) to validate the code.
        2. If the code is valid and the user is not already confirmed, their status is updated to confirmed.
        3. If confirmation is successful, a success message is returned.
        4. If the code is invalid or the user is already confirmed, a **400 Bad Request** error is returned.
    - **Response**:
        - **Success**: Returns **200 OK** with the message "Email confirmed successfully."
        - **Error**: Returns **400 Bad Request** with an error message (e.g., "Invalid code" or "User is already confirmed").

#### DTO Classes

1. **LoginRequest**
    - **Description**: Class used for the login request.
    - **Fields**:
        - `login`: The user's login.
        - `password`: The user's password.

2. **AuthResponse**
    - **Description**: Class used for the response after a successful login.
    - **Fields**:
        - `token`: The JWT token used for future authentications.
        - `role`: The user's role (e.g., "USER", "ADMIN").
        - `id`: The user's ID.

#### Notes

- All login attempts and email confirmation actions are logged.
- If the login or password is incorrect, a warning is logged.
- Errors during email confirmation are also logged for user action monitoring.

#### Error Responses

- **401 Unauthorized**: Returned for incorrect login/password or if the user does not exist.
- **400 Bad Request**: Returned for email confirmation errors or invalid confirmation codes.

#### Token Usage

- Upon successful login, a JWT token is returned, which should be used for future authentication (e.g., in request headers).


-----------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------

### Documentation for **FileDescriptorController**

#### Class Description: `FileDescriptorController`

This controller handles the upload functionality for files. It uses the `FileDescriptorService` to handle the logic for saving file metadata. The main endpoint provided by this controller is for uploading a file descriptor.

#### Constructor

The controller uses **dependency injection** to receive the following dependency:
- **`FileDescriptorService`** — A service for handling the file descriptor operations (e.g., saving file metadata).

#### Methods

1. **`/files/upload` (POST)**
    - **Description**: This method handles the upload of a file descriptor. The file descriptor contains metadata about the file being uploaded (e.g., name, size, type, etc.).
    - **Parameters**: The request body should contain the file descriptor metadata in the form of a **`FIleDescriptorDTO`** object. The `FIleDescriptorDTO` object contains the following fields:
        - `name`: The name of the file.
        - `type`: The type of the file (e.g., "image/jpeg").
        - `size`: The size of the file in bytes.
        - Any other relevant file attributes.
    - **Process**:
        1. The method calls the `saveFile` method of the `FileDescriptorService` to handle the file descriptor.
        2. If the file descriptor is saved successfully, a **200 OK** response with the saved `FileDescriptor` is returned.
        3. If the file descriptor cannot be saved or there is an error, a **400 Bad Request** response is returned.
    - **Response**:
        - **Success**: Returns a **200 OK** with the response body containing the `FileDescriptor` object that represents the metadata of the uploaded file.
        - **Error**: Returns a **400 Bad Request** if the file descriptor cannot be processed or saved.

#### DTO Classes

1. **FIleDescriptorDTO**
    - **Description**: Class used to represent the file descriptor during the upload process.
    - **Fields**:
        - `name`: The name of the file being uploaded.
        - `type`: The MIME type of the file.
        - `size`: The size of the file in bytes.
        - Any additional fields needed to describe the file.

2. **FileDescriptor**
    - **Description**: Entity representing the saved file descriptor in the database. Typically, this includes metadata like file name, type, size, and any other relevant details.
    - **Fields**:
        - `name`: The name of the file.
        - `type`: The MIME type of the file.
        - `size`: The size of the file in bytes.
        - Other fields like file location, status, etc.

#### Process Flow

1. **File Upload**: The user sends a POST request with file metadata (through `FIleDescriptorDTO`).
2. **Service Layer**: The `FileDescriptorService` processes the request by saving the metadata to a database or other storage system.
3. **Response**:
    - If the operation is successful, the system responds with the saved `FileDescriptor` object and a **200 OK** status.
    - If there is an issue with the request, the system responds with **400 Bad Request**.

#### Error Responses

- **400 Bad Request**: Returned if the file descriptor cannot be saved or if the provided data is invalid.

#### Notes

- The file descriptor does not directly handle the file itself. Instead, it handles the metadata about the file.
- The `FIleDescriptorDTO` object is used as the input for file upload, while the `FileDescriptor` object is returned as the response.

-----------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------

### Documentation for **NewsController**

#### Class Description: `NewsController`

This controller is responsible for handling news-related operations in the application. It interacts with the `NewsService` to retrieve and manage news articles. The controller provides several endpoints for retrieving news articles and posting new ones.

#### Constructor

The controller uses **dependency injection** to receive the following dependency:
- **`NewsService`** — A service for handling the news-related logic (e.g., fetching news, saving news).

#### Methods

1. **`/news` (GET)**
    - **Description**: This endpoint retrieves a paginated list of all news articles.
    - **Parameters**:
        - `page` (optional, default: `0`): The page number for pagination.
        - `size` (optional, default: `10`): The number of news articles per page.
    - **Process**:
        - The `getAllNews` method fetches news articles using the `findAll` method of `NewsService`, which takes pagination parameters (page and size).
        - The results are returned as a **Flux** of `ResponseEntity<News>`.
        - If there are no news articles, it returns a **400 Bad Request** response.
    - **Response**:
        - **Success**: Returns a **200 OK** with a list of news articles.
        - **Error**: Returns a **400 Bad Request** if the news articles cannot be fetched.

2. **`/news/speciallyForYou` (GET)**
    - **Description**: This endpoint retrieves a list of news articles tailored specifically for a user.
    - **Parameters**:
        - `userId`: The ID of the user for whom the news articles should be retrieved.
        - `page` (optional, default: `0`): The page number for pagination.
        - `size` (optional, default: `10`): The number of news articles per page.
    - **Process**:
        - The `getAllNewsByUser` method calls the `speciallyForYou` method in the `NewsService`, which fetches user-specific news using the user's `userId` and pagination parameters.
        - The results are returned as a **Flux** of `ResponseEntity<News>`.
        - If no news is found, it returns a **400 Bad Request**.
    - **Response**:
        - **Success**: Returns a **200 OK** with the user-specific news articles.
        - **Error**: Returns a **400 Bad Request** if the news articles cannot be retrieved or the user is not found.

3. **`/news/post` (POST)**
    - **Description**: This endpoint allows the creation of a new news article.
    - **Parameters**:
        - `news` (required): The content of the news article, including the title, content, and any other relevant fields.
        - `userId` (required): The ID of the user who is posting the news article.
        - `paths` (optional, required): A list of paths or URLs related to the news article (e.g., images, attachments).
    - **Process**:
        - The method checks if the `news` object and its `content` are provided.
        - If the `news` content is null or invalid, it returns a **400 Bad Request** response.
        - If the news is valid, the method saves the news article using the `saveNews` method from the `NewsService` and returns a **200 OK** with the saved news.
        - If the news cannot be saved, it returns a **400 Bad Request**.
    - **Response**:
        - **Success**: Returns a **200 OK** with the saved news article.
        - **Error**: Returns a **400 Bad Request** if the news article is invalid or cannot be saved.

#### DTO Classes

1. **News**
    - **Description**: Represents a news article.
    - **Fields**:
        - `title`: The title of the news article.
        - `content`: The body content of the news article.
        - Any additional fields like `author`, `datePosted`, etc.

2. **PageWrapper**
    - **Description**: Used to handle pagination information (page number and size).
    - **Fields**:
        - `page`: The page number (zero-indexed).
        - `size`: The number of items per page.

#### Process Flow

1. **Get All News**: A user sends a GET request to retrieve all news articles. The server fetches the articles, applies pagination, and returns the result.
2. **Get News for User**: A user sends a GET request to retrieve personalized news. The server fetches articles specific to the user based on their `userId`.
3. **Add News**: A user sends a POST request with a news article. The server validates the request, saves the article, and returns the saved article in the response.

#### Error Responses

- **400 Bad Request**: Returned when the request is invalid or the news article cannot be retrieved or saved.
- **Empty Responses**: If no news is found (either in the general list or user-specific list), the server returns an empty response with a **400 Bad Request**.

#### Notes

- Pagination is applied on both the `getAllNews` and `getAllNewsByUser` endpoints, allowing clients to fetch news in chunks.
- The `addNews` method expects a valid news object with content, and an optional list of paths for related media.

-----------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------
-----------------------------------------------------------------------------------------------------

### Documentation for **UserController**

#### Class Description: `UserController`

This controller is responsible for handling user-related operations, such as registration, updating user information, retrieving user profiles, and deleting users. It interacts with multiple services, including `UserService`, `RolesService`, `PendingService`, and `NewsService`, to manage user data and perform necessary actions.

#### Constructor

The controller uses **dependency injection** to receive the following services:
- **`UserService`** — A service that handles user-related logic, including fetching, saving, and updating user data.
- **`RolesService`** — A service that manages roles, such as assigning roles to users.
- **`PendingService`** — A service that handles pending user actions, such as email verification.
- **`NewsService`** — A service that provides functionality for retrieving news articles related to a user.

#### Methods

1. **`/users` (GET)**
    - **Description**: This endpoint retrieves a paginated list of all users.
    - **Parameters**:
        - `page` (optional, default: `0`): The page number for pagination.
        - `size` (optional, default: `10`): The number of users per page.
    - **Process**:
        - The method calls `userService.findAll` to fetch the users using pagination parameters (page and size).
    - **Response**:
        - **Success**: Returns a list of users as a **200 OK** response.
        - **Error**: If no users are found or there is an issue, it returns a **400 Bad Request**.

2. **`/register` (POST)**
    - **Description**: This endpoint allows for registering a new user.
    - **Parameters**:
        - `user` (required): The user object containing the username, password, and other details.
    - **Process**:
        - Validates the user input and assigns the role `ROLE_USER` to the new user.
        - Sets the user as pending and saves the user in the system.
        - Sends a confirmation request (e.g., email verification) by using the `PendingService`.
    - **Response**:
        - **Success**: Returns a **200 OK** with a message confirming the registration and email verification.
        - **Error**: Returns a **400 Bad Request** if the input is invalid or the role `ROLE_USER` is not found.

3. **`/updateUser` (POST)**
    - **Description**: This endpoint allows for updating an existing user's details.
    - **Parameters**:
        - `user` (required): The user object to update, including the user’s `id`.
    - **Process**:
        - The method validates the user input and updates the user using the `userService.updateUser`.
    - **Response**:
        - **Success**: Returns a **200 OK** with a message confirming that the user was updated.
        - **Error**: Returns a **400 Bad Request** if the user input is invalid.

4. **`/deleteUser/{id}` (DELETE)**
    - **Description**: This endpoint allows for deleting a user by their ID.
    - **Parameters**:
        - `id` (required): The unique ID of the user to delete.
    - **Process**:
        - The method deletes the user using `userService.deleteUser`.
    - **Response**:
        - **Success**: Returns a **200 OK** with a message confirming that the user was deleted.
        - **Error**: Returns a **500 Internal Server Error** if there is an issue deleting the user.

5. **`/user/profile/{username}` (POST)**
    - **Description**: This endpoint retrieves the profile of a user, which includes the user’s details and their news articles.
    - **Parameters**:
        - `username` (required): The username of the user whose profile is being accessed.
        - `Authorization` (required): The authorization header containing a Bearer token for authentication.
    - **Process**:
        - Validates the token and fetches the user profile along with news articles associated with that user.
        - Checks if the profile being accessed is the current user's profile (self-profile) or another user's profile.
        - Returns the profile data along with the news articles and a flag indicating whether the user is following the current user.
    - **Response**:
        - **Success**: Returns a **200 OK** with the profile data (user and associated news articles).
        - **Error**: Returns a **400 Bad Request** if the request is invalid, a **401 Unauthorized** if the token is missing or invalid, or a **404 Not Found** if the user is not found.

#### Helper Methods

1. **`extractToken`**
    - **Description**: Extracts the Bearer token from the `Authorization` header.
    - **Process**: Checks if the `Authorization` header starts with `Bearer `, and if so, extracts the token. Returns `null` if the header is invalid.

#### Process Flow

1. **Get All Users**: A user sends a GET request to retrieve all users, with pagination. The server fetches the users and returns them in a paginated format.
2. **Register User**: A user sends a POST request with their details. The server saves the user, assigns a role, and triggers a confirmation process (email verification).
3. **Update User**: A user sends a POST request to update their details. The server processes and saves the updated data.
4. **Delete User**: A user sends a DELETE request with the user ID. The server deletes the specified user.
5. **Get User Profile**: A user sends a POST request to retrieve another user's profile. The server fetches the user’s details and news, checking whether the profile is for the requesting user or someone else.

#### Error Responses

- **400 Bad Request**: If the user input is invalid or required parameters are missing (e.g., username, password).
- **401 Unauthorized**: If the authorization token is missing or invalid.
- **404 Not Found**: If the user or profile cannot be found.
- **500 Internal Server Error**: If there is an issue with deleting a user or performing other actions.

#### Notes

- The `ProfileDTO` class is used to provide detailed profile information, including user details, news articles, and social interactions (e.g., following status).
- The profile endpoint supports viewing both self-profiles and other users' profiles.
- Pagination is applied to the `getAllUsers` endpoint for efficient fetching of large sets of data.

