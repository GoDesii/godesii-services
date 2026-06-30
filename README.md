<p align="center">
  <h1 align="center">🍛 VillSyn Services</h1>
  <p align="center">
    <strong>Full-stack food delivery platform backend— built for the Indian market</strong>
  </p>
  <p align="center">
    <a href="#getting-started">Getting Started</a> · 
    <a href="#api-reference">API Reference</a> · 
    <a href="#architecture">Architecture</a> · 
    <a href="#deployment">Deployment</a>
  </p>
</p>

---

## 📋 Overview

**VillSyn Services** is a production-grade Spring Boot backend powering the **VillSync** food delivery platform. It provides a complete suite of REST APIs for managing restaurants, menus, orders, carts, deliveries, real-time notifications, and Analytics — modeled after leading Indian food delivery platforms (Zomato/Swiggy) with full **Indian GST compliance**.

### ✨ Key Highlights

| Feature | Description |
|---------|-------------|
| 🏪 **Restaurant Management** | Full CRUD with operational hours, addresses, food certificates, reviews & nutritional info |
| 🛒 **Smart Cart System** | Auto-creation, expiry, same-restaurant constraint, real-time price validation |
| 📦 **Order Lifecycle** | Complete order flow with status tracking, GST breakdown, and COD/Online payments |
| 🚚 **Delivery Management** | Partner assignment, real-time tracking via WebSocket, and notification system |
| 📊 **Analytics Engine** | Per-restaurant earnings, GST calculations (CGST/SGST/IGST), commission management |
| 🔐 **JWT Authentication** | OTP-based login via Twilio, role-based access (USER, ADMIN, DELIVERY) |
| 🔍 **Full-Text Search** | Hibernate Search + Lucene for fuzzy food/restaurant discovery with synonym support |
| 🔔 **Push Notifications** | Firebase Cloud Messaging (FCM) for order & delivery updates |
| ☁️ **AWS Integration** | S3 for media storage, EC2 for deployment |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Client Applications                   │
│              (Mobile App / Web Dashboard)                │
└──────────────────────┬──────────────────────────────────┘
                       │  HTTPS / WebSocket
┌──────────────────────▼──────────────────────────────────┐
│                  Spring Boot 3.5.6                        │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌────────────┐  │
│  │   Auth   │ │Restaurant│ │  Order   │ │  Delivery  │  │
│  │ Module   │ │  Module  │ │  Module  │ │   Module   │  │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └─────┬──────┘  │
│       │             │            │              │         │
│  ┌────▼─────────────▼────────────▼──────────────▼──────┐ │
│  │              Service Layer                          │ │
│  │  (Business Logic, GST Calc, Commission, Search)     │ │
│  └────────────────────┬────────────────────────────────┘ │
│                       │                                   │
│  ┌────────────────────▼────────────────────────────────┐ │
│  │           Spring Data JPA + Hibernate Search         │ │
│  └────────────────────┬────────────────────────────────┘ │
└───────────────────────┼──────────────────────────────────┘
                        │
        ┌───────────────┼───────────────┐
        ▼               ▼               ▼
   ┌─────────┐   ┌───────────┐   ┌───────────┐
   │  MySQL  │   │  Lucene   │   │  AWS S3   │
   │   8.x   │   │  Indexes  │   │  Bucket   │
   └─────────┘   └───────────┘   └───────────┘
```

### Module Breakdown

| Module | Controllers | Key Entities |
|--------|-------------|--------------|
| **Authentication** | `AuthController`, `UserController`, `UserProfileController`, `ShippingAddressController` | `User`, `UserProfile`, `ShippingAddress`, `OTPVerification`, `SecureToken` |
| **Restaurant** | `RestaurantController`, `MenuController`, `MenuItemController`, `CategoryController`, `ReviewController`, `FoodCertificateController`, `NutritionalInfoController`, `RestaurantAnalyticsController` | `Restaurant`, `Menu`, `MenuItem`, `Category`, `Review`, `FoodCertificate`, `NutritionalInfo`, `OperationalHour`, `RestaurantCommission` |
| **Order** | `CartController`, `CartItemController`, `OrderController`, `OrderItemController`, `OrderNotificationController` | `Cart`, `CartItem`, `Order`, `OrderItem`, `OrderAddress`, `OrderGst`, `OrderStatus` |
| **Delivery** | `DeliveryController`, `DeliveryNotificationController` | `DeliveryPartner`, `DeliveryAssignment` |
| **Search** | `SearchController` | Hibernate Search full-text indices |
| **Infrastructure** | `AwsBucketController`, `FcmTokenController`, `LocationController` | `UserFcmToken` |

> **Total: 20+ Controllers · 35+ Entities · 50+ REST API Endpoints**

---

## 🛠️ Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| **Language** | Java | 17 |
| **Framework** | Spring Boot | 3.5.6 |
| **Build Tool** | Gradle (Groovy DSL) | 8.x |
| **Database** | MySQL | 8.x |
| **ORM** | Spring Data JPA / Hibernate | — |
| **DB Migrations** | Liquibase | 5.0.1 |
| **Search Engine** | Hibernate Search + Lucene | 7.2.2 |
| **Authentication** | Spring Security + JWT | — |
| **Validation** | Spring Boot Starter Validation | — |
| **API Docs** | SpringDoc OpenAPI (Swagger UI) | 2.8.9 |
| **SMS/OTP** | Twilio SDK | 11.0.0 |
| **Push Notifications** | Firebase Admin SDK (FCM) | 9.4.2 |
| **Object Storage** | AWS S3 (via AWS SDK) | 1.12.797 |
| **Real-time** | Spring WebSocket | — |
| **Serialization** | Gson | 2.13.1 |
| **Boilerplate** | Lombok | — |
| **Testing** | JUnit 5 + Spring Security Test | — |

---

## 📁 Project Structure

```
VillSyn-services/
├── .github/workflows/
│   └── deploy.yml                    # CI/CD — Auto-deploy to AWS EC2
├── database/
│   └── TablesScript                  # Legacy SQL schema reference
├── docs/
│   ├── API_COMPLETE_DOCUMENTATION.md # Full API reference (50+ endpoints)
│   └── CART_API_DOCUMENTATION.md     # Detailed cart subsystem docs
├── uml/
│   └── Digram                        # PlantUML class diagrams
├── src/
│   ├── main/
│   │   ├── java/com/VillSyn/VillSyn_services/
│   │   │   ├── VillSynServicesApplication.java
│   │   │   ├── common/               # APIResponse, APIError, FoodCategory
│   │   │   ├── config/               # App configs (Cart, S3, WebSocket, Firebase, etc.)
│   │   │   ├── constant/             # VillSynConstant
│   │   │   ├── controller/
│   │   │   │   ├── auth/             # Auth, User, Profile, Address controllers
│   │   │   │   ├── restaurant/       # Restaurant, Menu, MenuItem, Category, etc.
│   │   │   │   ├── order/            # Cart, Order, OrderItem, Notifications
│   │   │   │   ├── delivery/         # Delivery, DeliveryNotification
│   │   │   │   └── search/           # Global search controller
│   │   │   ├── dto/                  # Request/Response DTOs + analytics
│   │   │   ├── entity/
│   │   │   │   ├── auth/             # User, UserProfile, ShippingAddress, Roles
│   │   │   │   ├── restaurant/       # Restaurant, Menu, MenuItem, Category
│   │   │   │   ├── order/            # Cart, Order, OrderGst, OrderStatus
│   │   │   │   ├── delivery/         # DeliveryPartner, DeliveryAssignment
│   │   │   │   └── payment/          # PaymentMethod
│   │   │   ├── exception/            # Custom exceptions + GlobalExceptionHandler
│   │   │   ├── repository/           # JPA repositories (auth, order, restaurant, delivery)
│   │   │   ├── security/             # JWT, SecurityConfig, UserPrincipal
│   │   │   └── service/              # Business logic services
│   │   └── resources/
│   │       ├── application.yaml      # Base config (GST, commission defaults)
│   │       ├── application-dev.yaml  # Dev profile (local MySQL, Twilio, S3, JWT)
│   │       ├── application-prod.yaml # Production profile
│   │       ├── application-qa.yaml   # QA profile
│   │       ├── db/changelog/         # Liquibase migrations
│   │       │   ├── db.changelog-master.xml
│   │       │   ├── initialization/   # DDL + DML base schema
│   │       │   └── migrate/          # Versioned migrations (v1.0.0, v2.0.0)
│   │       ├── food_synonyms.txt     # Search synonyms (biryani ↔ pulao, etc.)
│   │       └── logback.xml           # Logging configuration
│   └── test/                         # Unit & integration tests
├── build.gradle                      # Build config with Liquibase tasks
├── settings.gradle                   # rootProject.name = 'VillSyn-services'
├── end_to_end_order_flow.md          # Complete order lifecycle walkthrough
├── restaurant_analytics_analysis.md  # Analytics & GST design document
└── VillSyn_API_Collection.postman_collection.json  # Postman collection
```

---

## 🚀 Getting Started

### Prerequisites

| Requirement | Version | Notes                          |
|------------|---------|--------------------------------|
| **Java JDK** | 17+ | JDK                            |
| **MySQL** | 8.x | Ensure service is running      |
| **Gradle** | 8.x | Wrapper included (`./gradlew`) |

### 1. Clone the Repository

```bash
git clone https://github.com/VillSyn/VillSyn-services.git
cd VillSyn-services
```

### 2. Configure the Database

Create the MySQL database (auto-created if enabled):

```sql
CREATE DATABASE VillSyn_dev;
```

Update credentials in `src/main/resources/application-dev.yaml` if your local MySQL uses different credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/VillSyn_dev?createDatabaseIfNotExist=true
    username: root
    password: your_password
```

### 3. Run Database Migrations

The project uses **Liquibase** for schema management with a versioned migration strategy:

```bash
# Run all migrations (initialization + versioned)
./gradlew update -PrunList=master

# Or run specific migration steps
./gradlew update -PrunList=initDdl    # Initialization DDL only
./gradlew update -PrunList=initDml    # Initialization DML (seed data)
```

### 4. Build & Run

```bash
# Build the project (skip tests for faster startup)
./gradlew clean build -x test

# Run the application
./gradlew bootRun
```

The server starts on **`http://localhost:8081`** (configured in `application.yaml`).

### 5. Access Swagger UI

Once running, explore the interactive API docs:

```
http://localhost:8081/swagger-ui.html
```

---

## 📡 API Reference

### Quick Endpoint Map

#### 🔐 Authentication (`/auth`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/auth/login` | Authenticate & get JWT token |

#### 👤 User Management (`/api/v1`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/users/register` | Register new user |
| `GET/PUT` | `/api/v1/users/profile` | Manage user profile |
| `CRUD` | `/api/v1/shipping-addresses` | Manage delivery addresses |

#### 🏪 Restaurant (`/api/v1/restaurants`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/restaurants/create` | Create restaurant |
| `GET` | `/api/v1/restaurants` | List all (paginated) |
| `GET` | `/api/v1/restaurants/{id}` | Get by ID |
| `PUT` | `/api/v1/restaurants/{id}` | Update restaurant |
| `DELETE` | `/api/v1/restaurants/{id}` | Delete restaurant |

#### 🍽️ Menu & Items (`/api/v1/menu-items`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/menu-items` | Create menu item |
| `GET` | `/api/v1/menu-items/category/{id}` | Get by category |
| `PUT` | `/api/v1/menu-items/{id}` | Update item |
| `DELETE` | `/api/v1/menu-items/{id}` | Delete item |

#### 🛒 Cart (`/api/v1/carts`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/carts/add-item` | Add item to cart (smart) |
| `PUT` | `/api/v1/carts/{cartId}/items/{itemId}` | Update quantity |
| `DELETE` | `/api/v1/carts/{cartId}/items/{itemId}` | Remove item |
| `GET` | `/api/v1/carts/active/user/{userId}` | Get active cart |
| `DELETE` | `/api/v1/carts/{cartId}/clear` | Clear cart |

#### 📦 Orders (`/api/v1/orders`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/orders/place` | Place order from cart |
| `GET` | `/api/v1/orders/user/{username}` | Order history |
| `PATCH` | `/api/v1/orders/{id}/status` | Update order status |
| `GET` | `/api/v1/orders/{id}` | Get order details |

#### 🚚 Delivery (`/api/v1/delivery`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/v1/delivery/assign` | Assign delivery partner |
| `PUT` | `/api/v1/delivery/{id}/status` | Update delivery status |

#### 📊 Analytics (`/api/v1/analytics`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/analytics/restaurant/{id}/earnings` | Earnings analytics |
| `PUT` | `/api/v1/analytics/restaurant/{id}/commission` | Set commission rate |

#### 🔍 Search
| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/search` | Global fuzzy search (food + restaurants) |

> 📄 **Full API documentation**: See [`docs/API_COMPLETE_DOCUMENTATION.md`](docs/API_COMPLETE_DOCUMENTATION.md) and [`docs/CART_API_DOCUMENTATION.md`](docs/CART_API_DOCUMENTATION.md)

---

## 💰 Indian GST & Commission Model

VillSyn implements the **Indian GST food aggregator model** (as per Zomato/Swiggy):

### GST Streams

| Stream | Rate | Liability | Applied On |
|--------|------|-----------|------------|
| **Food GST** | 5% (2.5% CGST + 2.5% SGST) | Restaurant | `itemTotal` |
| **Platform GST** | 18% (9% CGST + 9% SGST) | VillSyn | `platformFee + deliveryFee` |

### Earnings Split Example (₹1,000 order)

```
Customer Pays:
  Item Total          ₹1,000.00
  Food GST (5%)      ₹   50.00
  Delivery Fee       ₹   40.00
  Platform Fee       ₹    5.00
  Packaging          ₹   10.00
  ─────────────────────────────
  Total              ₹1,105.00

Restaurant Gets:
  Item Total          ₹1,000.00
  − Commission (22%)  ₹  220.00
  ─────────────────────────────
  Net Payout          ₹  780.00

VillSyn Gets:
  Commission          ₹  220.00
  + Platform Fees     ₹   45.00
  − Platform GST      ₹    8.10
  ─────────────────────────────
  Net Revenue         ₹  256.90
```

> 📄 **Detailed breakdown**: See [`restaurant_analytics_analysis.md`](restaurant_analytics_analysis.md) and [`end_to_end_order_flow.md`](end_to_end_order_flow.md)

---

## 🗄️ Database

### Schema Management

The project uses **Liquibase** with a structured migration strategy:

```
db/changelog/
├── db.changelog-master.xml          # Master changelog (includes all)
├── initialization/
│   ├── ddl.xml                      # Base schema (all tables)
│   └── dml.xml                      # Seed data (roles, categories, etc.)
└── migrate/
    ├── v1.0.0/                      # Version 1 migrations
    └── v2.0.0/                      # Version 2 migrations
```

### Key Gradle Tasks

```bash
./gradlew update -PrunList=master              # Run full migration
./gradlew generateAllSql                       # Generate all SQL files
./gradlew generateInitDdlSql                   # Generate initialization DDL
./gradlew generateMigrateV100DdlSql            # Generate v1.0.0 DDL
./gradlew generateMigrationScript              # Generate master SQL (legacy)
```

### Core Tables

| Table | Description |
|-------|-------------|
| `users` | User accounts with role-based access |
| `user_profiles` | Extended user information |
| `restaurants` | Restaurant profiles with verification status |
| `menu_items` | Food items with pricing and dietary info |
| `categories` | Menu categories |
| `operational_hours` | Restaurant operating schedules |
| `carts` / `cart_items` | Shopping cart with expiry |
| `orders` / `order_items` | Order records with price snapshots |
| `order_gst` | Per-order GST breakdown (CGST/SGST/IGST) |
| `restaurant_commission` | Per-restaurant commission configuration |
| `delivery_partners` | Delivery personnel |
| `delivery_assignments` | Order-to-partner assignments |
| `payments` | Payment transactions (Razorpay integration) |
| `notifications` | Push notification records |

---

## 🌍 Environment Profiles

| Profile | Config File | Purpose |
|---------|-------------|---------|
| `dev` | `application-dev.yaml` | Local development (default active) |
| `qa` | `application-qa.yaml` | QA/staging environment |
| `prod` | `application-prod.yaml` | Production deployment |

Switch profiles:

```bash
# Via environment variable
SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun

# Via JVM argument
./gradlew bootRun --args='--spring.profiles.active=qa'
```

### Global Configuration (`application.yaml`)

```yaml
VillSyn:
  tax:
    gst:
      food-percentage: 5.0        # 5% GST on food
      platform-percentage: 18.0   # 18% GST on platform services
  commission:
    default-percentage: 20.0      # Default commission (override per-restaurant in DB)
```

---

## 🚢 Deployment

### CI/CD Pipeline

The project uses **GitHub Actions** for automated deployment to **AWS EC2**:

```
Push to `villsyn-qa` branch
    → Checkout code
    → Setup JDK 17
    → Build JAR (./gradlew clean build -x test)
    → SCP JAR to EC2
    → Restart systemd service
```

**Trigger**: Any push to the `villsyn-qa` branch automatically deploys to the QA environment.

### Manual Deployment

```bash
# Build production JAR
./gradlew clean build -x test

# Copy to server
scp build/libs/VillSyn-services-0.0.1-SNAPSHOT.jar user@server:/home/ubuntu/app/

# On the server
sudo systemctl restart springboot
```

### Required GitHub Secrets & Variables

| Type | Name | Description |
|------|------|-------------|
| Secret | `PROD_EC2_KEY` | SSH private key for EC2 access |
| Variable | `PROD_EC2_HOST` | EC2 instance public IP/hostname |

---

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run with detailed output
./gradlew test --info
```

### Postman Collection

Import the included Postman collection for manual API testing:

```
VillSyn_API_Collection.postman_collection.json
```

Set up environment variables in Postman:

| Variable | Value |
|----------|-------|
| `baseUrl` | `http://localhost:8081` |
| `apiVersion` | `/api/v1` |
| `authToken` | `{{token}}` (auto-set after login) |

---

## 📚 Documentation

| Document | Description |
|----------|-------------|
| [`docs/API_COMPLETE_DOCUMENTATION.md`](docs/API_COMPLETE_DOCUMENTATION.md) | Full API reference with 50+ endpoints, request/response examples |
| [`docs/CART_API_DOCUMENTATION.md`](docs/CART_API_DOCUMENTATION.md) | Detailed cart subsystem — business rules, validations, price formulas |
| [`end_to_end_order_flow.md`](end_to_end_order_flow.md) | Complete order lifecycle: restaurant creation → order → analytics |
| [`restaurant_analytics_analysis.md`](restaurant_analytics_analysis.md) | Analytics design — Indian GST model, commission architecture |
| [`uml/Digram`](uml/Digram) | PlantUML class diagrams for all modules |

---

## 🤝 Contributing

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'feat: add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### Branch Strategy

| Branch | Purpose |
|--------|---------|
| `main` | Stable production code |
| `villsyn-qa` | QA/staging — auto-deploys on push |
| `feature/*` | Feature development branches |

---

## 📄 License

This project is proprietary. All rights reserved by **VillSyn**.

---
<p align="center">
  Built with ❤️ by the <strong>VillSyn</strong> team
</p>
