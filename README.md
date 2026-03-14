# Bulk Transfer API

A simple API that processes **bulk transfer transactions from a CSV file** and stores them in a **PostgreSQL database**.  
The service is containerized using **Docker**.

---

# Running the Project

## 1. Start PostgreSQL using Docker

Run the following command to start the PostgreSQL container:

```bash
docker run -d --name postgres_db_phase3 -e POSTGRES_DB=postgres -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=123456 -p 5432:5432 postgres:15
```

PostgreSQL configuration:

| Parameter | Value |
|----------|------|
| Database | postgres |
| User | postgres |
| Password | 123456 |
| Port | 5432 |

---

## 2. Build the API Docker Image

Navigate to the project directory and run:

```bash
docker build -t bulkapi .
```

This builds the Docker image named **bulkapi**.

---

## 3. Run the API Container

Start the API container:

```bash
docker run -p 8080:8080 bulkapi
```

The API will run at:

```
http://localhost:8080
```

---

# API Usage

## Bulk Transfer Endpoint

```
POST /api/bulktransfer
```

Uploads a CSV file containing multiple transfer requests.

---

## Send Request using curl

Run the following command:

```bash
curl.exe -X POST "http://localhost:8080/api/bulktransfer" -F "file=@csv/bulk_transfers.csv"
```

---

## Example CSV File

Example `bulk_transfers.csv` format:

```
from_account,to_account,amount
1001,2001,50
1002,2003,120
1005,2007,30
```

---

# Docker Commands

## Check running containers

```bash
docker ps
```

## Stop PostgreSQL container

```bash
docker stop postgres_db_phase3
```

## Remove PostgreSQL container

```bash
docker rm postgres_db_phase3
```

---

# Notes

- Make sure **PostgreSQL is running before starting the API container**.
- The CSV file must follow the required format.
- Ensure the API database configuration points to the PostgreSQL container.

---