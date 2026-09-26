# Tool 2 - CS Job Radar Spring Boot

Java Spring Boot implementation for a resume-ready CS/CSE job aggregation platform.

## Problem Statement

Students miss relevant jobs because portals contain too many mixed posts. This tool scans current SarkariResult recruitment posts, matches actual CS/CSE-related qualifications, stores normalized jobs, and sends new-listing alerts through email or WhatsApp.

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- H2 database for local demo
- Jsoup scraper
- Scheduled 30-minute background refresh plus manual refresh
- PostgreSQL-ready upsert flow that preserves jobs and detects newly discovered listings
- SMTP email alerts and Twilio WhatsApp alerts
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
APP_PUBLIC_URL=
```

For Gmail, create a Google App Password and set `MAIL_HOST=smtp.gmail.com`, `MAIL_PORT=587`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_SMTP_AUTH=true`, and `MAIL_STARTTLS_ENABLE=true`. WhatsApp delivery requires a Twilio WhatsApp sender and its account credentials; this provider is not an unlimited free service.

## Live Refresh Behaviour

Each scan collects current SarkariResult recruitment links, opens each post, requires a degree plus a CS-related specialization, rejects expired last dates, and upserts by source URL. New URLs are published in the dashboard and matched against users' alert preferences. The matcher covers Computer Science, CSE, Computer Engineering, IT, Computer Applications, Software Engineering, Cyber Security, AI/ML, Data Science, Cloud, Networking, BCA, MCA, BSc CS, BE, and BTech wording.
