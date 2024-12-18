## **FIleDescriptorDTO**

### **Class Overview**
A Data Transfer Object (DTO) that encapsulates file metadata and content for file uploads or downloads.

### **Annotations**
- `@Data`: Lombok annotation to auto-generate getter, setter, `toString`, `equals`, and `hashCode`.

### **Fields**
- `name`: The name of the file.
- `type`: The file type, e.g., text, image, video, audio, etc.
- `extension`: The file extension, such as `txt`, `jpg`, `mp3`.
- `userToken`: User identifier or token representing the uploader.
- `file`: The actual file content represented as a byte array.

---

## **Message**

### **Class Overview**
A transient entity representing a generic message with a type, data, and a list of receivers. Suitable for messaging systems or notifications.

### **Annotations**
- `@Data`: Lombok annotation for getter, setter, `toString`, etc.
- `@AllArgsConstructor`: Generates a constructor with all fields.
- `@NoArgsConstructor`: Generates a no-args constructor.

### **Fields**
- `type`: Type of the message, e.g., `CREATE`, `UPDATE`, or `DELETE`.
- `data`: Payload or content of the message.
- `receivers`: The recipients or targets of the message.

### **Nested Class `Type`**
Defines constants for common message types:
- `CREATE`: Represents message type for creation operations.
- `UPDATE`: Represents message type for update operations.
- `DELETE`: Represents message type for delete operations.
- `BATCH`: Represents a batch operation (not in use).

---

## **PageWrapper**

### **Class Overview**
A utility class that wraps pagination details such as page number and page size.

### **Annotations**
- `@Data`: Lombok annotation for automatic getter, setter, `toString`, and utility methods.

### **Fields**
- `page`: The current page number.
- `size`: The number of items per page.

### **Static Methods**
- `of(int page, int size)`:
    - **Description**: A factory method to create a `PageWrapper` instance with the given page and size.
    - **Parameters**:
        - `page`: Page number.
        - `size`: Page size.
    - **Return Type**: `PageWrapper`.

---

## **ProfileDTO**

### **Class Overview**
A Data Transfer Object (DTO) that encapsulates user profile information, including user details, associated news posts, and relationship status.

### **Annotations**
- `@Data`: Lombok annotation to auto-generate getters, setters, `toString`, and other methods.

### **Fields**
- `user`: The `Users` entity representing the profile owner.
- `news`: A list of `News` entities posted by the user.
- `isFollowing`: Indicates whether the current user follows the profile user.
- `isSelf`: Indicates whether the current user is viewing their own profile.

---
