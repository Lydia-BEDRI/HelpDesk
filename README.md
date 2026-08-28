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

Le socle Docker Compose démarre actuellement PostgreSQL. Le backend et le frontend seront ajoutés progressivement.

```bash
cp .env.example .env
docker compose up -d
docker compose ps
```

Pour arrêter les conteneurs :

```bash
docker compose down
```

Consulter [cdc.md](docs/cdc.md) pour le périmètre, les règles métier et l'ordre de développement.