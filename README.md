# Price Comparator - Market

A Spring Boot backend application that helps users make smarter grocery shopping decisions by tracking prices, detecting discounts, suggesting substitutes, and optimizing baskets for cost savings across multiple stores.

## Features
- **Basket Optimization**  
  Optimize for the lowest cost:  
  (1) choose the best single store  
  (2) mix stores for the cheapest item prices
-  **Dynamic Price History**  
  View product price trends over time, with filters by name, brand, category, or store.
-  **Top & New Discounts**  
  View highest-percentage and newly added discounts across all stores.
-  **Smart Substitutes**  
  Find cost-effective substitutes by unit price within the same category.

## Project Structure

### Controllers (`controller/`)
REST API endpoints organized by domain:
- `AlertController` – Price alert creation and retrieval
- `BasketController` – Basket optimization (single store and split)
- `DiscountController` – All, best, and new discounts
- `ProductController` – Product listing, history, and substitutes

### Services (`service/`)
Contains business logic:
- `AlertService`, `BasketService`, `DiscountService`, `ProductService`, `DataLoaderService`

### Repositories (`repository/`)
Spring Data JPA interfaces:
- `ProductRepository`, `DiscountRepository`, `PriceAlertRepository`

### Domain Models (`domain/`)
Entities and composite IDs:
- `Product`, `Discount`, `PriceAlert`
- `ProductId`, `DiscountId`

### Data Transfer Objects (`dto/`)
Structured request/response models:
- `BasketRequest`, `PriceAlertRequest`, `ApiResponse`

### Exception Handling (`exception/`)
Centralized error management:
- `GlobalExceptionHandler` – Validation and conversion issues
- `FallbackHandler` – Invalid or unhandled routes

## Testing Structure

All tests are under `src/test/java`.

- **Controller Tests**: Use `MockMvc` to verify HTTP request/response.
- **Service Tests**: Unit tests with mocked repositories.
- **DTO Tests**: Validate constraints like `@NotNull`, `@Positive`, `@NotEmpty`.
- **Domain Tests**: Test utility methods in models (e.g., unit price).
- **Exception Handler Tests**: Verify API error responses.

---

## Instructions

### This project was created with:
- Java 22 
- Maven 3.9.7 
- Git 2.45.1

### Build & Run
1. **Clone the repository**
git clone https://github.com/Alexia-Claudia-Micu/AccesaJavaInternship2025.git
cd .\AccesaJavaInternship2025\
2. **Build the project**
./mvnw clean install
3. **Run the application**
./mvnw spring-boot:run
The application will start on http://localhost:8080
4. **Running Tests**
./mvnw test

---

## Assumptions and Simplifications

- **In-memory data only**: The application uses an in-memory database (H2) with CSV bootstrap files for simplicity; no external database is required.
- **No authentication or user accounts**: All features are open and not tied to specific users (e.g., price alerts are global).
- **Fixed currency and units**: Assumes all prices are in the same currency and quantities use consistent units for comparison.
- **No real-time notifications**: Price alerts are evaluated on request (via `/alerts/active`) rather than pushed via email or webhook.
- **Simplified substitute logic**: Product substitutes are matched by category and price/unit margin, but ignore other qualitative factors like brand preference.
- **Discount validity**: Discounts are considered active only if today's date is between `fromDate` and `toDate`, inclusive.
- **No pagination or filtering** on list endpoints: All products, discounts, and alerts are returned in full unless manually filtered by query params.

---

## API Endpoints

Below is a list of all available endpoints with example requests.

### Products

- `GET /products`  
  Retrieve all products.

- `GET /products?store={store}`  
  Retrieve products for a specific store.

- `GET /products/{productId}/price-history`  
  Get the price history of a product by ID.

- `GET /products/{productId}/price-history?store={store}`  
  Price history for a product at a specific store.

- `GET /products/name/price-history?name={name}`  
  Price history for products matching the name (partial match supported).

- `GET /products/name/price-history?name={name}&store={store}`  
  Filter price history by name and store.

- `GET /products/name/price-history?name={name}&brand={brand}&category={category}`  
  Full filtering by name, brand, and category.

- `GET /products/{productId}/substitutes`  
  Get recommended substitutes for a product (default margin = 10%).

- `GET /products/{productId}/substitutes?margin={N}`  
  Substitute search with custom margin percentage.

---

### Discounts

- `GET /discounts`  
  Retrieve all current discounts.

- `GET /discounts/best`  
  Get top 5 discounts by percentage (default).

- `GET /discounts/best?top={N}`  
  Get top N best discounts.

- `GET /discounts/new`  
  Discounts added in the last 24 hours.

---

### Basket Optimization

- `POST /basket/optimize`  
  Optimize basket for a single store:
  ```json
  {
    "productIds": ["P001", "P003", "P009"]
  }

- `POST /basket/split-optimize`  
  Optimize basket by selecting cheapest store per product:
  ```json
  {
    "productIds": ["P001", "P003", "P009"]
  }

---

### Price Alerts

- `POST /alerts`  
  Create a custom price alert:
  ```json
  {
  "productId": "P003",
  "storeName": "Kaufland",
  "targetPrice": 17.50
  }

- `GET /alerts/active`  
  Retrieve alerts where current prices are at or below the target.
