# 💬 Chat Application - Multimedia Messaging System

A Spring Boot-based chat application with multimedia messaging capabilities, persistent storage, and a comprehensive web interface for testing and monitoring.

## 📋 Table of Contents

- [Features](#features)
- [Quick Start](#quick-start)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Testing Tools](#testing-tools)
- [Database Management](#database-management)
- [Debugging Guide](#debugging-guide)
- [Common Issues](#common-issues)
- [Development Guidelines](#development-guidelines)

## ✨ Features

- **Multi-room Chat System**: Users can participate in multiple chat rooms
- **Multimedia Messages**: Support for text messages, image uploads, or both
- **Persistent Storage**: H2 file-based database that retains data across restarts
- **Real-time Message Viewing**: Web interface with auto-refresh capabilities
- **Comprehensive Testing Tools**: Multiple web interfaces for testing different scenarios
- **Role-based Room Access**: Users can only access rooms they're members of
- **File Upload Management**: Configurable file storage with size limits
- **Interactive API Documentation**: Swagger UI for API exploration

## 🚀 Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git

### Installation & Running

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd chat
   ```

2. **Build the application**

   ```bash
   mvn clean install
   ```

3. **Run the application**

   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - **Main Dashboard**: http://localhost:8080/
   - **API Documentation**: http://localhost:8080/swagger-ui.html
   - **Database Console**: http://localhost:8080/h2-console

### Default Test Data

The application automatically creates test users and rooms on first startup:

**Users:**

- `john`, `alice`, `bob`, `emma`, `david`, `sarah`
- `default-user` (for testing purposes)

**Rooms:**

- General Chat, Dev Team, Marketing Team, Project Alpha, Random Chat, Test Room, Management

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/chatmessage/chat/
│   │   ├── config/          # Configuration classes
│   │   │   ├── DataInitializer.java    # Test data setup
│   │   │   ├── CorsConfig.java         # CORS configuration
│   │   │   └── SwaggerConfig.java      # API documentation config
│   │   ├── controller/      # REST API endpoints
│   │   │   ├── RoomController.java     # Room management API
│   │   │   ├── MessageController.java  # Messaging API
│   │   │   ├── UserController.java     # User management API
│   │   │   └── TestController.java     # Testing utilities API
│   │   ├── model/           # JPA entities
│   │   │   ├── Room.java              # Room entity
│   │   │   ├── Message.java           # Message entity
│   │   │   └── User.java              # User entity
│   │   ├── repository/      # Data access layer
│   │   │   ├── RoomRepository.java    # Room data operations
│   │   │   ├── MessageRepository.java # Message data operations
│   │   │   └── UserRepository.java    # User data operations
│   │   ├── service/         # Business logic
│   │   │   ├── RoomService.java       # Room operations
│   │   │   ├── MessageService.java    # Message operations
│   │   │   ├── UserService.java       # User operations
│   │   │   └── StorageService.java    # File upload handling
│   │   └── ChatApplication.java       # Main application class
│   └── resources/
│       ├── static/          # Web interface files
│       │   ├── index.html              # Main dashboard
│       │   ├── message-viewer.html     # Message viewing interface
│       │   ├── message-tester.html     # Message sending interface
│       │   ├── upload-examples.html    # File upload testing
│       │   └── simple-upload.html      # Simple upload test
│       └── application.properties      # Application configuration
```

## 📚 API Documentation

### Core Endpoints

#### Room Management

- `GET /api/rooms` - Get rooms for a user
- `GET /api/rooms/all` - Get all rooms (admin)
- `GET /api/rooms/{roomId}` - Get specific room details
- `GET /api/rooms/membership-summary` - Get detailed membership info
- `POST /api/rooms` - Create a new room

#### Messaging

- `POST /api/messages` - Send a message (text, image, or both)
- `GET /api/rooms/{roomId}/messages` - Get messages for a room

#### User Management

- `GET /api/users` - Get all users
- `GET /api/users/{userId}` - Get specific user
- `POST /api/users` - Create a new user

#### Testing Utilities

- `POST /api/test/upload` - Test file upload functionality
- `POST /api/test/message-test` - Test message with file upload

### Authentication Note

This is a prototype version without authentication. All endpoints use `userId` parameters for user identification.

## 🧪 Testing Tools

The application includes several web interfaces for comprehensive testing:

### 1. Main Dashboard (`/`)

- Central hub with system statistics
- Quick access to all tools
- Server status monitoring

### 2. Message Viewer (`/message-viewer.html`)

- Real-time message viewing
- Room-based organization
- Auto-refresh every 5 seconds
- Image message support

### 3. Message Tester (`/message-tester.html`)

- Send messages with text and/or images
- Room selection interface
- Image preview functionality
- Response validation

### 4. Upload Examples (`/upload-examples.html`)

- Multiple file upload scenarios
- Form validation testing
- Error handling demonstration

## 🗃️ Database Management

### H2 Database Console

Access the database directly via: http://localhost:8080/h2-console

**Connection Settings:**

- JDBC URL: `jdbc:h2:file:./data/chatdb`
- User Name: `sa`
- Password: (leave empty)

### Database Files

- **Location**: `./data/chatdb.mv.db`
- **Type**: File-based H2 database
- **Persistence**: Data survives application restarts
- **Git**: Database files are excluded from version control via `.gitignore`

### File Storage

- **Upload Directory**: `./uploads/`
- **Configuration**: `file.upload-dir=./uploads` in application.properties
- **Git**: Upload directory is excluded from version control

### Useful Queries

```sql
-- View all rooms and their members
SELECT * FROM rooms;
SELECT * FROM room_members;

-- View all messages with timestamps
SELECT * FROM messages ORDER BY timestamp DESC;

-- View all users
SELECT * FROM users;

-- Count messages per room
SELECT room_id, COUNT(*) as message_count
FROM messages
GROUP BY room_id;
```

## 🐛 Debugging Guide

### Log Levels

The application uses different log levels for debugging:

```properties
# Application-specific logs (DEBUG level)
logging.level.com.chatmessage.chat=DEBUG

# Spring Web logs (INFO level)
logging.level.org.springframework.web=INFO
```

### Key Log Locations

1. **Room Operations**: Look for logs in `RoomController` and `RoomService`
2. **Message Operations**: Check `MessageController` and `MessageService`
3. **File Uploads**: Monitor `StorageService` and multipart handling
4. **Database Operations**: JPA/Hibernate logs show SQL queries

### Debug Endpoints

- `GET /api/rooms/all` - View all rooms without user restrictions
- `GET /api/rooms/membership-summary` - Detailed membership information
- `POST /api/test/upload` - Isolate file upload issues

### Common Debug Steps

1. **Check Console Output**: Application startup logs show test data creation
2. **Verify Database**: Use H2 console to inspect data directly
3. **Test Endpoints**: Use Swagger UI or testing tools for API validation
4. **Monitor Logs**: Enable DEBUG logging for detailed operation tracking

## ❗ Common Issues

### Issue: "Room not found" Error

**Cause**: Using invalid room ID or user not a member
**Solution**:

- Check room ID with `/api/rooms/all`
- Verify user membership with `/api/rooms/membership-summary`

### Issue: File Upload Fails

**Cause**: File size limits or incorrect multipart handling
**Solution**:

- Check file size (limit: 10MB)
- Use `/api/test/upload` to isolate the issue
- Verify `uploads/` directory permissions

### Issue: Database Connection Error

**Cause**: Database file permissions or corruption
**Solution**:

- Check `./data/` directory permissions
- Delete `./data/` folder to reset database
- Restart application to recreate test data

### Issue: "User is not a member of this room"

**Cause**: User trying to access unauthorized room
**Solution**:

- Check user's room memberships with `/api/rooms?userId=<userId>`
- Use `default-user` which has access to multiple rooms

## 👨‍💻 Development Guidelines

### Adding New Features

1. **Models**: Create/modify JPA entities in `model/` package
2. **Repositories**: Add data access methods in `repository/` interfaces
3. **Services**: Implement business logic in `service/` classes
4. **Controllers**: Add REST endpoints in `controller/` classes
5. **Testing**: Create corresponding test forms in `static/` folder

### Code Standards

- Use descriptive variable and method names
- Add proper JavaDoc for public methods
- Include error handling with meaningful messages
- Log important operations for debugging
- Follow Spring Boot best practices

### Testing New Endpoints

1. Add endpoint to Swagger documentation
2. Create corresponding test in web interface
3. Test with different user scenarios
4. Verify database state changes
5. Check error handling edge cases

### Database Schema Changes

- Use `spring.jpa.hibernate.ddl-auto=update` for development
- Test schema changes with fresh database
- Document any breaking changes

## 🔧 Configuration

Key configuration options in `application.properties`:

```properties
# Server
server.port=8080

# Database (change to mem: for non-persistent testing)
spring.datasource.url=jdbc:h2:file:./data/chatdb

# File uploads
spring.servlet.multipart.max-file-size=10MB
file.upload-dir=./uploads

# Logging
logging.level.com.chatmessage.chat=DEBUG
```

## 📞 Support

For debugging help:

1. Check this README for common solutions
2. Review application logs for error details
3. Use the built-in testing tools to isolate issues
4. Inspect database state via H2 console
5. Test individual components using the test endpoints

---

_Happy coding! 🚀_
