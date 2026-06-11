# 📖 GoDesii API Documentation

This directory contains the complete technical documentation for the GoDesii platform APIs.

---

## Documents

### [API_COMPLETE_DOCUMENTATION.md](API_COMPLETE_DOCUMENTATION.md)

**Comprehensive API Reference** covering all 50+ REST endpoints across the platform:

| Module | Endpoints Covered |
|--------|-------------------|
| **Authentication** | Login, token management |
| **Restaurant Management** | CRUD, operational hours, verification |
| **Menu & Items** | Items, categories, dietary types |
| **Order Management** | Create, status lifecycle, history |
| **Cart Management** | Smart cart with validations, price breakdown |

Includes request/response examples, validation rules, error codes, Postman setup, and a complete end-to-end test flow.

---

### [CART_API_DOCUMENTATION.md](CART_API_DOCUMENTATION.md)

**Deep-dive into the Cart subsystem** — the most complex transactional module:

- **11 API endpoints** documented with full payloads
- **System configuration** (expiry, packaging charges, GST, platform fee)
- **Business logic & validations** (same-restaurant constraint, availability checks, price drift detection)
- **Price calculation formulas** with worked examples
- **Error handling** — all cart-specific error scenarios with response bodies
- **Service layer** method reference

---

## Quick Links

| Topic | File |
|-------|------|
| Full API Reference | [API_COMPLETE_DOCUMENTATION.md](API_COMPLETE_DOCUMENTATION.md) |
| Cart Subsystem | [CART_API_DOCUMENTATION.md](CART_API_DOCUMENTATION.md) |
| Order Lifecycle | [../end_to_end_order_flow.md](../end_to_end_order_flow.md) |
| Analytics & GST | [../restaurant_analytics_analysis.md](../restaurant_analytics_analysis.md) |
| Postman Collection | [../GoDesii_API_Collection.postman_collection.json](../GoDesii_API_Collection.postman_collection.json) |

---

## Swagger / OpenAPI

When the application is running locally, access the interactive API explorer:

```
http://localhost:8081/swagger-ui.html
```

The OpenAPI spec is available at:

```
http://localhost:8081/v3/api-docs
```
