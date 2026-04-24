# End-to-End API Flow: Restaurant Onboarding to Analytics

This document illustrates the complete lifecycle of a restaurant and customer interaction on the GoDesii platform, starting from restaurant creation down to the analytics visualization demonstrating the Indian GST rules and commission calculations.

---

## 1. Restaurant Creation
*The platform admin or restaurant owner creates a new restaurant profile.*

**Endpoint:** `POST /api/v1/restaurants`

**Request Body:**
```json
{
  "name": "Sharma Dhaba",
  "phoneNo": "+91-9876543210",
  "cuisineType": "North Indian",
  "description": "Authentic Punjabi Food",
  "isVerified": true,
  "isActive": true,
  "foodCategory": "VEG",
  "operationalHourRequest": [
    { "dayOfWeek": "MONDAY", "openingTime": "08:00:00", "closingTime": "23:00:00" }
  ],
  "addressRequest": {
    "addressLine1": "Plot 42, Service Road",
    "city": "Bengaluru",
    "state": "Karnataka",
    "postalCode": "560100",
    "country": "India",
    "latitude": 12.8452,
    "longitude": 77.6602
  }
}
```

**Response:**
```json
{
  "status": 200,
  "message": "Restaurant created successfully",
  "data": {
    "id": 1,
    "name": "Sharma Dhaba",
    "isActive": true
  }
}
```

---

## 2. Commission Configuration
*GoDesii sets the agreed platform commission rate (e.g., 22%) for this specific restaurant.*

**Endpoint:** `PUT /api/v1/analytics/restaurant/1/commission?newCommissionPercent=22.0`

**Response:**
```json
{
  "status": 200,
  "message": "Commission updated to 22.0% for restaurant 1",
  "data": {
    "id": 1,
    "restaurantId": 1,
    "commissionPercentage": 22.00,
    "effectiveFrom": "2024-04-24T12:00:00Z",
    "active": true
  }
}
```
*Flow Explanation: This commission percentage will now automatically apply to every order placed at this restaurant for analytics and payout calculations.*

---

## 3. Customer Order Selection (Cart)
*Customer "johndoe" selects multiple items from Sharma Dhaba.*

**Endpoint:** `POST /api/v1/cart/items`

**Request Body:**
```json
{
  "username": "johndoe",
  "restaurantId": 1,
  "menuItemId": "MI-100",
  "quantity": 2
}
```

**Response:**
*Notice how prices are presented in the lowest denomination (paise/cents).*
```json
{
  "status": 200,
  "message": "Item added to cart",
  "data": {
    "cartId": 105,
    "restaurantId": 1,
    "items": [
      {
        "menuItemId": "MI-100",
        "itemName": "Family Pack Chicken Biryani",
        "quantity": 2,
        "unitPrice": 50000 
      }
    ],
    "priceBreakdown": {
      "itemTotal": 100000,          // ₹1000.00
      "deliveryFee": 4000,          // ₹40.00
      "packagingCharges": 1000,     // ₹10.00
      "platformFee": 500,           // ₹5.00
      "gst": 5000,                  // ₹50.00 (5% GST calculated on itemTotal dynamically)
      "discount": 0,                
      "totalAmount": 110500         // ₹1105.00 (Customer pays this)
    }
  }
}
```

---

## 4. Place Order
*Customer proceeds to checkout and places the order with Cash on Delivery (COD).*

**Endpoint:** `POST /api/v1/orders/place`

**Request Body:**
```json
{
  "username": "johndoe",
  "paymentMethod": "COD",
  "specialInstructions": "Please provide extra onions."
}
```

**Response:**
```json
{
  "status": 200,
  "data": {
    "orderId": "550e8400-e29b-41d4-a716-446655440000",
    "orderStatus": "PAYMENT_SUCCESS",
    "totalAmount": 110500,
    "order": {
      "cartId": 105,
      "items": [
        {
          "orderItemId": "8f3e2d1a-4c5b-6a7f-8e9d-0b1c2d3a4b5c",
          "productId": "MI-100",
          "productName": "Family Pack Chicken Biryani",
          "productImageUrl": "https://godesii-images.s3.amazonaws.com/mi-100.jpg",
          "specialInstruction": "Please provide extra onions.",
          "quantity": 2,
          "priceAtPurchase": 50000
        }
      ]
    }
  }
}
```
*Flow Explanation: Behind the scenes, the `OrderService` captures the `itemTotal`, `deliveryFee`, `packagingCharges`, and `platformFee` into the `Order` entity. It also calculates the Indian GST (5% fixed split as 2.5% CGST + SGST on ₹1000 itemTotal) and saves it in the `OrderGst` table. The cart is then cleared.*

---

## 5. Order History
*Customer views their past orders.*

**Endpoint:** `GET /api/v1/orders/user/johndoe`

**Response:**
```json
{
  "status": 200,
  "data": [
    {
      "orderId": "550e8400-e29b-41d4-a716-446655440000",
      "restaurantId": "1",
      "orderStatus": "DELIVERED",
      "totalAmount": 110500,
      "orderDate": "2024-04-24T12:30:00Z",
      "itemTotal": 100000,
      "deliveryFee": 4000,
      "platformFee": 500,
      "packagingCharges": 1000,
      "orderItems": [
        {
          "orderItemId": "8f3e2d1a-4c5b-6a7f-8e9d-0b1c2d3a4b5c",
          "productId": "MI-100",
          "productName": "Family Pack Chicken Biryani",
          "productImageUrl": "https://godesii-images.s3.amazonaws.com/mi-100.jpg",
          "specialInstruction": "Please provide extra onions.",
          "quantity": 2,
          "priceAtPurchase": 50000
        }
      ]
    }
  ]
}
```

---

## 6. Restaurant Earnings Analytics
*The restaurant owner logs into their dashboard to visualize their earnings at the end of the day.*

**Endpoint:** `GET /api/v1/analytics/restaurant/1/earnings?startDate=2024-04-01&endDate=2024-04-30&groupBy=DAY`

**Response:**
```json
{
  "status": 200,
  "message": "Earnings analytics fetched successfully",
  "data": {
    "summary": {
      "restaurantId": "1",
      "period": "01 Apr 2024 – 30 Apr 2024",
      "totalOrders": 1,
      "grossRevenue": 1000.00,            // Derived from itemTotal (100000 paise → 1000 INR)
      "totalCgst": 25.00,                 // 2.5% of 1000
      "totalSgst": 25.00,                 // 2.5% of 1000
      "totalIgst": 0.00,
      "totalFoodGst": 50.00,              // Total 5% food GST (Remitted to Govt)
      "totalPlatformGst": 8.10,           // 18% of 45 (₹40 delivery + ₹5 platform fee) GoDesii liability
      "commissionPercentage": 22.00,      // Commission fetched from RestaurantCommission table
      "totalPlatformCommissionDeducted": 220.00, // 22% of ₹1000 itemTotal
      "netEarningsToRestaurant": 780.00,  // (1000 - 220)
      "platformEarnings": 256.90,         // (220 commission + 45 fees - 8.10 GST)
      "avgOrderValue": 1000.00
    },
    "chartData": [
      {
        "label": "2024-04-24",
        "grossRevenue": 1000.00,
        "netEarnings": 780.00,
        "gstDeducted": 50.00,
        "commissionDeducted": 220.00,
        "orderCount": 1
      }
    ],
    "orders": [
      {
        "orderId": "550e8400-e29b-41d4-a716-446655440000",
        "orderDate": "2024-04-24T12:30:00Z",
        "orderStatus": "DELIVERED",
        "itemTotal": 1000.00,
        "deliveryFee": 40.00,
        "packagingCharges": 10.00,
        "platformFee": 5.00,
        "discountAmount": 0.00,
        "totalAmount": 1105.00,
        "cgst": 25.00,
        "sgst": 25.00,
        "igst": 0.00,
        "totalFoodGst": 50.00,
        "platformCommissionDeducted": 220.00,
        "netPayout": 780.00
      }
    ],
    "currentPage": 0,
    "totalPages": 1,
    "totalElements": 1,
    "pageSize": 20
  }
}
```

### Flow Breakdown for Analytics Math:
For this single order of `₹1105.00` total paid by the customer:
1. **Gross Revenue (`itemTotal`)** = `₹1000.00` (Items value)
2. **Food GST** = `₹50.00` (5% calculated dynamically at checkout and paid extra by customer).
3. **Platform Commission** = `₹220.00` (computed as 22% of the `itemTotal`).
4. **Restaurant Net Payout** = `1000.00 - 220.00 = ₹780.00`. The restaurant collected the base value + taxes, gives taxes to gov, pays commision, and pockets `₹780.00`.
5. **Platform Earnings** = Commission (`₹220.00`) + Delivery/Platform Fees (`₹45.00`) = `₹265.00`. GoDesii then pays its own 18% GST (on the `₹45` of platform services) which is `₹8.10`. Leaving GoDesii an effective revenue of `₹256.90`.
