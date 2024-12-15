### API Documentation for Frontend Integration

---

### **Authentication API**

#### **Login**
- **Endpoint:** `POST /api/auth/login`
- **Description:** Authenticates a user and provides a JWT token and role.
- **Request Body:**
  ```json
  {
    "login": "string",
    "password": "string"
  }
  ```
- **Response:**
  - Success (HTTP 200):
    ```json
    {
      "token": "string",
      "role": "string"
    }
    ```
  - Failure (HTTP 401):
    ```json
    {
      "token": null,
      "role": null
    }
    ```
- **Notes:** Use the token in the `Authorization` header for subsequent API requests (`Bearer <token>`).

---

### **User Management API**

#### **Get All Users**
- **Endpoint:** `GET /api/users`
- **Description:** Retrieves a paginated list of users.
- **Query Parameters:**
  - `page` (integer, default: 0): Page number.
  - `size` (integer, default: 10): Number of users per page.
- **Response:**
  - Success (HTTP 200):
    ```json
    [
      {
        "id": "string",
        "username": "string",
        "login": "string",
        "roles": {
          "code": "string",
          "name": "string"
        }
      }
    ]
    ```

#### **Add User**
- **Endpoint:** `POST /api/addUser`
- **Description:** Adds a new user.
- **Request Body:**
  ```json
  {
    "username": "string",
    "login": "string",
    "password": "string"
  }
  ```
- **Response:**
  - Success (HTTP 200):
    ```json
    "User added successfully"
    ```
  - Failure (HTTP 400):
    ```json
    "Invalid user input" or "Role not found"
    ```

#### **Update User**
- **Endpoint:** `POST /api/updateUser`
- **Description:** Updates user details.
- **Request Body:**
  ```json
  {
    "id": "string",
    "username": "string",
    "password": "string"
  }
  ```
- **Response:**
  - Success (HTTP 200):
    ```json
    "User updated successfully"
    ```
  - Failure (HTTP 400):
    ```json
    "Invalid user input"
    ```

#### **Delete User**
- **Endpoint:** `DELETE /api/deleteUser/{id}`
- **Description:** Deletes a user by ID.
- **Response:**
  - Success (HTTP 200):
    ```json
    "User deleted successfully"
    ```
  - Failure (HTTP 500):
    ```json
    "Error deleting user: <error_message>"
    ```

---

### **WebSocket API**

#### **Real-time Updates for User Events**
- **Endpoint:** WebSocket connection to the server.
- **Description:** Provides real-time notifications for user creation, updates, and deletions.
- **Message Format:**
  - Sent from server:
    ```json
    {
      "type": "CREATE/UPDATE/DELETE",
      "user": {
        "id": "string",
        "username": "string",
        "login": "string",
        "roles": {
          "code": "string",
          "name": "string"
        }
      }
    }
    ```

---

### **Authentication Token Management**

#### **JWT Details**
- Tokens are issued with a validity of 10 hours.
- Include the JWT in the `Authorization` header:
  ```
  Authorization: Bearer <token>
  ```

#### **Token Validation**
- Decode the JWT token to retrieve user roles using the `/api/auth/login` endpoint.

---

### **Roles API (For Internal Use)**

#### **Retrieve Default Roles**
- **Endpoint:** (Internally managed; not exposed to frontend)
- **Description:** Handles default role management.

---

### Error Handling
- **HTTP 400:** Bad Request (Invalid input)
- **HTTP 401:** Unauthorized (Authentication failure)
- **HTTP 500:** Internal Server Error (Unexpected issues)
