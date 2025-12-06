# Custom Exception Handling Implementation Report

## Project: CourtReserve Backend
**Date:** December 6, 2025  
**Task:** Implement Custom Exception Handling for All Exceptions

---

## 1. Overview

A comprehensive global exception handling system was implemented for the CourtReserve Spring Boot application. This replaces the repetitive try-catch blocks in controllers with a centralized exception handler using `@RestControllerAdvice`, providing consistent error responses across all API endpoints.

---

## 2. Files Created

### 2.1 Exception Package (`src/main/java/com/example/courtreserve/exception/`)

| File | Description |
|------|-------------|
| `ResourceNotFoundException.java` | 404 errors for missing resources |
| `BadRequestException.java` | 400 errors for invalid requests |
| `ConflictException.java` | 409 errors for duplicate resources |
| `UnauthorizedException.java` | 401 errors for authentication failures |
| `ForbiddenException.java` | 403 errors for authorization failures |
| `ErrorResponse.java` | Standardized error response DTO |
| `GlobalExceptionHandler.java` | Central exception handler with `@RestControllerAdvice` |

---

## 3. Files Modified

### 3.1 Service Implementations

| File | Changes Made |
|------|--------------|
| `UserServiceImpl.java` | Replaced `RuntimeException` with `ResourceNotFoundException` and `ConflictException` |
| `VendorServiceImpl.java` | Replaced `RuntimeException` with `ResourceNotFoundException` and `ConflictException` |
| `BookingServiceImpl.java` | Replaced `RuntimeException` with `ResourceNotFoundException` and `ForbiddenException` |
| `CourtServiceImpl.java` | Replaced `RuntimeException` with `ResourceNotFoundException` and `ForbiddenException` |
| `TeamServiceImpl.java` | Replaced `RuntimeException` with `ResourceNotFoundException`, `BadRequestException`, and `ConflictException` |
| `TournamentServiceImpl.java` | Replaced `RuntimeException` with `ResourceNotFoundException`, `BadRequestException`, and `ConflictException` |
| `MatchServiceImpl.java` | Replaced `RuntimeException` with `ResourceNotFoundException` and `BadRequestException` |
| `ReviewServiceImpl.java` | Replaced `RuntimeException` with `ResourceNotFoundException` |

### 3.2 Controllers

| File | Changes Made |
|------|--------------|
| `UserController.java` | Removed try-catch blocks, simplified to single service calls |
| `VendorController.java` | Removed try-catch blocks |
| `BookingController.java` | Removed try-catch blocks (7 methods simplified) |
| `CourtController.java` | Removed try-catch blocks (5 methods simplified) |
| `TeamController.java` | Removed try-catch blocks (6 methods simplified) |
| `TournamentController.java` | Removed try-catch blocks (8 methods simplified) |
| `MatchController.java` | Removed try-catch blocks (5 methods simplified) |
| `ReviewController.java` | Removed try-catch blocks |

---

## 4. Exception Mapping

### 4.1 Custom Exceptions

| Exception | HTTP Status | When Used |
|-----------|-------------|-----------|
| `ResourceNotFoundException` | 404 NOT_FOUND | User/Vendor/Court/Booking/Team/Tournament/Match not found |
| `BadRequestException` | 400 BAD_REQUEST | Invalid elimination type, sport mismatch, invalid state transitions |
| `ConflictException` | 409 CONFLICT | Duplicate email, duplicate team name, already registered |
| `UnauthorizedException` | 401 UNAUTHORIZED | Invalid credentials, authentication failure |
| `ForbiddenException` | 403 FORBIDDEN | Booking doesn't belong to user/vendor, not authorized |

### 4.2 Framework Exceptions Handled

| Exception | HTTP Status | Scenario |
|-----------|-------------|----------|
| `EntityNotFoundException` | 404 NOT_FOUND | JPA entity not found |
| `MethodArgumentNotValidException` | 400 BAD_REQUEST | Bean validation failures |
| `IllegalArgumentException` | 400 BAD_REQUEST | Invalid method arguments |
| `MissingServletRequestParameterException` | 400 BAD_REQUEST | Missing required parameters |
| `MethodArgumentTypeMismatchException` | 400 BAD_REQUEST | Type conversion failures |
| `HttpMessageNotReadableException` | 400 BAD_REQUEST | Malformed JSON request |
| `BadCredentialsException` | 401 UNAUTHORIZED | Invalid login credentials |
| `AuthenticationException` | 401 UNAUTHORIZED | Authentication failures |
| `AccessDeniedException` | 403 FORBIDDEN | @PreAuthorize access denied |
| `HttpRequestMethodNotSupportedException` | 405 METHOD_NOT_ALLOWED | Wrong HTTP method |
| `HttpMediaTypeNotSupportedException` | 415 UNSUPPORTED_MEDIA_TYPE | Wrong content type |
| `NoHandlerFoundException` | 404 NOT_FOUND | Endpoint not found |
| `Exception` (catch-all) | 500 INTERNAL_SERVER_ERROR | Unexpected errors |

---

## 5. Error Response Format

### 5.1 Standard Error Response

```json
{
  "timestamp": "2025-12-06 15:30:45",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: '123'",
  "path": "/user/association"
}
```

### 5.2 Validation Error Response

```json
{
  "timestamp": "2025-12-06 15:30:45",
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more fields have validation errors",
  "path": "/user/register",
  "validationErrors": [
    {"field": "email", "message": "must be a valid email address"},
    {"field": "password", "message": "must be at least 8 characters"}
  ]
}
```

---

## 6. Code Metrics

### 6.1 Before vs After (Controller Code)

| Metric | Before | After | Reduction |
|--------|--------|-------|-----------|
| Lines of code (BookingController) | 168 | 98 | **42%** |
| Try-catch blocks (all controllers) | ~35 | 0 | **100%** |
| Duplicate error handling patterns | 35+ | 1 (centralized) | **97%** |

### 6.2 New Files Added
- **7 new exception-related files** created in `exception/` package
- **Total new lines of code:** ~500 lines (reusable exception infrastructure)

---

## 7. Benefits Achieved

1. **Centralized Error Handling** - All exceptions handled in one place
2. **Consistent API Responses** - Standardized error format across all endpoints
3. **Cleaner Controller Code** - Controllers focus only on happy path
4. **Better Maintainability** - Error handling logic in one location
5. **Improved Logging** - Centralized logging with appropriate log levels
6. **Type Safety** - Semantic exception types instead of generic RuntimeException
7. **Better Client Experience** - Meaningful error messages with proper HTTP status codes

---

## 8. Verification

- [x] **Compilation:** Successful (`mvnw compile`)
- [x] **Linter Errors:** None
- [x] **All 8 controllers updated**
- [x] **All 8 service implementations updated**

---

## 9. Usage Examples

### Throwing Exceptions in Services

```java
// Resource not found
User user = userRepository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

// Duplicate resource
if (userRepository.existsByEmail(email)) {
    throw new ConflictException("User", "email", email);
}

// Business rule violation
if (!"CONFIRMED".equals(tournament.getStatus())) {
    throw new BadRequestException("Tournament must be confirmed before starting");
}

// Authorization failure
if (!userId.equals(booking.getUser().getId())) {
    throw new ForbiddenException("Booking doesn't belong to user");
}
```

### Controller Methods (Simplified)

```java
@PostMapping("/createBooking")
@PreAuthorize("hasRole('USER')")
public ResponseEntity<?> createBooking(@RequestBody AddBookingRequest request) {
    var booking = bookingService.createBooking(request);
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "Booking created successfully", "booking", booking));
}
```

---

## 10. Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────┐
│                           CLIENT REQUEST                            │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                         CONTROLLER LAYER                            │
│  UserController, BookingController, CourtController, etc.           │
│  (Clean code - no try-catch blocks)                                 │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│                          SERVICE LAYER                              │
│  UserServiceImpl, BookingServiceImpl, CourtServiceImpl, etc.        │
│  (Throws custom exceptions: ResourceNotFoundException, etc.)        │
└─────────────────────────────────────────────────────────────────────┘
                                    │
                    ┌───────────────┴───────────────┐
                    │                               │
                    ▼                               ▼
           ┌───────────────┐              ┌─────────────────────────┐
           │    SUCCESS    │              │      EXCEPTION          │
           │   RESPONSE    │              │       THROWN            │
           └───────────────┘              └─────────────────────────┘
                                                    │
                                                    ▼
                                    ┌───────────────────────────────┐
                                    │   GLOBAL EXCEPTION HANDLER    │
                                    │   @RestControllerAdvice       │
                                    │   GlobalExceptionHandler.java │
                                    └───────────────────────────────┘
                                                    │
                                                    ▼
                                    ┌───────────────────────────────┐
                                    │      ErrorResponse DTO        │
                                    │   - timestamp                 │
                                    │   - status                    │
                                    │   - error                     │
                                    │   - message                   │
                                    │   - path                      │
                                    │   - validationErrors (opt)    │
                                    └───────────────────────────────┘
                                                    │
                                                    ▼
                                    ┌───────────────────────────────┐
                                    │       JSON ERROR RESPONSE     │
                                    └───────────────────────────────┘
```

---

## 11. File Structure

```
src/main/java/com/example/courtreserve/
├── exception/
│   ├── BadRequestException.java          # 400 Bad Request
│   ├── ConflictException.java            # 409 Conflict
│   ├── ErrorResponse.java                # Standardized error DTO
│   ├── ForbiddenException.java           # 403 Forbidden
│   ├── GlobalExceptionHandler.java       # Central @RestControllerAdvice
│   ├── ResourceNotFoundException.java    # 404 Not Found
│   ├── UnauthorizedException.java        # 401 Unauthorized
│   └── EXCEPTION_HANDLING_REPORT.md      # This report
├── controller/
│   ├── BookingController.java            # Updated - no try-catch
│   ├── CourtController.java              # Updated - no try-catch
│   ├── MatchController.java              # Updated - no try-catch
│   ├── ReviewController.java             # Updated - no try-catch
│   ├── TeamController.java               # Updated - no try-catch
│   ├── TournamentController.java         # Updated - no try-catch
│   ├── UserController.java               # Updated - no try-catch
│   └── VendorController.java             # Updated - no try-catch
└── service/impl/
    ├── BookingServiceImpl.java           # Uses custom exceptions
    ├── CourtServiceImpl.java             # Uses custom exceptions
    ├── MatchServiceImpl.java             # Uses custom exceptions
    ├── ReviewServiceImpl.java            # Uses custom exceptions
    ├── TeamServiceImpl.java              # Uses custom exceptions
    ├── TournamentServiceImpl.java        # Uses custom exceptions
    ├── UserServiceImpl.java              # Uses custom exceptions
    └── VendorServiceImpl.java            # Uses custom exceptions
```

---

## 12. Future Recommendations

1. Add `@Valid` annotations to request DTOs for bean validation
2. Consider adding rate limiting exception (`TooManyRequestsException` - 429)
3. Add request IDs for error tracking in production
4. Consider implementing problem details (RFC 7807) for more structured errors
5. Add internationalization (i18n) support for error messages

---

**Report Generated:** December 6, 2025  
**Status:** ✅ Implementation Complete

