# TaskTracker — Backend

![CI/CD](https://github.com)

REST-API und Geschäftslogik für die Webanwendung **TaskTracker** ([https://dushyna.site](https://dushyna.site)).

---

## 🛠 Technologie-Stack

* **Sprache & Framework:** Java 17, Spring Boot 3 (Spring Web, Spring Data JPA, Spring Security)
* **Build-Tool:** Gradle
* **Datenbank:** MySQL 8.0
* **Authentifizierung:** JWT (JSON Web Tokens) über `HttpOnly`-Cookies
* **Containerisierung & CI/CD:** Docker (Multi-Stage-Build mit Distroless Runtime), Docker Compose, GitHub Actions

---

## 🔐 Sicherheit und Authentifizierung

Zur Absicherung der API und Ressourcen verwendet TaskTracker **JWT (JSON Web Tokens)** mit einer Trennung in zwei Token-Typen:

* **Access Token (AT)**
  * Kurzlebiger Token (Standard: 15 Minuten).
  * Wird in einem HTTP-Cookie mit dem Flag `HttpOnly` gespeichert.
  * Dient zur Autorisierung jeder Anfrage an geschützte Endpunkte und minimiert das Risiko bei einer Kompromittierung.

* **Refresh Token (RT)**
  * Langlebiger Token (Standard: 7 Tage / 10080 Minuten).
  * Wird ebenfalls in einem `HttpOnly`-Cookie gespeichert.
  * Ermöglicht das Erneuern des Access Tokens ohne erneute Eingabe der Zugangsdaten.

### Ablauf der Authentifizierung
1. Beim Login (`POST /api/v1/auth/login`) prüft der Server E-Mail und Passwort und setzt beide Tokens (AT + RT) als `HttpOnly`-Cookies.
2. Der Browser sendet den Access Token bei jeder Anfrage an geschützte Routen automatisch mit.
3. Nach Ablauf des Access Tokens ruft der Client `POST /api/v1/auth/refresh-token` auf, um mithilfe des Refresh Tokens einen neuen Access Token zu erhalten.
4. Beim Logout (`POST /api/v1/auth/logout`) werden beide Cookies gelöscht und die Tokens invalidiert.

### Benutzerrollen
* **`ROLE_ADMIN`** — Vollständige administrative Rechte: Verwaltung von Benutzern, Projekten und Systemeinstellungen.
* **`ROLE_USER`** — Standardzugriff: Erstellen und Verwalten eigener Projekte und Aufgaben.

---

## 📡 API-Endpunkte

### 1. Authentifizierung & Registrierung
* `POST /api/v1/users/register` — Registrierung eines neuen Benutzers *(öffentlich)*.
* `GET /api/v1/users/confirm/{code}` — Aktivierung des Benutzerkontos per Bestätigungscode *(öffentlich)*.
* `POST /api/v1/auth/login` — Anmeldung mit E-Mail und Passwort (setzt JWT-Cookies).
* `POST /api/v1/auth/refresh-token` — Erneuerung des Access Tokens über den Refresh Token.
* `POST /api/v1/auth/logout` — Abmeldung und Löschen der Auth-Cookies.

### 2. Projekte
* `POST /api/v1/projects` — Neues Projekt erstellen (`ROLE_USER`, `ROLE_ADMIN`).
* `GET /api/v1/projects` — Alle Projekte abrufen, an denen der Benutzer beteiligt ist.
* `GET /api/v1/projects/{id}` — Projektdetails nach ID abrufen *(Projektmitglieder)*.
* `DELETE /api/v1/projects/{id}` — Projekt löschen *(Projekteigentümer oder `ROLE_ADMIN`)*.

### 3. Aufgaben (Tasks)
* `POST /api/v1/tasks` — Neue Aufgabe erstellen *(Projektmitglieder)*.
* `GET /api/v1/tasks/{id}` — Aufgabendetails nach ID abrufen *(Projektmitglieder)*.
* `GET /api/v1/tasks/project/{projectId}` — Alle Aufgaben eines Projekts abrufen *(Projektmitglieder)*.
* `DELETE /api/v1/tasks/{id}` — Aufgabe löschen *(Aufgabenersteller oder Projekteigentümer)*.

### 4. Benutzerverwaltung (`ROLE_ADMIN`)
* `GET /api/v1/users/all` — Liste aller registrierten Benutzer abrufen *(nur `ROLE_ADMIN`)*.

### 5. Einladungen & Teammitglieder *(in Planung)*
* `POST /api/v1/projects/{projectId}/invitations` — Benutzer zu einem Projekt einladen *(Projekteigentümer)*.
* `GET /api/v1/projects/{projectId}/collaborators` — Liste der Projektmitglieder abrufen *(Projektmitglieder)*.

---

## 🚀 Lokale Entwicklung

### Voraussetzungen
* JDK 17+
* Docker & Docker Compose

### Anwendung starten
1. Repository klonen:
   ```bash
   git clone https://github.com/Dushyna/task-tracker-be.git
   cd task-tracker-be
   ```

2. Umgebungsvariablen in einer `.env`-Datei im Verzeichnis `docker/` konfigurieren:
   ```env
   DB_PASSWORD=root
   EMAIL_USERNAME=your-email@example.com
   EMAIL_PASSWORD=your-email-password
   JWT_AT_SECRET=your-access-token-secret
   JWT_RT_SECRET=your-refresh-token-secret
   BASE_URL=http://localhost:8080
   ```

3. Datenbank und Backend mit Docker Compose starten:
   ```bash
   docker compose -f docker/docker-compose.yml up -d --build
   ```
   Alternativ kann das Backend lokal über Gradle gestartet werden:
   ```bash
   ./gradlew bootRun
   ```

---

## 🔄 CI/CD & Deployment

Das Projekt ist mit **GitHub Actions** (`.github/workflows/gradle.yml`) integriert. Die Pipeline wird bei jedem `push` und `pull_request` auf die Branches `main` und `develop` ausgelöst:

1. **Continuous Integration (`build`)**
  * Checkout des Repositories und Einrichtung von **JDK 17 (Temurin)** inklusive Gradle-Caching.
  * Kompilierung und Ausführung der automatisierten Tests via `./gradlew build --no-daemon`.

2. **Continuous Deployment (`deploy`)**
  * Wird automatisch nach erfolgreichem Build bei einem `push` auf den Branch `develop` ausgeführt.
  * Verbindet sich per SSH mit dem Produktionsserver (VPS), aktualisiert den Quellcode und baut den `backend`-Container über Docker Compose neu (`DOCKER_BUILDKIT=1 docker compose up -d --build --no-deps backend`).

---

## 🔗 Verwandte Repositories

* **Frontend:** [Dushyna/task-tracker-fe](https://github.com)
* **Live-Anwendung:** [https://dushyna.site](https://dushyna.site)
