# Tool 2 - CS Job Radar Spring Boot

Java Spring Boot implementation for a resume-ready CS/CSE job aggregation platform.

## Problem Statement

Students miss relevant jobs because portals contain too many mixed posts. This tool collects public job listings, extracts eligibility, scores CS/CSE relevance, stores normalized jobs, and sends alerts through email, Telegram, and WhatsApp adapter hooks.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 database for local demo
- Jsoup scraper
- Scheduled background refresh
- Email, Telegram, and WhatsApp notification adapter points
- Static dashboard served by Spring Boot

## Run

Install Maven, then:

```bash
mvn spring-boot:run
```

Open:

```text
http://localhost:8080
```

## API

```text
GET  /api/jobs
GET  /api/jobs?query=java&category=Government&type=Full%20Time
POST /api/jobs/refresh
POST /api/alerts
```

## Deployment Options

- Render: Java web service, build command `mvn clean package`, start command `java -jar target/cs-job-radar-0.0.1-SNAPSHOT.jar`
- Railway: Java service from GitHub
- VPS: run the packaged jar behind Nginx

Use environment variables for notification credentials:

```text
MAIL_HOST=
MAIL_PORT=
MAIL_USERNAME=
MAIL_PASSWORD=
TELEGRAM_BOT_TOKEN=
TWILIO_ACCOUNT_SID=
TWILIO_AUTH_TOKEN=
TWILIO_WHATSAPP_FROM=
```
