# ZubarskaOrdinacija Backend

[![Java](https://img.shields.io/badge/Java-17-blue?logo=openjdk)](https://openjdk.org/projects/jdk/17/)

A Spring Boot 2.7 REST API for managing a dental clinic — patients, dentists, and appointment scheduling.

---

## Live Demo & Frontend

Live Demo: [https://zaricu22.github.io/ZubarskaOrdinacijaFrontend/](https://zaricu22.github.io/ZubarskaOrdinacijaFrontend/)

Source: [https://github.com/zaricu22/ZubarskaOrdinacijaFrontend](https://github.com/zaricu22/ZubarskaOrdinacijaFrontend)

---

## Tech Stack

| | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 2.7.3 |
| Persistence | Spring Data JPA + PostgreSQL (CockroachDB in production) |
| Build | Maven |
| Containerization | Docker |
| Deploy | Render |

---

## Database

Schema and seed data scripts are in `docs/db/`:

| File | Purpose |
|------|---------|
| `dental_init.sql` | Creates the `dental` database, `public` schema, and all tables |
| `dental_seed-data.sql` | Sample patients, dentists, and appointments |

Run `dental_init.sql` first (as superuser to create the database), then `dental_seed-data.sql`.

> **Seed users**
>
> | ID Number | Name | Role |
> |-----------|------|------|
> | `0213456789` | Janko Jankovic | pacijent |
> | `02144556677` | Paja Pajic | pacijent |
> | `432543` | Filip Filipovic | zubar |

---

## Getting Started

### Prerequisites

- **Java 17+** — `java --version`
- **Maven** — or use the included `./mvnw` wrapper

### Clone

```bash
git clone https://github.com/zaricu22/ZubarskaOrdinacijaBackend.git
cd ZubarskaOrdinacijaBackend
```

### Environment Variables

| Variable | Description | Example |
|---|---|---|
| `DB_URL` | PostgreSQL JDBC connection URL | `jdbc:postgresql://localhost:5432/dental` |
| `DB_USERNAME` | Database username | `admin` |
| `DB_PASSWORD` | Database password | `secret` |

### Run locally

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080/OrdinacijaREST`.

---

## Deployment

Render is configured via the dashboard: Branch `main`, Dockerfile Path `./Dockerfile`, Auto-Deploy: `On Commit`.

When a new commit is pushed to `main`, Render detects it, runs `docker build` using the Dockerfile, and redeploys the service.

The Dockerfile performs a full Maven build inside the container:
- **Dockerfile** — builds the JAR from source using `maven:3.9-eclipse-temurin-17-alpine` and runs it on a slim JRE image

---

## API Endpoints — `/OrdinacijaREST`

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/korisnikProvera/{korisnikId}` | Verify a user exists by ID number |
| `GET` | `/proveraTipaKorisnika/{korisnikId}/{tipKorisnika}` | Verify a user's type (pacijent / zubar) |
| `POST` | `/korisnikUnos` | Create a new user |
| `GET` | `/pregledSvihTerminaPacijent` | List all appointments for a patient |
| `GET` | `/pregledNeisteklihTerminaPacijent` | List upcoming (non-expired) appointments for a patient |
| `GET` | `/pregledTerminaZubarDan/{datum}` | List a dentist's appointments on a given day |
| `GET` | `/pregledTerminaZubarPeriod` | List a dentist's appointments over a date range |
| `POST` | `/zakazivanjeTermina` | Schedule a new appointment |
| `PUT` | `/otkazivanjeTermina/{datum}` | Cancel an appointment by date |
| `PUT` | `/promeniRokOtkazivanja/{noviRok}` | Update the cancellation deadline (days before appointment) |

---

## Project Structure

```
src/main/java/com/example/ordinacija/
├── controller/         # REST controllers
├── model/              # JPA entities (Korisnik, Parametri, Termin)
├── repositories/       # Spring Data repositories
└── services/           # Business logic (PacijentService, ZubarService, PrijavaService)
docs/db/
├── dental_init.sql
└── dental_seed-data.sql
```
