# Production Roadmap

## Best Free 24/7 Hosting Choice

Use Oracle Cloud Always Free VPS for the Spring Boot app.

Why:

- It can run independently of your laptop.
- Your site remains live when your laptop is off.
- It is closer to true 24/7 than hobby web-service free tiers.

Use Neon or Supabase free Postgres for database.

## Easy Free Preview Choice

Render free web service + Neon Postgres.

Tradeoff:

- Easier deployment.
- Good for resume/demo.
- Free web service has platform limits and should not be treated as serious production.

## Environment Variables

```text
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://<host>/<db>?sslmode=require
DATABASE_USERNAME=<postgres_user>
DATABASE_PASSWORD=<postgres_password>
ADMIN_USERNAME=admin
ADMIN_PASSWORD=<strong_password>
APP_PUBLIC_URL=https://your-domain
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=<gmail>
MAIL_PASSWORD=<gmail_app_password>
TELEGRAM_BOT_TOKEN=<bot_token>
TWILIO_ACCOUNT_SID=<twilio_sid>
TWILIO_AUTH_TOKEN=<twilio_token>
TWILIO_WHATSAPP_FROM=whatsapp:+14155238886
```

## Current Implemented Production Features

- PostgreSQL production profile.
- Dockerfile.
- Render deploy config.
- Admin basic auth.
- Saved jobs API.
- Daily email digest scheduler.
- SarkariResult source adapter.
- Company careers adapter.
- Separate helper tabs for Naukri and LinkedIn.

## Notes on Naukri and LinkedIn

Do not blindly scrape LinkedIn/Naukri pages in production. They are dynamic, rate-limited, and may restrict scraping. The current app keeps them in separate helper tabs and opens filtered external searches. For production-grade integration, add compliant APIs, partner feeds, or user-authorized integrations.

## Notes on WhatsApp

Free WhatsApp automation is limited. Twilio has trial credits, but production WhatsApp notifications usually need paid approval/usage. Telegram bot and email alerts are better free-first options.

## Notes on Browser Push

Browser push requires HTTPS and VAPID keys. It can be added after deployment because localhost/H2 is not the right final setup for it.

## Local PostgreSQL

Your laptop has PostgreSQL service installed. To run with local Postgres:

1. Create database `cs_job_radar` in pgAdmin.
2. Update `application-local-postgres.yml` password if needed.
3. Run with profile:

```powershell
& "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2025.1.2\plugins\maven\lib\maven3\bin\mvn.cmd" spring-boot:run "-Dspring-boot.run.profiles=local-postgres"
```

## Admin API

```text
GET /api/admin/stats
```

Login with:

```text
ADMIN_USERNAME
ADMIN_PASSWORD
```

## Saved Jobs API

```text
POST /api/saved-jobs
GET  /api/saved-jobs?userEmail=you@example.com
DELETE /api/saved-jobs
```

Payload:

```json
{
  "userEmail": "you@example.com",
  "jobId": 1
}
```
