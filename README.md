# JPStudy Backend Server

## 🛠 Tech Stack
### Environment
- Language: Java 21
- Framework: Spring Boot 3.4.9
- Build Tool: Gradle 8.7

### Database & Storage
- RDBMS: PostgreSQL (Production), H2 (Test)
- NoSQL (Cache/Session): Redis
- Migration: Flyway
- Object Storage: Cloudflare R2 (S3 Compatible)

### Messaging & Async
- Message Broker: RabbitMQ
- Event Handling: Spring Event, SSE (Server-Sent Events)

### Security
- Auth: Spring Security, JWT (Access/Refresh Token)
- OAuth2: Google Login

### Infra & DevOps
- CI/CD: Jenkins, Docker, Kubernetes
- Cloud: Azure Communication Email Service



## Getting Started

### Prerequisites
- JDK 21+
- Docker & Docker Compose (Redis, PostgreSQL, RabbitMQ)

### Environment Variables (.env or System Properties)
```yaml
# Database
POSTGRES_URL=jdbc:postgresql://localhost:5432/jpstudy
POSTGRES_USER=your_user
POSTGRES_PASSWORD=your_password

# Redis
REDIS_HOST=localhost
REDIS_PASSWORD=your_redis_password

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# JWT
JWT_SECRET_KEY=your_secret_key_must_be_long_enough

# OAuth2 (Google)
GOOGLE_CLIENT_ID=your_client_id
GOOGLE_CLIENT_SECRET=your_client_secret

# Cloudflare R2 (Storage)
CLOUDFLARE_ACCOUNT_ID=your_account_id
CLOUDFLARE_ACCESS_KEY=your_access_key
CLOUDFLARE_SECRET_KEY=your_secret_key
CLOUDFLARE_BUCKET_NAME=your_bucket_name

# Azure Email
AZURE_EMAIL_ENDPOINT=your_connection_string
AZURE_EMAIL_SENDER_ADDRESS=noreply@yourdomain.com

# Service URIs
BACKEND_URI=http://localhost:8080
FRONTEND_URI=http://localhost:3000
```

## Installation & Run
### 1. Clone the repository
```shell
git clone https://github.com/jpstudy-org/jpstudy-backend.git
cd jpstudy-backend
```

### 2. Build
```shell
./gradlew clean build
```

### 3. Run
```shell
java -jar build/libs/app.jar
```

## API Documentation
```text
http://localhost:8080/swagger-ui.html
```