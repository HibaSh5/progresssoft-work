# Phase 2 – CliqTransferAPIs

This phase provides **REST APIs** that allow users to perform CliQ money transfers programmatically.

The application is implemented using **Spring Boot** and **Maven**, and it exposes REST endpoints for retrieving accounts, listing transfers, and executing CliQ transfers.

The application can be **built and run using Docker**.

### Run PostgreSQL with Docker

Start PostgreSQL using Docker:

docker run -d --name postgres-db --network phase2-postgres -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=123456 -e POSTGRES_DB=postgres -v ./postgres_container_data:/var/lib/postgresql/data postgres:15-alpine

## Running the Application with Docker

Build the Docker image:

docker build -t phase2 .

Run the container:

docker run -p 8080:8080 phase2

## Available APIs

### List of Accounts
GET http://localhost:8080/api/accounts

Returns the list of available accounts.

### List of Transfers
GET http://localhost:8080/api/transfers/list

Returns the history of transfers.

### Perform CliQ Transfer
POST http://localhost:8080/api/transfers/cliq

Allows users to perform a money transfer using the CliQ transfer process.