# GoDesii Platform - Complete API Documentation

> **Comprehensive Technical Reference **  
> Version: 1.0 | Last Updated: January 23, 2026

## Table of Contents

1. [Overview](#overview)
2. [Base Configuration](#base-configuration)
3. [**Authentication APIs**](#authentication-apis)
4. [**Restaurant Management APIs**](#restaurant-management-apis)
5. [**Menu & Items APIs**](#menu--items-apis)
6. [**Order Management APIs**](#order-management-apis)
7. [**Cart Management APIs**](#cart-management-apis)
8. [Common Response Format](#common-response-format)
9. [Error Handling](#error-handling)
10. [Postman Collection Setup](#postman-collection-setup)

---

## Overview

The GoDesii platform consists of **15 REST API controllers** across **3 main modules**:

| Module | Controllers | Entities |
|--------|-------------|----------|
| **Authentication** | 4 controllers | User, UserProfile, ShippingAddress, Auth |
| **Restaurant** | 7 controllers | Restaurant, Menu, MenuItem, Category, Review, FoodCertificate, NutritionalInfo |
| **Order** | 4 controllers | Cart, CartItem, Order, OrderItem |

**Total Endpoints:** 50+ RESTful APIs

---

## Base Configuration

### API Base URL
```
Base URL: http://localhost:8080
API Version: /api/v1
```

### Common Headers
```http
Content-Type: application/json
Accept: application/json
Authorization: Bearer {token}  (for protected endpoints)
```

### Cart Configuration (`CartConfig.java`)

| Setting | Value | Description |
|---------|-------|-------------|
| Cart Expiry | 30 minutes | Auto-expiration after last update |
| Packaging Charge | ₹5 per item | Per unique item |
| GST Rate | 5% | On item total |
| Platform Fee | ₹2 | Flat fee |
| Delivery Fee | ₹40 | Currently fixed |

---

## Authentication APIs

### AuthController
**Base Path:** `/auth`

#### 1. Login
**Endpoint:** `POST /auth/login`

**Description:** Authenticate user and get access token

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Success Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 123,
  "email": "user@example.com",
  "expiresIn": 3600
}
```

**Test Payloads:**

**Valid Login:**
```json
{
  "email": "john.doe@godesii.com",
  "password": "Test@1234"
}
```

**Invalid Credentials:**
```json
{
  "email": "invalid@example.com",
  "password": "wrongPassword"
}
```

---

## Restaurant Management APIs

### RestaurantController
**Base Path:** `/api/v1/restaurants`

#### 1. Create Restaurant
**Endpoint:** `POST /api/v1/restaurants/create`

**Request Body:**
```json
{
  "name": "Taste of India",
  "phoneNo": "+917812345678",
  "cuisineType": "North Indian, Chinese",
  "description": "Authentic Indian cuisine with modern twist",
  "isVerified": false,
  "operationalHourRequest": [
    {
      "dayOfWeek": 1,
      "openTime": "10:00",
      "closeTime": "23:00",
      "serviceType": "DELIVERY"
    },
    {
      "dayOfWeek": 2,
      "openTime": "10:00",
      "closeTime": "23:00",
      "serviceType": "DELIVERY"
    }
  ],
  "addressRequest": {
    "addressLine1": "123, MG Road",
    "addressLine2": "Near City Mall",
    "city": "Bangalore",
    "state": "Karnataka",
    "postalCode": "560001",
    "country": "India",
    "latitude": 12.9716,
    "longitude": 77.5946
  }
}
```

**Field Validations:**
- `name`: Min 2, Max 100 characters
- `phoneNo`: Valid phone number format
- `cuisineType`: Min 2, Max 50 characters
- `description`: Max 500 characters
- `dayOfWeek`: 0=Sunday, 1=Monday, ..., 6=Saturday
- `serviceType`: DINE_IN, DELIVERY, BOTH

**Success Response (201 Created):**
```json
{
  "status": "CREATED",
  "data": {
    "id": 1,
    "name": "Taste of India",
    "phoneNo": "+917812345678",
    "cuisineType": "North Indian, Chinese",
    "description": "Authentic Indian cuisine with modern twist",
    "isVerified": false,
    "isActive": true,
    "operatingHours": [...],
    "address": {...}
  },
  "message": "Successfully created"
}
```

**Test Scenarios:**

**Scenario 1: Full Restaurant with Operating Hours**
```json
{
  "name": "Biryani House",
  "phoneNo": "+919876543210",
  "cuisineType": "Mughlai, Hyderabadi",
  "description": "Best biryani in town",
  "isVerified": true,
  "operationalHourRequest": [
    {
      "dayOfWeek": 0,
      "openTime": "11:00",
      "closeTime": "22:00",
      "serviceType": "BOTH"
    }
  ],
  "addressRequest": {
    "addressLine1": "45, Brigade Road",
    "city": "Bangalore",
    "state": "Karnataka",
    "postalCode": "560025",
    "country": "India",
    "latitude": 12.9698,
    "longitude": 77.6055
  }
}
```

**Scenario 2: Overnight Operating Hours (22:00 - 02:00)**
```json
{
  "name": "Late Night Cafe",
  "phoneNo": "+918888888888",
  "cuisineType": "Continental, Fast Food",
  "description": "Open late for night owls",
  "isVerified": false,
  "operationalHourRequest": [
    {
      "dayOfWeek": 5,
      "openTime": "22:00",
      "closeTime": "02:00",
      "serviceType": "DELIVERY"
    }
  ],
  "addressRequest": {
    "addressLine1": "Plot 88, Indiranagar",
    "city": "Bangalore",
    "state": "Karnataka",
    "postalCode": "560038",
    "country": "India"
  }
}
```

#### 2. Get All Restaurants (Paginated)
**Endpoint:** `GET /api/v1/restaurants?page=0&size=10&sortBy=id&direction=asc`

**Query Parameters:**
- `page`: Page number (default: 0)
- `size`: Page size (default: 10)
- `sortBy`: Sort field (default: id)
- `direction`: asc/desc (default: asc)

**Success Response (200 OK):**
```json
{
  "status": "OK",
  "data": {
    "content": [
      {
        "id": 1,
        "name": "Taste of India",
        "cuisineType": "North Indian",
        "isActive": true,
        "isVerified": false
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 25,
    "totalPages": 3,
    "first": true,
    "last": false
  },
  "message": "Successfully fetched"
}
```

#### 3. Get Restaurant by ID
**Endpoint:** `GET /api/v1/restaurants/{id}`

**Example:** `GET /api/v1/restaurants/1`

#### 4. Update Restaurant
**Endpoint:** `PUT /api/v1/restaurants/{id}`

**Request Body:** (Partial updates supported)
```json
{
  "name": "Taste of India - Updated",
  "description": "New description",
  "isVerified": true
}
```

#### 5. Delete Restaurant
**Endpoint:** `DELETE /api/v1/restaurants/{id}`

**Success Response (204 No Content):**
```json
{
  "status": "NO_CONTENT",
  "data": null,
  "message": "Successfully deleted"
}
```

---

## Menu & Items APIs

### MenuItemController
**Base Path:** `/api/v1/menu-items`

#### 1. Create Menu Item
**Endpoint:** `POST /api/v1/menu-items`

**Request Body:**
```json
{
  "name": "Butter Chicken",
  "description": "Creamy tomato-based curry with tender chicken pieces",
  "basePrice": 350.00,
  "imageUrl": "https://cdn.godesii.com/items/butter-chicken.jpg",
  "isAvailable": true,
  "dietaryType": "NON_VEG",
  "categoryId": 5
}
```

**Field Validations:**
- `name`: Required, Min 2, Max 100 characters
- `description`: Max 1000 characters
- `basePrice`: Required, Min 0.01
- `imageUrl`: Valid URL format
- `isAvailable`: Boolean (default: true)
- `dietaryType`: VEG, NON_VEG, EGG, VEGAN
- `categoryId`: Required

**Success Response (201 Created):**
```json
{
  "status": "CREATED",
  "data": {
    "itemId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
    "name": "Butter Chicken",
    "description": "Creamy tomato-based curry with tender chicken pieces",
    "basePrice": 350.00,
    "imageUrl": "https://cdn.godesii.com/items/butter-chicken.jpg",
    "isAvailable": true, "dietaryType": "NON_VEG",
    "category": {
      "id": 5,
      "name": "Main Course"
    }
  },
  "message": "Successfully created"
}
```

**Test Scenarios:**

**Vegetarian Item:**
```json
{
  "name": "Paneer Tikka Masala",
  "description": "Cottage cheese cubes in spicy tomato gravy",
  "basePrice": 280.00,
  "imageUrl": "https://cdn.godesii.com/items/paneer-tikka.jpg",
  "isAvailable": true,
  "dietaryType": "VEG",
  "categoryId": 5
}
```

**Vegan Item:**
```json
{
  "name": "Dal Tadka",
  "description": "Yellow lentils tempered with spices",
  "basePrice": 180.00,
  "imageUrl": "https://cdn.godesii.com/items/dal-tadka.jpg",
  "isAvailable": true,
  "dietaryType": "VEGAN",
  "categoryId": 5
}
```

**Unavailable Item:**
```json
{
  "name": "Special Biryani",
  "description": "Chef's special biryani (temporarily unavailable)",
  "basePrice": 420.00,
  "isAvailable": false,
  "dietaryType": "NON_VEG",
  "categoryId": 6
}
```

#### 2. Get All Menu Items
**Endpoint:** `GET /api/v1/menu-items`

#### 3. Get Menu Item by ID
**Endpoint:** `GET /api/v1/menu-items/{itemId}`

**Example:** `GET /api/v1/menu-items/a1b2c3d4-e5f6-7890-abcd-ef1234567890`

#### 4. Get Menu Items by Category
**Endpoint:** `GET /api/v1/menu-items/category/{categoryId}`

**Example:** `GET /api/v1/menu-items/category/5`

**Success Response:**
```json
{
  "status": "OK",
  "data": [
    {
      "itemId": "uuid-1",
      "name": "Butter Chicken",
      "basePrice": 350.00,
      "dietaryType": "NON_VEG",
      "isAvailable": true
    },
    {
      "itemId": "uuid-2",
      "name": "Paneer Tikka",
      "basePrice": 280.00,
      "dietaryType": "VEG",
      "isAvailable": true
    }
  ],
  "message": "Successfully fetched"
}
```

#### 5. Update Menu Item
**Endpoint:** `PUT /api/v1/menu-items/{itemId}`

**Request Body:** (Partial update)
```json
{
  "basePrice": 380.00,
  "isAvailable": true
}
```

#### 6. Delete Menu Item
**Endpoint:** `DELETE /api/v1/menu-items/{itemId}`

---

## Order Management APIs

### OrderController
**Base Path:** `/api/v1/orders`

#### 1. Create Order
**Endpoint:** `POST /api/v1/orders/create`

**Request Body:**
```json
{
  "userId": "user-uuid-123",
  "restaurantId": "restaurant-uuid-456",
  "orderStatus": "PENDING",
  "orderItems": [
    {
      "productId": "item-uuid-1",
      "quantity": 2,
      "priceAtPurchase": 350
    },
    {
      "productId": "item-uuid-2",
      "quantity": 1,
      "priceAtPurchase": 180
    }
  ],
  "orderAddress": {
    "addressLine1": "Flat 301, Park View Apartments",
    "addressLine2": "Whitefield",
    "city": "Bangalore",
    "state": "Karnataka",
    "postalCode": "560066",
    "country": "India"
  }
}
```

**Field Validations:**
- `userId`: Required, not blank
- `restaurantId`: Required, not blank
- `orderStatus`: Max 50 characters
- `orderItems`: Required, at least one item
- `priceAtPurchase`: Price at time of order (in paise)

**Success Response (201 Created):**
```json
{
  "status": "CREATED",
  "data": {
    "id": "order-uuid-789",
    "userId": "user-uuid-123",
    "restaurantId": "restaurant-uuid-456",
    "orderStatus": "PENDING",
    "orderDate": "2026-01-23T17:00:00Z",
    "totalAmount": 880,
    "orderItems": [...],
    "orderAddress": {...}
  },
  "message": "Successfully created"
}
```

**Test Scenarios:**

**Multi-Item Order:**
```json
{
  "userId": "user-1001",
  "restaurantId": "rest-5001",
  "orderStatus": "CONFIRMED",
  "orderItems": [
    {
      "productId": "butter-chicken-uuid",
      "quantity": 2,
      "priceAtPurchase": 35000
    },
    {
      "productId": "garlic-naan-uuid",
      "quantity": 4,
      "priceAtPurchase": 5000
    },
    {
      "productId": "dal-makhani-uuid",
      "quantity": 1,
      "priceAtPurchase": 22000
    }
  ],
  "orderAddress": {
    "addressLine1": "House 42, Sector 7",
    "city": "Gurgaon",
    "state": "Haryana",
    "postalCode": "122001",
    "country": "India"
  }
}
```

#### 2. Get All Orders (Paginated)
**Endpoint:** `GET /api/v1/orders?page=0&size=10&sortBy=orderDate&direction=desc`

#### 3. Get Order by ID
**Endpoint:** `GET /api/v1/orders/{orderId}`

#### 4. Update Order
**Endpoint:** `PUT /api/v1/orders/{orderId}`

#### 5. Update Order Status
**Endpoint:** `PATCH /api/v1/orders/{orderId}/status?status=CONFIRMED`

**Query Parameter:**
- `status`: PENDING, CONFIRMED, PREPARING, OUT_FOR_DELIVERY, DELIVERED, CANCELLED

**Example:** `PATCH /api/v1/orders/order-uuid-789/status?status=CONFIRMED`

**Success Response (200 OK):**
```json
{
  "status": "OK",
  "data": {
    "id": "order-uuid-789",
    "orderStatus": "CONFIRMED",
    "orderDate": "2026-01-23T17:00:00Z",
    "totalAmount": 880
  },
  "message": "Successfully updated"
}
```

**Test Status Updates:**
```
# Order lifecycle
PENDING -> CONFIRMED -> PREPARING -> OUT_FOR_DELIVERY -> DELIVERED
                   \-> CANCELLED
```

#### 6. Delete Order
**Endpoint:** `DELETE /api/v1/orders/{orderId}`

---

## Cart Management APIs

### CartController
**Base Path:** `/api/v1/carts`

#### 1. Add Item to Cart ⭐
**Endpoint:** `POST /api/v1/carts/add-item`

**Description:** Smart cart management with auto-creation, validation, and price calculation

**Request Body:**
```json
{
  "userId": 101,
  "restaurantId": 5,
  "menuItemId": "butter-chicken-uuid",
  "quantity": 2,
  "specialInstruction": "Extra spicy, no onions"
}
```

**Validations Performed:**
- ✅ Restaurant is active and open
- ✅ Same restaurant constraint
- ✅ Menu item availability
- ✅ Price verification
- ✅ Cart expiry check

**Success Response (200 OK):**
```json
{
  "status": "OK",
  "data": {
    "cartId": "cart-uuid-abc123",
    "userId": 101,
    "restaurantId": 5,
    "restaurantName": "Taste of India",
    "items": [
      {
        "cartItemId": "item-uuid-xyz789",
        "menuItemId": "butter-chicken-uuid",
        "menuItemName": "Butter Chicken",
        "quantity": 2,
        "unitPrice": 35000,
        "totalPrice": 70000,
        "specialInstruction": "Extra spicy, no onions",
        "isAvailable": true,
        "priceChanged": false,
        "currentPrice": null
      }
    ],
    "priceBreakdown": {
      "itemTotal": 70000,
      "packagingCharges": 5,
      "gst": 3500,
      "platformFee": 2,
      "deliveryFee": 40,
      "discount": 0,
      "totalAmount": 73547
    },
    "totalPrice": 73547,
    "createdAt": "2026-01-23T17:00:00Z",
    "updatedAt": "2026-01-23T17:05:00Z",
    "expiresAt": "2026-01-23T17:35:00Z"
  },
  "message": "Item added to cart successfully"
}
```

**Price Breakdown Formula:**
```
Item Total = 70000 (₹700.00)
Packaging = 5 (₹0.05)  [₹5 per item]
GST = 3500 (₹35.00)    [5% of item total]
Platform Fee = 2 (₹0.02)
Delivery = 40 (₹0.40)
Total = 73547 (₹735.47)
```

**Test Scenarios:**

**Add First Item (Creates New Cart):**
```json
{
  "userId": 201,
  "restaurantId": 10,
  "menuItemId": "paneer-tikka-uuid",
  "quantity": 1,
  "specialInstruction": "Medium spice"
}
```

**Add Second Item (Same Restaurant):**
```json
{
  "userId": 201,
  "restaurantId": 10,
  "menuItemId": "garlic-naan-uuid",
  "quantity": 3,
  "specialInstruction": null
}
```

**Error: Different Restaurant (Should Fail):**
```json
{
  "userId": 201,
  "restaurantId": 15,
  "menuItemId": "biryani-uuid",
  "quantity": 1
}
```

**Expected Error (400 Bad Request):**
```json
{
  "status": "BAD_REQUEST",
  "data": null,
  "message": "Cannot add items from different restaurant. Please clear your current cart first. Current restaurant ID: 10, Requested restaurant ID: 15"
}
```

#### 2. Update Cart Item ⭐
**Endpoint:** `PUT /api/v1/carts/{cartId}/items/{cartItemId}`

**Request Body:**
```json
{
  "quantity": 5
}
```

**Note:** Setting `quantity: 0` removes the item

**Test Scenarios:**

**Increase Quantity:**
```json
{
  "quantity": 8
}
```

**Remove Item:**
```json
{
  "quantity": 0
}
```

**Response on Cart Empty (204 No Content):**
```json
{
  "status": "NO_CONTENT",
  "data": null,
  "message": "Cart is now empty and has been deleted"
}
```

#### 3. Remove Cart Item
**Endpoint:** `DELETE /api/v1/carts/{cartId}/items/{cartItemId}`

**Example:** `DELETE /api/v1/carts/cart-uuid-abc123/items/item-uuid-xyz789`

#### 4. Get Active Cart⭐
**Endpoint:** `GET /api/v1/carts/active/user/{userId}`

**Example:** `GET /api/v1/carts/active/user/101`

**Features:**
- Returns only non-expired cart
- Checks item availability
- Detects price changes
- Enriches with restaurant name

**Success Response (200 OK):**
```json
{
  "status": "OK",
  "data": {
    "cartId": "cart-uuid-abc123",
    "userId": 101,
    "restaurantId": 5,
    "restaurantName": "Taste of India",
    "items": [
      {
        "cartItemId": "item-uuid-1",
        "menuItemName": "Butter Chicken",
        "quantity": 2,
        "unitPrice": 35000,
        "isAvailable": true,
        "priceChanged": true,
        "currentPrice": 38000
      },
      {
        "cartItemId": "item-uuid-2",
        "menuItemName": "Garlic Naan",
        "quantity": 3,
        "isAvailable": false,
        "priceChanged": false
      }
    ],
    "priceBreakdown": {...},
    "expiresAt": "2026-01-23T17:35:00Z"
  },
  "message": "Active cart retrieved successfully"
}
```

**Warning Indicators:**
- `priceChanged: true` - Item price increased/decreased
- `isAvailable: false` - Item currently out of stock

#### 5. Clear Cart
**Endpoint:** `DELETE /api/v1/carts/{cartId}/clear`

**Success Response (204 No Content):**
```json
{
  "status": "NO_CONTENT",
  "data": null,
  "message": "Cart cleared successfully"
}
```

#### 6. Legacy Cart Operations

**Create Cart (Legacy):**  
`POST /api/v1/carts/create`

**Get All Carts (Paginated):**  
`GET /api/v1/carts?page=0&size=10&sortBy=createAt&direction=desc`

**Get Cart by ID:**  
`GET /api/v1/carts/{cartId}`

**Get Cart by User ID:**  
`GET /api/v1/carts/user/{userId}`

**Update Cart:**  
`PUT /api/v1/carts/{cartId}`

**Delete Cart:**  
`DELETE /api/v1/carts/{cartId}`

---

## Common Response Format

### Success Response Structure
```json
{
  "status": "HTTP_STATUS",
  "data": { ... },
  "message": "Success message"
}
```

### HTTP Status Codes

| Code | Status | Usage |
|------|--------|-------|
| 200 | OK | Successful GET, PUT, PATCH |
| 201 | CREATED | Successful POST (resource created) |
| 204 | NO_CONTENT | Successful DELETE or empty cart |
| 400 | BAD_REQUEST | Validation error, business rule violation |
| 404 | NOT_FOUND | Resource not found |
| 500 | INTERNAL_SERVER_ERROR | Server error |

---

## Error Handling

### Validation Error (400)
```json
{
  "status": "BAD_REQUEST",
  "data": null,
  "message": "Menu item name is required"
}
```

### Resource Not Found (404)
```json
{
  "status": "NOT_FOUND",
  "data": null,
  "message": "Restaurant not found with ID: 999"
}
```

### Cart Validation Errors

**Different Restaurant:**
```json
{
  "status": "BAD_REQUEST",
  "message": "Cannot add items from different restaurant. Please clear your current cart first."
}
```

**Restaurant Closed:**
```json
{
  "status": "NOT_FOUND",
  "message": "Restaurant is currently closed. Please check operational hours."
}
```

**Item Unavailable:**
```json
{
  "status": "NOT_FOUND",
  "message": "Menu item is not available or out of stock. Item ID: xyz"
}
```

**Cart Expired:**
```json
{
  "status": "BAD_REQUEST",
  "message": "Cart has expired. Please create a new cart."
}
```

---

## Postman Collection Setup

### Environment Variables
```json
{
  "baseUrl": "http://localhost:8080",
  "apiVersion": "/api/v1",
  "authToken": "{{token}}",
  "userId": "101",
  "restaurantId": "5",
  "cartId": "{{cartId}}",
  "orderId": "{{orderId}}"
}
```

### Collection Structure
```
└── GoDesii APIs
    ├── 01. Authentication
    │   └── Login
    ├── 02. Restaurant Management
    │   ├── Create Restaurant
    │   ├── Get All Restaurants
    │   ├── Get Restaurant by ID
    │   ├── Update Restaurant
    │   └── Delete Restaurant
    ├── 03. Menu Items
    │   ├── Create Menu Item
    │   ├── Get All Menu Items
    │   ├── Get Menu Item by ID
    │   ├── Get Items by Category
    │   ├── Update Menu Item
    │   └── Delete Menu Item
    ├── 04. Orders
    │   ├── Create Order
    │   ├── Get All Orders
    │   ├── Get Order by ID
    │   ├── Update Order
    │   ├── Update Order Status
    │   └── Delete Order
    └── 05. Cart Management
        ├── Add Item to Cart
        ├── Update Cart Item
        ├── Remove Cart Item
        ├── Get Active Cart
        ├── Clear Cart
        ├── Get All Carts
        └── Delete Cart
```

---

## Complete Test Flow Example

### End-to-End Order Flow

```javascript
// 1. Login
POST /auth/login
{
  "email": "customer@godesii.com",
  "password": "Test@123"
}
// Save token

// 2. Browse Restaurants
GET /api/v1/restaurants?page=0&size=20

// 3. View Restaurant Menu
GET/api/v1/menu-items/category/5

// 4. Add Items to Cart
POST /api/v1/carts/add-item
{
  "userId": 101,
  "restaurantId": 5,
  "menuItemId": "butter-chicken-uuid",
  "quantity": 2
}

POST /api/v1/carts/add-item
{
  "userId": 101,
  "restaurantId": 5,
  "menuItemId": "naan-uuid",
  "quantity": 4
}

// 5. Review Cart
GET /api/v1/carts/active/user/101

// 6. Update Quantity
PUT /api/v1/carts/{cartId}/items/{itemId}
{
  "quantity": 3
}

// 7. Create Order from Cart
POST /api/v1/orders/create
{
  "userId": "101",
  "restaurantId": "5",
  "orderStatus": "PENDING",
  "orderItems": [...],  // From cart
  "orderAddress": {...}
}

// 8. Track Order Status
GET /api/v1/orders/{orderId}

// 9. Update Order Status (Restaurant Side)
PATCH /api/v1/orders/{orderId}/status?status=CONFIRMED
```

---

## Testing Checklist

### Restaurant APIs
- [ ] Create restaurant with full details
- [ ] Create restaurant with overnight hours (22:00-02:00)
- [ ] Get all restaurants with pagination
- [ ] Update restaurant details
- [ ] Verify restaurant validation errors

### Menu Item APIs
- [ ] Create VEG/NON_VEG/VEGAN items
- [ ] Get items by category
- [ ] Mark item as unavailable
- [ ] Update price
- [ ] Delete item

### Cart APIs
- [ ] Add item to empty cart (creates cart)
- [ ] Add item to existing cart (same restaurant)
- [ ] Try adding from different restaurant (should fail)
- [ ] Update item quantity
- [ ] Set quantity to 0 (removes item)
- [ ] Get active cart with price warnings
- [ ] Verify cart expiry (wait 31 minutes)
- [ ] Try adding when restaurant closed (should fail)
- [ ] Try adding unavailable item (should fail)
- [ ] Verify price calculations
- [ ] Clear cart

### Order APIs
- [ ] Create order with multiple items
- [ ] Update order status through lifecycle
- [ ] Get order history
- [ ] Cancel order

---

**Document Version:** 1.0  
**Platform:** GoDesii Food Delivery Service  
**Total Controllers:** 15  
**Total Endpoints:** 50+  
**Last Updated:** January 23, 2026
