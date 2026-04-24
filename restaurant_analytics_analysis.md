# Restaurant Earnings Analytics — Analysis & Design

## Overview

This document captures the analysis for building a **Restaurant Earnings Analytics API** for GoDesii, modeled after how Zomato/Swiggy present earnings and platform deductions to restaurant partners under the Indian GST tax regime.

---

## 1. Do We Need a New Entity?

| Status | Entity | Purpose | Decision |
|--------|--------|---------|----------|
| ✅ Exists | `Order` | Core order data, fees, discount breakdown | Already has all pricing fields added |
| ✅ Exists | `OrderGst` | CGST, SGST, IGST breakdown per order | Created in last session |
| ⚠️ Recommended | `RestaurantCommission` | Platform commission config per restaurant | **Pending user decision** |

### Why `RestaurantCommission` is Recommended

Currently there is **no mechanism** to know what percentage commission GoDesii charges each restaurant. Without this:
- Analytics math must be hardcoded — not scalable
- Different restaurants cannot have different commission rates (which is how real platforms work)
- There is no admin control surface to manage commissions

This is a **lightweight config table**, not a transactional one. It stores one row per restaurant with the agreed commission rate.

> [!IMPORTANT]
> **Decision Required:** Should commission % come from a `RestaurantCommission` DB entity (configurable per restaurant) or from `application.yaml` (global flat rate)?
>
> **Recommendation: DB entity** — mirrors real platforms, supports per-restaurant negotiation, no redeployment needed to change rates.

---

## 2. Indian GST Regime — How Zomato/Swiggy Do It

Under Indian GST for food delivery platforms, there are **two separate GST streams**:

### GST on Food (Restaurant's Liability)
| Scenario | GST Rate | ITC Available |
|----------|----------|---------------|
| Restaurant turnover ≤ ₹1.5 Cr (Composition) | 5% (no ITC) | ❌ No |
| Regular Restaurant | 5% GST | ❌ No (food services) |

### GST on Platform Services (GoDesii's Liability)
| Component | GST Rate | Who Bears It |
|-----------|----------|--------------|
| Platform / Convenience Fee | 18% GST | Customer pays |
| Delivery Charges | 18% GST | Customer pays |

### Intra-state vs Inter-state (CGST/SGST vs IGST)
| Supply Type | Tax Applied | Split |
|-------------|-------------|-------|
| Same state (restaurant & GoDesii in same state) | CGST + SGST | 50% each |
| Different states | IGST | 100% IGST |

> [!NOTE]
> For simplicity (and how Swiggy/Zomato operate), most transactions are treated as **intra-state** (CGST + SGST) since both the restaurant and platform are typically in the same state as the customer.

---

## 3. Earnings Split Per Order (Swiggy/Zomato Model)

```
Customer Pays (totalAmount):
  = itemTotal
  + deliveryFee
  + packagingCharges
  + platformFee
  + GST (on food @ 5%) + GST (on platform/delivery @ 18%)
  - discountAmount

──────────────────────────────────────────────────────────────

GoDesii Collects (full payment hits GoDesii):
  → GoDesii's Earnings:
      platformFee + deliveryFee + packagingCharges
      + commission % of itemTotal (e.g. 20%)
      - GoDesii's own 18% GST liability on platform/delivery charges

  → Restaurant's Net Payout:
      itemTotal
      - commission % deducted by GoDesii
      - food GST (5%) that restaurant must remit to govt
```

### Example (₹500 order, 5% food GST, 20% commission, ₹40 delivery, ₹10 platform fee)

| Line Item | Amount |
|-----------|--------|
| Item Total | ₹500.00 |
| Food GST (5% CGST 2.5% + SGST 2.5%) | ₹25.00 |
| Delivery Fee | ₹40.00 |
| Delivery GST (18%) | ₹7.20 |
| Platform Fee | ₹10.00 |
| Platform GST (18%) | ₹1.80 |
| **Customer Pays** | **₹584.00** |
| GoDesii Commission (20% of ₹500) | ₹100.00 |
| **Restaurant Net Payout** | **₹375.00** (₹500 - ₹100 commission - ₹25 food GST) |
| **GoDesii Net Earnings** | **₹142.00** (₹40 + ₹10 + ₹100 commission - ₹9 GST liability) |

---

## 4. Proposed API Design

### Endpoint
```
GET /api/v1/analytics/restaurant/{restaurantId}/earnings
```

### Query Parameters (Filters)

| Parameter | Type | Description |
|-----------|------|-------------|
| `startDate` | `ISO-8601 date` | Start of date range (e.g. `2024-01-01`) |
| `endDate` | `ISO-8601 date` | End of date range (e.g. `2024-01-31`) |
| `minAmount` | `Long` | Minimum order value filter |
| `maxAmount` | `Long` | Maximum order value filter |
| `orderStatus` | `String` | Filter by order status (default: `DELIVERED`) |
| `page` | `int` | Page number (default: 0) |
| `size` | `int` | Page size (default: 20) |
| `groupBy` | `String` | `DAY`, `WEEK`, `MONTH` — for chart aggregation |

### Response Structure

```json
{
  "summary": {
    "restaurantId": "R001",
    "restaurantName": "Sharma Dhaba",
    "period": "2024-01-01 to 2024-01-31",
    "totalOrders": 150,
    "grossRevenue": 75000.00,
    "totalFoodGst": 3750.00,
    "totalCgst": 1875.00,
    "totalSgst": 1875.00,
    "totalIgst": 0.00,
    "platformCommissionDeducted": 15000.00,
    "netEarningsToRestaurant": 56250.00,
    "platformEarnings": 18750.00,
    "avgOrderValue": 500.00
  },
  "chartData": [
    { "date": "2024-01-01", "grossRevenue": 2500.00, "netEarnings": 1875.00, "gst": 125.00 }
  ],
  "orders": {
    "content": [
      {
        "orderId": "ORD-123",
        "orderDate": "2024-01-01T10:30:00Z",
        "itemTotal": 500.00,
        "cgst": 12.50,
        "sgst": 12.50,
        "igst": 0.00,
        "totalGst": 25.00,
        "platformCommission": 100.00,
        "netPayout": 375.00
      }
    ],
    "totalPages": 8,
    "totalElements": 150
  }
}
```

---

## 5. Components to Build

| Layer | File | Description |
|-------|------|-------------|
| Entity | `RestaurantCommission.java` | Commission config per restaurant *(if approved)* |
| Repository | `OrderAnalyticsRepository.java` | JPQL/JpaSpecification queries with filters |
| Repository | `RestaurantCommissionRepository.java` | Fetch commission config *(if approved)* |
| DTOs | `RestaurantEarningsResponse.java` | Top-level analytics response |
| DTOs | `EarningsSummary.java` | Aggregated summary block |
| DTOs | `OrderEarningDetail.java` | Per-order earning detail |
| DTOs | `EarningsChartPoint.java` | Data point for chart rendering |
| DTOs | `EarningsFilterRequest.java` | Query filter params |
| Service | `RestaurantAnalyticsService.java` | Core calculation logic |
| Controller | `RestaurantAnalyticsController.java` | Endpoint exposure |

---

## 6. Open Questions / Decisions

> [!IMPORTANT]
> **Q1: Commission Rate Source** — DB entity (per restaurant) vs `application.yaml` (global)?

> [!IMPORTANT]
> **Q2: GST on Platform Fees** — Should the analytics also show GoDesii's own 18% GST liability (on platform fee + delivery) broken out separately, or just the food GST (restaurant's concern)?

> [!NOTE]
> **Q3: Who sees this API?** — Restaurant owner dashboard only, or also a GoDesii admin view showing platform-wide earnings?
