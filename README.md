# Phase 2 – CliqTransferAPIs

This phase provides **REST APIs** that allow users to perform **CliQ money transfers programmatically**.

The application is implemented using **Spring Boot** and **Maven**, and it exposes REST endpoints for:

- Retrieving accounts
- Listing transfers
- Executing CliQ transfers

The application can be **built and run using Docker**.

---

# Requirements

Make sure you have the following installed:

- Docker
- Docker Desktop (or Docker Engine)
- Git

---

# Run PostgreSQL with Docker

Start PostgreSQL using Docker:

```bash
docker run -d --name postgres-db --network phase2-postgres -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=123456 -e POSTGRES_DB=postgres -v ./postgres_container_data:/var/lib/postgresql/data postgres:15-alpine
```

This starts a PostgreSQL container with persistent storage.

---

# Running the Application with Docker

## Build the Docker Image

```bash
docker build -t phase2 .
```

## Run the Container

```bash
docker run -p 8080:8080 phase2
```

The API will be available at:

```
http://localhost:8080
```

---

# Available APIs

## 1. List of Accounts

```
GET /api/accounts
```

Example:

```
http://localhost:8080/api/accounts
```

Returns the list of available accounts.

---

## 2. List of Transfers

```
GET /api/transfers/list
```

Example:

```
http://localhost:8080/api/transfers/list
```

Returns the history of transfers.

---

## 3. Perform CliQ Transfer

```
POST /api/transfers/cliq
```

Example:

```
http://localhost:8080/api/transfers/cliq
```

Allows users to perform a **money transfer using the CliQ transfer process**.

---

# Notes

- Ensure **PostgreSQL is running before starting the application container**.
- The API connects to the PostgreSQL database configured in the application.
- Default application port: **8080**.

---