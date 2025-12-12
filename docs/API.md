# MeetMate API Documentation

Base URL: `http://localhost:8080/api`

## Authentication

All authenticated endpoints require a Bearer token in the Authorization header:
```
Authorization: Bearer <access_token>
```

### Register
**POST** `/auth/register`

Request:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

Response (201):
```json
{
  "user": {
    "id": "uuid",
    "name": "John Doe",
    "email": "john@example.com"
  },
  "accessToken": "jwt-token",
  "refreshToken": "refresh-token"
}
```

### Login
**POST** `/auth/login`

Request:
```json
{
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

Response (200):
```json
{
  "user": {
    "id": "uuid",
    "name": "John Doe",
    "email": "john@example.com"
  },
  "accessToken": "jwt-token",
  "refreshToken": "refresh-token"
}
```

### Refresh Token
**POST** `/auth/refresh`

Request:
```json
{
  "refreshToken": "refresh-token"
}
```

Response (200):
```json
{
  "user": {
    "id": "uuid",
    "name": "John Doe",
    "email": "john@example.com"
  },
  "accessToken": "new-jwt-token",
  "refreshToken": "new-refresh-token"
}
```

## User Management

### Get Profile
**GET** `/users/me`

Response (200):
```json
{
  "id": "uuid",
  "name": "John Doe",
  "email": "john@example.com",
  "defaultLocation": "Ahmedabad, Gujarat",
  "defaultLat": 23.0225,
  "defaultLng": 72.5714
}
```

### Update Profile
**PUT** `/users/me`

Request:
```json
{
  "name": "John Smith",
  "defaultLocation": "Mumbai, Maharashtra",
  "defaultLat": 19.0760,
  "defaultLng": 72.8777
}
```

Response (200):
```json
{
  "id": "uuid",
  "name": "John Smith",
  "email": "john@example.com",
  "defaultLocation": "Mumbai, Maharashtra",
  "defaultLat": 19.0760,
  "defaultLng": 72.8777
}
```

## Group Management

### Create Group
**POST** `/groups`

Request:
```json
{
  "name": "Weekend Squad"
}
```

Response (201):
```json
{
  "id": "uuid",
  "name": "Weekend Squad",
  "ownerId": "uuid",
  "createdAt": "2024-01-01T10:00:00Z",
  "members": [
    {
      "userId": "uuid",
      "name": "John Doe",
      "nickname": null,
      "isActive": true,
      "joinedAt": "2024-01-01T10:00:00Z"
    }
  ]
}
```

### List Groups
**GET** `/groups`

Response (200):
```json
[
  {
    "id": "uuid",
    "name": "Weekend Squad",
    "ownerId": "uuid",
    "createdAt": "2024-01-01T10:00:00Z",
    "members": [...]
  }
]
```

### Get Group
**GET** `/groups/{id}`

Response (200):
```json
{
  "id": "uuid",
  "name": "Weekend Squad",
  "ownerId": "uuid",
  "createdAt": "2024-01-01T10:00:00Z",
  "members": [
    {
      "userId": "uuid",
      "name": "John Doe",
      "nickname": "Johnny",
      "isActive": true,
      "joinedAt": "2024-01-01T10:00:00Z"
    }
  ]
}
```

### Update Group
**PUT** `/groups/{id}`

Request:
```json
{
  "name": "Updated Squad Name"
}
```

Response (200): Group object

### Delete Group
**DELETE** `/groups/{id}`

Response (204): No Content

### Add Member
**POST** `/groups/{id}/members`

Request:
```json
{
  "email": "friend@example.com",
  "nickname": "Buddy"
}
```

Response (200): Group object

### Remove Member
**DELETE** `/groups/{groupId}/members/{userId}`

Response (204): No Content

## Preferences

### Update Preferences
**POST** `/groups/{groupId}/preferences`

Request:
```json
{
  "currentLocation": "Maninagar, Ahmedabad",
  "currentLat": 22.9908,
  "currentLng": 72.6197,
  "transportModes": ["CAR", "METRO"],
  "maxDistanceKm": 25,
  "travelWillingness": "MEDIUM",
  "budgetLevel": "MEDIUM",
  "foodPreference": "VEG_FRIENDLY"
}
```

Response (200):
```json
{
  "userId": "uuid",
  "groupId": "uuid",
  "currentLocation": "Maninagar, Ahmedabad",
  "currentLat": 22.9908,
  "currentLng": 72.6197,
  "transportModes": ["CAR", "METRO"],
  "maxDistanceKm": 25,
  "travelWillingness": "MEDIUM",
  "budgetLevel": "MEDIUM",
  "foodPreference": "VEG_FRIENDLY",
  "updatedAt": "2024-01-01T10:00:00Z"
}
```

### Get Group Preferences
**GET** `/groups/{groupId}/preferences`

Response (200):
```json
[
  {
    "userId": "uuid",
    "groupId": "uuid",
    "currentLocation": "Maninagar, Ahmedabad",
    "currentLat": 22.9908,
    "currentLng": 72.6197,
    "transportModes": ["CAR", "METRO"],
    "maxDistanceKm": 25,
    "travelWillingness": "MEDIUM",
    "budgetLevel": "MEDIUM",
    "foodPreference": "VEG_FRIENDLY",
    "updatedAt": "2024-01-01T10:00:00Z"
  }
]
```

### Get Member Preference
**GET** `/groups/{groupId}/preferences/{userId}`

Response (200): MemberPreference object

## Places

### Create Place
**POST** `/places`

Request:
```json
{
  "name": "Pizza Palace",
  "category": "RESTAURANT",
  "area": "Maninagar",
  "address": "123 Main St, Maninagar, Ahmedabad",
  "lat": 22.9908,
  "lng": 72.6197,
  "costLevel": "MEDIUM",
  "hasVeg": true,
  "hasNonVeg": true,
  "rating": 4.2
}
```

Response (201): Place object

### List Places
**GET** `/places`

Query parameters:
- `category` (optional): RESTAURANT, CAFE, FOOD_COURT, MALL, ENTERTAINMENT
- `area` (optional): Location area
- `costLevel` (optional): LOW, MEDIUM, HIGH

Response (200):
```json
[
  {
    "id": "uuid",
    "name": "Pizza Palace",
    "category": "RESTAURANT",
    "area": "Maninagar",
    "address": "123 Main St, Maninagar, Ahmedabad",
    "lat": 22.9908,
    "lng": 72.6197,
    "costLevel": "MEDIUM",
    "hasVeg": true,
    "hasNonVeg": true,
    "rating": 4.2,
    "isActive": true,
    "createdAt": "2024-01-01T10:00:00Z"
  }
]
```

### Get Place
**GET** `/places/{id}`

Response (200): Place object

### Update Place
**PUT** `/places/{id}`

Request: Place update object

Response (200): Place object

### Delete Place
**DELETE** `/places/{id}`

Response (204): No Content

## Recommendations

### Generate Recommendations
**POST** `/recommendations/generate`

Request:
```json
{
  "groupId": "uuid",
  "memberIds": ["uuid1", "uuid2"],
  "maxResults": 5
}
```

Response (200):
```json
[
  {
    "place": {
      "id": "uuid",
      "name": "Pizza Palace",
      "category": "RESTAURANT",
      "area": "Maninagar",
      "lat": 22.9908,
      "lng": 72.6197,
      "costLevel": "MEDIUM",
      "rating": 4.2
    },
    "score": 85.5,
    "avgDistanceKm": 12.3,
    "maxDistanceKm": 15.7,
    "reasoning": "Great balance of distance fairness and budget compatibility. All members within 16km range."
  }
]
```

## Error Responses

All error responses follow this format:

```json
{
  "timestamp": "2024-01-01T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/groups"
}
```

Common HTTP status codes:
- 400: Bad Request (validation errors)
- 401: Unauthorized (invalid/missing JWT)
- 403: Forbidden (insufficient permissions)
- 404: Not Found
- 409: Conflict (user already exists, etc.)
- 500: Internal Server Error
