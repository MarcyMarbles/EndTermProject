## **AbstractSuperClass**

### **Class Overview**
An abstract superclass that defines common fields for all entities, such as creation, update, and deletion timestamps.

### **Annotations**
- `@Getter` and `@Setter`: Lombok annotations to auto-generate getter and setter methods.

### **Fields**
- `id`: Unique identifier for the entity.
- `createdAt`: Timestamp when the entity was created.
- `updatedAt`: Timestamp when the entity was last updated.
- `deletedAt`: Timestamp when the entity was marked as deleted.

### **Methods**
- `isDeleted()`:
    - **Description**: Checks whether the entity is deleted by verifying if `deletedAt` is not null.
    - **Return Type**: `boolean`.

---

## **Comments**

### **Class Overview**
Represents user comments on news posts.

### **Annotations**
- `@Document`: Marks this class as a MongoDB document.
- `@EqualsAndHashCode(callSuper = true)`: Inherits `equals` and `hashCode` logic from the superclass.
- `@Data`: Lombok annotation to auto-generate boilerplate code.

### **Fields**
- `content`: Editable text content of the comment.
- `author`: Reference to the user who authored the comment (immutable).
- `news`: Reference to the news item that the comment belongs to (immutable).

---

## **FileDescriptor**

### **Class Overview**
Represents metadata for a file uploaded to the system.

### **Annotations**
- `@Document`: Marks this class as a MongoDB document.
- `@EqualsAndHashCode(callSuper = true)`: Inherits equality checks from the superclass.
- `@Data`: Lombok annotation for getter, setter, `toString`, etc.

### **Fields**
- `name`: Name of the file.
- `type`: Type of the file, e.g., text, image, video, etc.
- `path`: File path in the file system.
- `size`: Size of the file in bytes.
- `extension`: File extension (e.g., `txt`, `jpg`, `mp3`).
- `userId`: ID of the user who uploaded the file.

---

## **News**

### **Class Overview**
Represents a news post, including content, attachments, and user interactions.

### **Annotations**
- `@Document`: Marks this class as a MongoDB document.
- `@EqualsAndHashCode(callSuper = true)`: Inherits equality checks from the superclass.
- `@Data`: Lombok annotation for convenience methods.

### **Fields**
- `content`: Editable content of the news post.
- `attachments`: List of file attachments (editable).
- `author`: Reference to the author of the news post (immutable).
- `likes`: List of users who liked the post (editable).
- `dislikes`: List of users who disliked the post (editable).
- `comments`: List of comments associated with the news post (editable).

---

## **Roles**

### **Class Overview**
Represents user roles in the system, such as `ADMIN` or `USER`.

### **Annotations**
- `@Document`: Marks this class as a MongoDB document.
- `@EqualsAndHashCode(callSuper = true)`: Inherits equality checks from the superclass.
- `@Data`: Lombok annotation for getters, setters, and other utility methods.

### **Fields**
- `name`: Name of the role, e.g., "Administrator".
- `code`: Role code, e.g., "ROLE_ADMIN".

---

## **Users**

### **Class Overview**
Represents a user entity with fields for authentication, profile details, and relationships.

### **Annotations**
- `@Document`: Marks this class as a MongoDB document.
- `@EqualsAndHashCode(callSuper = true)`: Inherits equality checks from the superclass.
- `@Data`: Lombok annotation for boilerplate code.

### **Fields**
- `login`: User’s unique login identifier (immutable).
- `password`: User’s password (editable).
- `username`: User’s display name (editable).
- `email`: User’s email address (editable).
- `roles`: User’s role, editable only by an `ADMIN`.
- `avatar`: Reference to a file descriptor representing the user's avatar image (editable).
- `friends`: List of friend IDs, which can include group members (editable).
- `isGroup`: Indicates whether the user represents a group (immutable).
- `isPending`: Indicates if the user is pending email confirmation (editable).

---