# 🗄️ Database

This directory contains the legacy SQL schema reference for the GoDesii platform.

> [!NOTE]
> The project now uses **Liquibase** for schema management. The active migration scripts are located at:
> ```
> src/main/resources/db/changelog/
> ```

---

## Files

### [TablesScript](TablesScript)

Legacy SQL DDL script containing the initial table definitions for:

| Table | Description |
|-------|-------------|
| `users` | User accounts with phone, email, role (USER/ADMIN/DELIVERY) |
| `otp_verification` | OTP codes for phone-based authentication |
| `user_profiles` | Extended user info (name, profile picture) |
| `addresses` | User address book with geolocation |
| `restaurants` | Restaurant profiles with cuisine and rating |
| `menu_items` | Food items linked to restaurants |
| `cart_items` | Shopping cart entries |
| `orders` | Order records with payment/order status |
| `order_items` | Individual items within an order |
| `payments` | Razorpay payment transactions |
| `delivery_tracking` | Real-time delivery location tracking |
| `notifications` | SMS/Push notification log |

---

## Current Schema Management

The project uses **Liquibase 5.0.1** with a versioned migration strategy:

```
src/main/resources/db/changelog/
├── db.changelog-master.xml       # Master changelog
├── initialization/
│   ├── ddl.xml                   # Full base schema
│   └── dml.xml                   # Seed data
└── migrate/
    ├── v1.0.0/                   # Version 1 migrations
    └── v2.0.0/                   # Version 2 migrations
```

### Useful Gradle Commands

```bash
# Run full migration
./gradlew update -PrunList=master

# Generate SQL without executing
./gradlew generateAllSql

# Run initialization only
./gradlew update -PrunList=initDdl
./gradlew update -PrunList=initDml
```

See the [main README](../README.md#-database) for more details.
