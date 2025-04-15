# Stock Price API

A Spring Boot application that fetches and stores stock price data using the Polygon.io API.

## Features

- Fetch stock data from Polygon.io API
- Store stock data in PostgreSQL database
- REST API endpoints for stock data retrieval
- Comprehensive monitoring and metrics
- Integration tests with Testcontainers

## Prerequisites

- Java 17
- Maven
- Docker (for Testcontainers)
- PostgreSQL
- Polygon.io API key

## Setup

1. Clone the repository:
```bash
git clone <repository-url>
cd stock-api
```

2. Set up environment variables:
```bash
export POLYGON_API_KEY=your_api_key_here
```

3. Configure database in `application.yml`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/stock_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

4. Build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```

## Docker Setup

Alternatively, you can use Docker Compose to run the application and 
PostgreSQL database

### Build the image application
```bash
docker-compose build
```
### Run
```bash
docker-compose up -d
```

This will start:
- The Spring Boot application on port 8080
- PostgreSQL database on port 5432

### Stop
```bash
docker-compose stop
```

## API Endpoints

### Stock Data

- `POST /stocks/fetch?companySymbol={symbol}&fromDate={date}&toDate={date}`
  - Fetches and saves stock data for the specified company and date range
  - Example: `/stocks/fetch?companySymbol=AAPL&fromDate=2024-03-01&toDate=2024-03-02`
  - Response:
      ```json
        [
            {
                "companySymbol": "AAPL",
                "date": "2024-03-01",
                "openPrice": 179.55,
                "closePrice": 179.66,
                "highPrice": 180.53,
                "lowPrice": 177.38,
                "volume": 73450582
            }
        ]
   ```

- `GET /stocks/{companySymbol}?date={date}`
  - Retrieves stock data for a specific company and date
  - Example: `/stocks/AAPL?date=2024-01-01`
  - Response:
      ```json
      {
         "companySymbol": "AAPL",
         "date": "2024-01-01",
         "openPrice": 174.25,
         "closePrice": 176.10,
         "highPrice": 178.00,
         "lowPrice": 172.80,
         "volume": 58900000
      }
   ```


## Monitoring and Metrics

The application includes Spring Boot Actuator for monitoring and metrics. The following endpoints are available:

### Health Checks
- `GET /actuator/health`
  - Overall application health status
  - Database connectivity
  - Disk space
  - Polygon API availability

### Metrics
- `GET /actuator/metrics`
  - Application metrics including:
    - `stock.fetch.count`: Number of stock data fetch operations
    - `stock.save.count`: Number of stock data save operations
    - `stock.notfound.count`: Number of stock not found errors


## Testing

The project includes comprehensive tests:

1. Unit Tests:
```bash
mvn test
```

2. Integration Tests:
```bash
mvn test -Dtest=StockServiceImplIntegrationTest
```

Integration tests use Testcontainers to run a PostgreSQL database in Docker.

## Code Coverage

Code coverage reports are generated using JaCoCo. After running tests, view the report at:
```
target/site/jacoco/index.html
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/sstjerne/stockapi/
│   │       ├── config/         # Configuration classes
│   │       ├── controller/     # REST controllers
│   │       ├── dto/           # Data Transfer Objects
│   │       ├── entity/        # JPA entities
│   │       ├── exception/     # Custom exceptions
│   │       ├── health/        # Health indicators
│   │       ├── metrics/       # Custom metrics
│   │       ├── repository/    # JPA repositories
│   │       ├── service/       # Service interfaces
│   │       └── service/impl/  # Service implementations
│   └── resources/
│       └── application.properties
└── test/
    └── java/
        └── com/sstjerne/stockapi/
            ├── config/         # Configuration tests
            ├── controller/     # Controller tests
            ├── repository/     # Repository tests
            ├── service/        # Service tests
            └── service/impl/   # Service implementation tests
```

## Dependencies

- Spring Boot 3.2.3
- Spring Data JPA
- PostgreSQL
- Spring Boot Actuator
- Testcontainers
- Lombok
- WebFlux (for reactive HTTP client)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details. 