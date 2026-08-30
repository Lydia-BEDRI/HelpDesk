# HelpDesk

Application web de gestion de tickets IT, construite avec Angular, Spring Boot et PostgreSQL.

## Structure

```text
backend/   API Java Spring Boot
frontend/  application Angular
docs/      documentation technique
 cdc.md     cahier des charges et feuille de route
```

## Démarrage

Le backend Spring Boot est initialisé et PostgreSQL est lancé par Docker Compose.

```bash
cp .env.example .env
docker compose up -d
docker compose ps
```

L'API sera accessible sur <http://localhost:8080> lorsque les premiers endpoints seront ajoutés.

Pour arrêter les conteneurs :

```bash
docker compose down
```

Consulter [cdc.md](docs/cdc.md) pour le périmètre, les règles métier et l'ordre de développement.