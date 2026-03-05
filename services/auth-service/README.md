
#  Auth Service – Enterprise JWT Authentication Microservice

This service handles **authentication, authorization, OTP verification, token lifecycle management, and secure logout** for the Ecommerce Platform.

It follows:

* ✅ JWT Access Token (short-lived)
* ✅ Opaque Refresh Token (stored as SHA-256 hash)
* ✅ OTP Verification (Redis-based)
* ✅ BCrypt password hashing
* ✅ Role-Based Access Control (RBAC)
* ✅ Secure logout with refresh token revocation

---

# Tech Stack

* Spring Boot
* Spring Security
* JWT
* Redis (OTP + TTL support)
* PostgreSQL (Credential + Refresh Token persistence)
* BCrypt Password Encoder

---

#  Architecture Overview

```
Client
   ↓
API Gateway
   ↓
Auth Service
   ↓
PostgreSQL (Credentials + Refresh Tokens)
   ↓
Redis (OTP Store with TTL)
   ↓
Notification Service (SMS/Email OTP)
```

---

#  Exposed APIs

Base URL:

```
/api/v1/auth
```

---

## 1️ Register

### POST `/register`

Registers a new user and sends OTP.

```json
{
  "email": "user@example.com",
  "password": "Strong@123",
  "phone": "9876543210"
}
```

### Process:

* Password hashed using BCrypt
* Credentials saved in DB
* OTP generated & stored in Redis (10 min TTL)
* OTP sent via notification-service

### Response

`201 Created`

```json
{
  "message": "OTP sent successfully"
}
```

---

## 2️ Verify OTP (After Registration)

Verifies OTP and activates account.

### POST `/verify-otp`

```json
{
  "email": "user@example.com",
  "otp": "123456"
}
```

### Process:

* Redis validates OTP
* Account marked verified
* Access Token generated (15 mins)
* Refresh Token generated (30 days)
* Refresh Token stored as SHA-256 hash in DB

---

## 3️ Login

### POST `/login`

```json
{
  "email": "user@example.com",
  "password": "Strong@123"
}
```

### Process:

* bcrypt.matches(password, storedHash)
* Generate:

    * Access Token (JWT, 15 mins)
    * Refresh Token (UUID, 30 days)
* Refresh token stored hashed in DB

### Response

```json
{
  "accessToken": "jwt-token",
  "refreshToken": "opaque-token",
  "expiresIn": 900
}
```

---

## 4️ Refresh Access Token

### POST `/refresh`

```json
{
  "refreshToken": "stored-refresh-token"
}
```

### Process:

1. Incoming token hashed using SHA-256
2. DB lookup by hash
3. Check:

    * Not expired
    * Not revoked
4. Old refresh token revoked
5. New access + refresh token generated
6. New refresh token stored in DB

---

## 5️ Logout

### POST `/logout`

Requires valid access token.

### Process:

* Extract userId from JWT
* Delete all refresh tokens for userId
* Client clears tokens
* Future refresh attempts fail

---

#  Complete Token Flow (End-to-End)

```
REGISTRATION
────────────────────────────────────────
Client → POST /register
      → DB stores bcrypt hash
      → Redis stores OTP (10 min TTL)
      → Notification-service sends OTP

Client → POST /verify-otp
      → Redis validates
      → Access Token (15 min)
      → Refresh Token (30 days)
      → Refresh stored hashed in DB
      → Both returned
```

---

```
LOGIN
────────────────────────────────────────
Client → POST /login
      → bcrypt.matches()
      → Access + Refresh generated
      → Refresh stored hashed
```

---

```
AUTHENTICATED REQUEST
────────────────────────────────────────
Client → GET /api/v1/orders
Header: Authorization: Bearer <accessToken>

JwtAuthenticationFilter:
    → Validate signature
    → Validate expiry
    → Extract userId + role
    → Set SecurityContext
```

---

```
ACCESS TOKEN EXPIRED
────────────────────────────────────────
Client → API → 401 Unauthorized
Client → POST /refresh
      → Hash incoming token
      → DB lookup
      → Revoke old refresh
      → Issue new access + refresh
      → Return tokens
```

---

```
LOGOUT
────────────────────────────────────────
Client → POST /logout
      → Delete all refresh tokens for userId
      → Client clears storage
      → Next refresh attempt fails
```

---

#  Security Design Decisions

| Feature          | Implementation          |
| ---------------- | ----------------------- |
| Password Storage | BCrypt                  |
| Access Token     | JWT (15 min)            |
| Refresh Token    | Opaque UUID             |
| Refresh Storage  | SHA-256 hashed          |
| OTP Storage      | Redis (TTL 10 min)      |
| Token Revocation | DB flag + deletion      |
| Account Lock     | After 5 failed attempts |


---

# Configuration Example

```yaml
jwt:
  secret: your-secret-key
  access-token-expiration: 900
  refresh-token-expiration: 2592000

redis:
  host: localhost
  port: 6379
```

---

#  Production Enhancements

* Add MFA (TOTP)
* Add device tracking
* Add IP anomaly detection
* Add rate limiting
* Add brute-force protection
* Add audit logging
* Add distributed tracing

---

#  Why This Design is Enterprise-Level?

* Refresh tokens stored hashed (zero leakage risk)
* Stateless access tokens
* Rotating refresh tokens
* Redis-based OTP
* Proper logout revocation
* Role-based security integration
* API Gateway compatible

---

#  Future Improvements

* OAuth2 Social Login
* SSO support
* WebAuthn
* Key rotation strategy
* Blacklist cache for JWT

---

#  Summary

This Auth Service provides:

* Secure authentication
* Robust token lifecycle management
* Enterprise-grade refresh rotation
* OTP-based verification
* Secure logout revocation
* Production-ready architecture

---
