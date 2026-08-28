# Cahier des charges - HelpDesk

## 1. Présentation

HelpDesk est une application web interne de gestion de tickets IT. Elle centralise les demandes des employés et permet aux équipes informatiques de les prendre en charge, de les suivre et de les résoudre.

## 2. Objectifs

- Permettre aux employés de créer et suivre leurs demandes.
- Permettre aux agents IT de rechercher, filtrer, traiter et résoudre les tickets.
- Donner aux administrateurs une vision globale de l'activité du support.
- Fournir une base de projet démontrant Angular, Spring Boot, PostgreSQL, sécurité, tests, Docker et CI/CD.

## 3. Utilisateurs et rôles

### EMPLOYEE

Peut créer un ticket, consulter ses propres tickets, les modifier tant qu'ils ne sont pas clôturés, ajouter des commentaires et fermer un ticket résolu. Il ne peut pas gérer les utilisateurs, les catégories, les priorités ou les statistiques globales.

### AGENT

Peut consulter, rechercher et filtrer les tickets, prendre en charge un ticket, modifier son statut et sa priorité, ajouter des commentaires et résoudre un ticket.

### ADMIN

Possède les droits d'un agent et peut gérer les utilisateurs, les rôles, les catégories, les priorités et les statistiques.

## 4. Stack technique

- Frontend : Angular + TypeScript
- Backend : Java + Spring Boot
- Base de données : PostgreSQL
- Authentification : Spring Security + JWT
- Documentation API : OpenAPI / Swagger
- Conteneurisation : Docker / Docker Compose
- Tests : JUnit, Mockito et Angular testing
- CI/CD : GitHub Actions

## 5. Périmètre MVP obligatoire

### Authentification

- Inscription avec prénom, nom, e-mail et mot de passe.
- Connexion avec retour d'un JWT et d'une durée d'expiration.
- Mots de passe hachés, jamais stockés en clair.
- Contrôle des accès selon les rôles EMPLOYEE, AGENT et ADMIN.

### Tickets

Un ticket contient un identifiant, un titre, une description, un statut, une priorité, une catégorie, une date de création, une date de mise à jour, un créateur et un agent responsable.

Fonctionnalités MVP :

- création, consultation, modification et suppression ;
- attribution à un agent ;
- changement de statut et de priorité ;
- recherche sur le numero, le titre, la description, l'utilisateur ou l'agent ;
- filtres par statut, priorité, catégorie, agent, utilisateur et date ;
- pagination côté serveur.

Le statut initial est `OPEN`. Les transitions autorisées sont :

```text
OPEN -> IN_PROGRESS
IN_PROGRESS -> WAITING_FOR_USER | RESOLVED
WAITING_FOR_USER -> IN_PROGRESS
RESOLVED -> CLOSED
```

Un employé peut fermer uniquement un ticket résolu. Un employé ne peut jamais modifier l'agent responsable.

### Catégories et priorités

Categories initiales : `HARDWARE`, `SOFTWARE`, `NETWORK`, `ACCOUNT`, `ACCESS`, `SECURITY`, `OTHER`.

Priorites : `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`.

Une catégorie désactivée n'est plus proposée lors de la création d'un ticket. Seul un administrateur peut gérer les catégories et les priorités.

### Commentaires

Chaque ticket possède une conversation. Un commentaire contient un auteur, un contenu, une date et le ticket associé. Un commentaire ne peut pas être modifié par un autre utilisateur.

### Interface MVP

Routes principales :

```text
/login
/register
/dashboard
/tickets
/tickets/new
/tickets/:id
/profile
```

L'interface doit être responsive et proposer au minimum la connexion, le dashboard, la liste paginée, la création et le détail d'un ticket avec conversation.

## 6. API cible

```text
POST   /api/auth/register
POST   /api/auth/login

GET    /api/tickets
GET    /api/tickets/{id}
POST   /api/tickets
PATCH  /api/tickets/{id}
DELETE /api/tickets/{id}
PATCH  /api/tickets/{id}/status
PATCH  /api/tickets/{id}/assign

GET    /api/tickets/{id}/comments
POST   /api/tickets/{id}/comments

GET    /api/categories
POST   /api/categories
PATCH  /api/categories/{id}
DELETE /api/categories/{id}

GET    /api/users
GET    /api/users/{id}
PATCH  /api/users/{id}
```

Les erreurs doivent être uniformes et inclure les statuts HTTP pertinents : `400`, `401`, `403`, `404`, `409` et `500`. Les validations principales portent sur le titre obligatoire de 5 à 150 caractères et la description obligatoire d'au moins 10 caractères.

## 7. Modèle de données

Entites principales : `User`, `Ticket`, `Category`, `Comment`, `Notification` et `TicketHistory`.

Relations : un utilisateur crée des tickets, écrit des commentaires et reçoit des notifications ; un ticket appartient à une catégorie et possède des commentaires et un historique.

## 8. Architecture cible

```text
backend/src/main/java/com/helpdesk/
├── auth
├── user
├── ticket
├── category
├── comment
├── notification
├── dashboard
├── common
└── config
```

Chaque domaine peut contenir `controller`, `service`, `repository`, `entity`, `dto`, `mapper` et `exception` selon son besoin.

## 9. Infrastructure et qualité

- `docker compose up` doit permettre de démarrer l'environnement avec PostgreSQL.
- Swagger doit etre disponible via `/swagger-ui/index.html`.
- Les services, règles métier, contrôleurs et permissions doivent être couverts par des tests backend.
- Le frontend doit tester les formulaires, la liste, les filtres et les erreurs.
- GitHub Actions doit exécuter l'installation, le build, les tests et les contrôles qualité sur les Pull Requests.
- Le README doit documenter l'installation, l'architecture, la stack et des comptes de démonstration sans vrais identifiants.

## 10. V2 et V3

Après validation du MVP : notifications temps réel avec WebSocket, historique complet, statistiques avancées, pièces jointes, export CSV et mode sombre.

Pour une V3 portfolio : SLA par priorité, audit, Redis, Prometheus, Grafana, logs structurés, health checks et déploiement.

Les fonctionnalités avancées ne doivent pas bloquer la livraison du MVP.

## 11. Ordre de developpement

1. Concevoir l'architecture, les cas d'utilisation, les rôles, les règles métier et le modèle de données.
2. Implémenter le backend, la base PostgreSQL, la sécurité JWT, l'API et les tests.
3. Implémenter le frontend Angular et les parcours d'authentification et de tickets.
4. Ajouter Docker et Docker Compose.
5. Finaliser Swagger, les tests et GitHub Actions.
6. Ajouter progressivement les fonctionnalités V2 puis V3.