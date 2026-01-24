# GoDesii Cart API - Technical Documentation

## Table of Contents
1. [System Configuration](#system-configuration)
2. [API Endpoints Overview](#api-endpoints-overview)
3. [Detailed API Documentation](#detailed-api-documentation)
4. [Test JSON Payloads](#test-json-payloads)
5. [Error Handling](#error-handling)
6. [Business Logic & Validations](#business-logic--validations)

---

## System Configuration

### CartConfig Settings

| Configuration | Value | Description |
|---------------|-------|-------------|
| **Cart Expiry** | 30 minutes | Carts expire 30 minutes after last update |
| **Packaging Charge** | ₹5 per item | Applied to each unique item in cart |
| **GST Rate** | 5% | Applied on item total |
| **Platform Fee** | ₹2 | Flat fee per order |
| **Delivery Fee** | ₹40 | Placeholder (can be distance-based) |

**Location:** `com.godesii.godesii_services.config.CartConfig`

---

## API Endpoints Overview

| Endpoint | Method | Description | Response Type |
|----------|--------|-------------|---------------|
| `/api/v1/carts/add-item` | POST | Add item to cart | `CartResponse` |
| `/api/v1/carts/{cartId}/items/{itemId}` | PUT | Update item quantity | `CartResponse` |
| `/api/v1/carts/{cartId}/items/{itemId}` | DELETE | Remove item from cart | `CartResponse` |
| `/api/v1/carts/active/user/{userId}` | GET | Get active cart | `CartResponse` |
| `/api/v1/carts/{cartId}/clear` | DELETE | Clear entire cart | `204 No Content` |
| `/api/v1/carts/create` | POST | Create cart (legacy) | `Cart` |
| `/api/v1/carts` | GET | Get all carts (paginated) | `Page<Cart>` |
| `/api/v1/carts/{id}` | GET | Get cart by ID | `Cart` |
| `/api/v1/carts/user/{userId}` | GET | Get cart by user ID | `Cart` |
| `/api/v1/carts/{id}` | PUT | Update cart | `Cart` |
| `/api/v1/carts/{id}` | DELETE | Delete cart | `204 No Content` |

---

## Detailed API Documentation

### 1. Add Item to Cart

**Endpoint:** `POST /api/v1/carts/add-item`

**Description:** Add a menu item to the user's cart. Creates a new cart if one doesn't exist, or adds to existing cart.

**Validations:**
- ✅ Same restaurant constraint 
- ✅ Restaurant is active and open
- ✅ Menu item is available
- ✅ Price verification
- ✅ Cart expiry check

**Request Body:**
```json
{
  "userId": 1,
  "restaurantId": 123,
  "menuItemId": "550e8400-e29b-41d4-a716-446655440000",
  "quantity": 2,
  "specialInstruction": "Extra spicy, no onions"
}
```

**Field Validations:**
- `userId`: Required, must be positive
- `restaurantId`: Required, must be positive
- `menuItemId`: Required, not blank
- `quantity`: Required, range 1-100
- `specialInstruction`: Optional, max 500 characters

**Success Response (200 OK):**
```json
{
  "status": "OK",
  "data": {
    "cartId": "cart-uuid-123",
    "userId": 1,
    "restaurantId": 123,
    "restaurantName": "Taste of India",
    "items": [
      {
        "cartItemId": "item-uuid-456",
        "menuItemId": "550e8400-e29b-41d4-a716-446655440000",
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
    "updatedAt": "2026-01-23T17:05:30Z",
    "expiresAt": "2026-01-23T17:35:30Z"
  },
  "message": "Item added to cart successfully"
}
```

**Error Responses:**

| Status | Error | Description |
|--------|-------|-------------|
| 400 | `CartValidationException` | Different restaurant item attempted |
| 404 | `RestaurantClosedException` | Restaurant inactive or closed |
| 404 | `ItemNotAvailableException` | Menu item unavailable |
| 400 | Validation Error | Invalid request parameters |

**Different Restaurant Error (400):**
```json
{
  "status": "BAD_REQUEST",
  "data": null,
  "message": "Cannot add items from different restaurant. Please clear your current cart first. Current restaurant ID: 123, Requested restaurant ID: 456"
}
```

**Restaurant Closed Error (404):**
```json
{
  "status": "NOT_FOUND",
  "data": null,
  "message": "Restaurant is currently closed. Please check operational hours."
}
```

---

### 2. Update Cart Item Quantity

**Endpoint:** `PUT /api/v1/carts/{cartId}/items/{cartItemId}`

**Description:** Update the quantity of a specific item in the cart. Setting quantity to 0 removes the item.

**Path Parameters:**
- `cartId`: Cart UUID
- `cartItemId`: Cart item UUID

**Request Body:**
```json
{
  "quantity": 5
}
```

**Field Validations:**
- `quantity`: Required, range 0-100 (0 removes item)

**Success Response (200 OK):**
```json
{
  "status": "OK",
  "data": {
    "cartId": "cart-uuid-123",
    "userId": 1,
    "restaurantId": 123,
    "restaurantName": "Taste of India",
    "items": [
      {
        "cartItemId": "item-uuid-456",
        "menuItemId": "550e8400-e29b-41d4-a716-446655440000",
        "menuItemName": "Butter Chicken",
        "quantity": 5,
        "unitPrice": 35000,
        "totalPrice": 175000,
        "specialInstruction": "Extra spicy, no onions",
        "isAvailable": true,
        "priceChanged": false,
        "currentPrice": null
      }
    ],
    "priceBreakdown": {
      "itemTotal": 175000,
      "packagingCharges": 5,
      "gst": 8750,
      "platformFee": 2,
      "deliveryFee": 40,
      "discount": 0,
      "totalAmount": 183797
    },
    "totalPrice": 183797,
    "createdAt": "2026-01-23T17:00:00Z",
    "updatedAt": "2026-01-23T17:10:00Z",
    "expiresAt": "2026-01-23T17:40:00Z"
  },
  "message": "Cart item updated successfully"
}
```

**Cart Deleted Response (204 No Content) - When last item removed:**
```json
{
  "status": "NO_CONTENT",
  "data": null,
  "message": "Cart is now empty and has been deleted"
}
```

**Error Responses:**

| Status | Error | Description |
|--------|-------|-------------|
| 400 | `CartValidationException` | Cart expired |
| 404 | `ResourceNotFoundException` | Cart or item not found |

---

### 3. Remove Cart Item

**Endpoint:** `DELETE /api/v1/carts/{cartId}/items/{cartItemId}`

**Description:** Remove a specific item from the cart completely.

**Path Parameters:**
- `cartId`: Cart UUID
- `cartItemId`: Cart item UUID

**Success Response (200 OK):**
```json
{
  "status": "OK",
  "data": {
    "cartId": "cart-uuid-123",
    "userId": 1,
    "restaurantId": 123,
    "restaurantName": "Taste of India",
    "items": [],
    "priceBreakdown": {
      "itemTotal": 0,
      "packagingCharges": 0,
      "gst": 0,
      "platformFee": 2,
      "deliveryFee": 40,
      "discount": 0,
      "totalAmount": 42
    },
    "totalPrice": 42,
    "createdAt": "2026-01-23T17:00:00Z",
    "updatedAt": "2026-01-23T17:12:00Z",
    "expiresAt": "2026-01-23T17:42:00Z"
  },
  "message": "Cart item removed successfully"
}
```

---

### 4. Get Active Cart

**Endpoint:** `GET /api/v1/carts/active/user/{userId}`

**Description:** Retrieve the user's active (non-expired) cart with full details including availability and price change warnings.

**Path Parameters:**
- `userId`: User ID (Long)

**Success Response (200 OK):**
```json
{
  "status": "OK",
  "data": {
    "cartId": "cart-uuid-123",
    "userId": 1,
    "restaurantId": 123,
    "restaurantName": "Taste of India",
    "items": [
      {
        "cartItemId": "item-uuid-456",
        "menuItemId": "550e8400-e29b-41d4-a716-446655440000",
        "menuItemName": "Butter Chicken",
        "quantity": 2,
        "unitPrice": 35000,
        "totalPrice": 70000,
        "specialInstruction": "Extra spicy",
        "isAvailable": true,
        "priceChanged": true,
        "currentPrice": 38000
      },
      {
        "cartItemId": "item-uuid-789",
        "menuItemId": "660e8400-e29b-41d4-a716-446655440001",
        "menuItemName": "Garlic Naan",
        "quantity": 3,
        "unitPrice": 5000,
        "totalPrice": 15000,
        "specialInstruction": null,
        "isAvailable": false,
        "priceChanged": false,
        "currentPrice": null
      }
    ],
    "priceBreakdown": {
      "itemTotal": 85000,
      "packagingCharges": 10,
      "gst": 4250,
      "platformFee": 2,
      "deliveryFee": 40,
      "discount": 0,
      "totalAmount": 89302
    },
    "totalPrice": 89302,
    "createdAt": "2026-01-23T17:00:00Z",
    "updatedAt": "2026-01-23T17:05:00Z",
    "expiresAt": "2026-01-23T17:35:00Z"
  },
  "message": "Active cart retrieved successfully"
}
```

**Note:** Response includes:
- `priceChanged: true` - Item price has changed since adding to cart
- `isAvailable: false` - Item is currently out of stock

**Error Response (404):**
```json
{
  "status": "NOT_FOUND",
  "data": null,
  "message": "No active cart found for user ID: 1"
}
```

---

### 5. Clear Cart

**Endpoint:** `DELETE /api/v1/carts/{cartId}/clear`

**Description:** Remove all items and delete the cart completely.

**Path Parameters:**
- `cartId`: Cart UUID

**Success Response (204 No Content):**
```json
{
  "status": "NO_CONTENT",
  "data": null,
  "message": "Cart cleared successfully"
}
```

---

### 6. Create Cart (Legacy)

**Endpoint:** `POST /api/v1/carts/create`

**Description:** Create a cart using the legacy CartRequest format. Recommended to use `/add-item` instead.

**Request Body:**
```json
{
  "userId": 1,
  "restaurantId": 123,
  "cartItemRequests": [
    {
      "productId": 550,
      "quantity": 2,
      "price": 35000,
      "specialInstruction": "Extra spicy"
    },
    {
      "productId": 551,
      "quantity": 3,
      "price": 5000,
      "specialInstruction": null
    }
  ]
}
```

**Success Response (201 Created):**
```json
{
  "status": "CREATED",
  "data": {
    "id": "cart-uuid-123",
    "createAt": "2026-01-23T17:00:00Z",
    "updatedAt": "2026-01-23T17:00:00Z",
    "expiresAt": null,
    "userId": 1,
    "restaurantId": 123,
    "totalPrice": 85000,
    "cartItems": [...]
  },
  "message": "Successfully created"
}
```

---

### 7. Get All Carts (Paginated)

**Endpoint:** `GET /api/v1/carts`

**Description:** Retrieve paginated list of all carts.

**Query Parameters:**
- `page`: Page number (default: 0)
- `size`: Page size (default: 10)
- `sortBy`: Field to sort by (default: createAt)
- `direction`: Sort direction - asc/desc (default: desc)

**Example:** `GET /api/v1/carts?page=0&size=20&sortBy=updatedAt&direction=desc`

**Success Response (200 OK):**
```json
{
  "status": "OK",
  "data": {
    "content": [
      {
        "id": "cart-uuid-123",
        "userId": 1,
        "restaurantId": 123,
        "totalPrice": 85000,
        "createAt": "2026-01-23T17:00:00Z",
        "updatedAt": "2026-01-23T17:05:00Z",
        "expiresAt": "2026-01-23T17:35:00Z",
        "cartItems": [...]
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 20,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      }
    },
    "totalElements": 45,
    "totalPages": 3,
    "last": false,
    "first": true,
    "numberOfElements": 20,
    "empty": false
  },
  "message": "Successfully fetched"
}
```

---

### 8. Get Cart by ID

**Endpoint:** `GET /api/v1/carts/{id}`

**Path Parameters:**
- `id`: Cart UUID

**Success Response (200 OK):**
```json
{
  "status": "OK",
  "data": {
    "id": "cart-uuid-123",
    "createAt": "2026-01-23T17:00:00Z",
    "updatedAt": "2026-01-23T17:05:00Z",
    "expiresAt": "2026-01-23T17:35:00Z",
    "userId": 1,
    "restaurantId": 123,
    "totalPrice": 85000,
    "cartItems": [...]
  },
  "message": "Successfully fetched"
}
```

---

### 9. Get Cart by User ID

**Endpoint:** `GET /api/v1/carts/user/{userId}`

**Path Parameters:**
- `userId`: User ID (Long)

**Success Response (200 OK):**
```json
{
  "status": "OK",
  "data": {
    "id": "cart-uuid-123",
    "userId": 1,
    "restaurantId": 123,
    "totalPrice": 85000,
    "createAt": "2026-01-23T17:00:00Z",
    "updatedAt": "2026-01-23T17:05:00Z",
    "expiresAt": "2026-01-23T17:35:00Z",
    "cartItems": [...]
  },
  "message": "Successfully fetched"
}
```

---

## Test JSON Payloads

### Complete Test Scenarios

#### Scenario 1: New User - Add First Item

**Request:** `POST /api/v1/carts/add-item`
```json
{
  "userId": 101,
  "restaurantId": 5,
  "menuItemId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "quantity": 1,
  "specialInstruction": "Medium spice level"
}
```

---

#### Scenario 2: Existing Cart - Add Another Item

**Request:** `POST /api/v1/carts/add-item`
```json
{
  "userId": 101,
  "restaurantId": 5,
  "menuItemId": "b2c3d4e5-f6g7-8901-bcde-f12345678901",
  "quantity": 2,
  "specialInstruction": null
}
```

---

#### Scenario 3: Update Item Quantity to 5

**Request:** `PUT /api/v1/carts/cart-uuid-abc123/items/item-uuid-xyz789`
```json
{
  "quantity": 5
}
```

---

#### Scenario 4: Remove Item (Set Quantity to 0)

**Request:** `PUT /api/v1/carts/cart-uuid-abc123/items/item-uuid-xyz789`
```json
{
  "quantity": 0
}
```

---

#### Scenario 5: Multiple Items - Bulk Create (Legacy)

**Request:** `POST /api/v1/carts/create`
```json
{
  "userId": 102,
  "restaurantId": 8,
  "cartItemRequests": [
    {
      "productId": 201,
      "quantity": 1,
      "price": 25000,
      "specialInstruction": "Less oil"
    },
    {
      "productId": 202,
      "quantity": 2,
      "price": 15000,
      "specialInstruction": "Extra cheese"
    },
    {
      "productId": 203,
      "quantity": 1,
      "price": 8000,
      "specialInstruction": null
    }
  ]
}
```

---

## Error Handling

### Standard Error Response Format

```json
{
  "status": "ERROR_TYPE",
  "data": null,
  "message": "Detailed error message"
}
```

### Common Error Scenarios

#### 1. Validation Errors (400 Bad Request)

**Invalid Quantity:**
```json
{
  "status": "BAD_REQUEST",
  "data": null,
  "message": "Quantity must be at least 1"
}
```

**Missing Required Field:**
```json
{
  "status": "BAD_REQUEST",
  "data": null,
  "message": "User ID is required"
}
```

---

#### 2. Different Restaurant Constraint (400)

```json
{
  "status": "BAD_REQUEST",
  "data": null,
  "message": "Cannot add items from different restaurant. Please clear your current cart first. Current restaurant ID: 5, Requested restaurant ID: 8"
}
```

---

#### 3. Restaurant Closed (404)

```json
{
  "status": "NOT_FOUND",
  "data": null,
  "message": "Restaurant is currently closed. Please check operational hours."
}
```

---

#### 4. Item Not Available (404)

```json
{
  "status": "NOT_FOUND",
  "data": null,
  "message": "Menu item is not available or out of stock. Item ID: a1b2c3d4-e5f6-7890-abcd-ef1234567890"
}
```

---

#### 5. Cart Expired (400)

```json
{
  "status": "BAD_REQUEST",
  "data": null,
  "message": "Cart has expired. Please create a new cart."
}
```

---

#### 6. Resource Not Found (404)

```json
{
  "status": "NOT_FOUND",
  "data": null,
  "message": "Cart not found with ID: invalid-cart-id"
}
```

---

## Business Logic & Validations

### Cart Creation & Management

| Validation | Implementation |
|------------|----------------|
| **Same Restaurant** | Cannot add items from different restaurants to same cart |
| **Cart Expiry** | Expires 30 minutes after last update |
| **Auto-Creation** | Cart created automatically when first item added |
| **Auto-Deletion** | Cart deleted when last item removed |
| **Quantity Limits** | Min: 1, Max: 100 per item |

### Restaurant Validation

| Check | Logic |
|-------|-------|
| **Active Status** | Restaurant must be active (`isActive = true`) |
| **Operating Hours** | Current time must be within delivery hours |
| **Day of Week** | Validates against restaurant's schedule |
| **Overnight Hours** | Handles hours spanning midnight (e.g., 22:00-02:00) |

### Item Validation

| Check | Logic |
|-------|-------|
| **Availability** | Menu item must have `isAvailable = true` |
| **Existence** | Item must exist in database |
| **Price Update** | Cart price updated if menu price changed |

### Price Calculation Formula

```
Item Total = Σ(item.price × item.quantity)
Packaging = number_of_items × ₹5
GST = Item Total × 5%
Platform Fee = ₹2
Delivery Fee = ₹40

Total = Item Total + Packaging + GST + Platform Fee + Delivery Fee - Discounts
```

**Example Calculation:**
```
Items: 
  - Butter Chicken: ₹350 × 2 = ₹700
  - Garlic Naan: ₹50 × 3 = ₹150

Item Total = ₹850
Packaging = 2 items × ₹5 = ₹10
GST = ₹850 × 5% = ₹42.50
Platform Fee = ₹2
Delivery Fee = ₹40
Discount = ₹0

Total = ₹850 + ₹10 + ₹42.50 + ₹2 + ₹40 = ₹944.50
```

---

## Service Layer Methods

### CartService Public Methods

| Method | Description | Returns |
|--------|-------------|---------|
| `addItemToCart()` | Add item with full validation | `CartResponse` |
| `updateCartItem()` | Update item quantity | `CartResponse` |
| `removeCartItem()` | Remove specific item | `CartResponse` |
| `getActiveCart()` | Get non-expired cart | `CartResponse` |
| `clearCart()` | Delete entire cart | `void` |
| `create()` | Legacy cart creation | `Cart` |
| `getAll()` | Paginated cart list | `Page<Cart>` |
| `getById()` | Get cart by ID | `Cart` |
| `getByUserId()` | Get cart by user ID | `Cart` |
| `update()` | Update cart details | `Cart` |
| `delete()` | Delete cart | `void` |
| `cleanupExpiredCarts()` | Remove expired carts | `int` |

### Validation Methods

- `validateRestaurantOpen()` - Checks restaurant status and hours
- `validateItemAvailability()` - Verifies menu item availability
- `isExpired()` - Checks if cart has expired
- `calculatePriceBreakdown()` - Computes all price components
- `buildCartResponse()` - Enriches response with restaurant/item details

---

## Database Schema Requirements

### Cart Table
```sql
ALTER TABLE cart ADD COLUMN expires_at TIMESTAMP;
```

---

## Postman Collection Example

### Environment Variables
```json
{
  "baseUrl": "http://localhost:8080/api/v1",
  "userId": "1",
  "restaurantId": "123",
  "menuItemId": "550e8400-e29b-41d4-a716-446655440000",
  "cartId": "{{cartId}}",
  "cartItemId": "{{cartItemId}}"
}
```

### Collection Structure
```
└── Cart API
    ├── Add Item to Cart
    ├── Update Cart Item
    ├── Remove Cart Item
    ├── Get Active Cart
    ├── Clear Cart
    ├── Create Cart (Legacy)
    ├── Get All Carts
    ├── Get Cart by ID
    ├── Get Cart by User ID
    └── Delete Cart
```

---

## Testing Checklist

- [ ] Add item to empty cart
- [ ] Add item from same restaurant
- [ ] Try adding item from different restaurant (should fail)
- [ ] Update item quantity
- [ ] Set quantity to 0 (should remove item)
- [ ] Remove last item (should delete cart)
- [ ] Get active cart
- [ ] Wait 31 minutes and try to access cart (should fail)
- [ ] Try adding unavailable item (should fail)
- [ ] Try adding when restaurant closed (should fail)
- [ ] Verify price calculations
- [ ] Test price change detection

---

**Document Version:** 1.0  
**Last Updated:** January 23, 2026  
**Service:** GoDesii Cart Management API
