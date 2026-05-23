# H2 Console Login

Open:

```text
http://localhost:8080/h2-console
```

Use these exact values:

```text
Driver Class: org.h2.Driver
JDBC URL: jdbc:h2:file:./data/cs-job-radar-db
User Name: sa
Password:
```

Leave password blank.

If the console still shows `jdbc:h2:~/test`, replace it manually with the URL above.

## Useful Queries

```sql
SELECT * FROM JOB_POSTS;
```

```sql
SELECT * FROM JOB_POST_SKILLS;
```

```sql
SELECT * FROM ALERT_PREFERENCES;
```

```sql
SELECT * FROM ALERT_PREFERENCE_CHANNELS;
```

## What Is Stored

`JOB_POSTS` stores normalized job data such as title, organization, category, eligibility, posted date, last date, source URL, apply URL, and relevance score.

`JOB_POST_SKILLS` stores skills for each job.

`ALERT_PREFERENCES` stores alert form details.

`ALERT_PREFERENCE_CHANNELS` stores selected channels like email, Telegram, WhatsApp, and browser.
