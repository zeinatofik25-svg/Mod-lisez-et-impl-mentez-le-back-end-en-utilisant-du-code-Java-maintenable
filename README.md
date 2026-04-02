# ChaTop - Application de Gestion de Locations

Application full-stack composée d'un **frontend Angular** et d'un **backend Spring Boot**. Elle permet la gestion de locations immobilières avec authentification JWT, CRUD de locations et système de messagerie.

---

## Table des matières

1. [Prérequis](#prérequis)
2. [Cloner le projet](#cloner-le-projet)
3. [Installation de la base de données](#installation-de-la-base-de-données)
4. [Configuration du backend](#configuration-du-backend)
5. [Lancer le backend](#lancer-le-backend)
6. [Installation et lancement du frontend](#installation-et-lancement-du-frontend)
7. [Swagger — Documentation API](#swagger--documentation-api)
8. [Architecture du projet](#architecture-du-projet)
9. [Endpoints de l'API](#endpoints-de-lapi)
10. [Tests](#tests)
11. [Ressources](#ressources)

---

## Prérequis

Assurez-vous d'avoir les outils suivants installés sur votre machine :

| Outil      | Version minimale | Vérification                |
| ---------- | ---------------- | --------------------------- |
| **Java**   | 21+              | `java -version`             |
| **Maven**  | 3.9+ (ou wrapper)| `mvn -version` ou `.\mvnw.cmd -version` |
| **MySQL**  | 8.0+             | `mysql --version`           |
| **Node.js**| 22+              | `node -v`                   |
| **npm**    | 9+               | `npm -v`                    |

---

## Cloner le projet

```bash
git clone https://github.com/zeinatofik25-svg/Mod-lisez-et-impl-mentez-le-back-end-en-utilisant-du-code-Java-maintenable.git
cd Mod-lisez-et-impl-mentez-le-back-end-en-utilisant-du-code-Java-maintenable
```

---

## Installation de la base de données

### Étape 1 — Créer la base et l'utilisateur MySQL

Ouvrez un terminal MySQL en tant que root :

```bash
mysql -u root -p
```

Exécutez les commandes suivantes :

```sql
-- Créer la base de données
CREATE DATABASE IF NOT EXISTS chatop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Créer un utilisateur dédié (remplacez 'votre_mot_de_passe' par un mot de passe sécurisé)
CREATE USER IF NOT EXISTS 'chatop_user'@'localhost' IDENTIFIED BY 'votre_mot_de_passe';

-- Accorder les privilèges
GRANT ALL PRIVILEGES ON chatop_db.* TO 'chatop_user'@'localhost';
FLUSH PRIVILEGES;
```

### Étape 2 — Créer les tables

Toujours dans le terminal MySQL :

```sql
USE chatop_db;
SOURCE ressources/sql/script.sql;
```

Ou depuis la ligne de commande :

```bash
mysql -u chatop_user -p chatop_db < ressources/sql/script.sql
```

Le script crée les tables suivantes :
- **USERS** — Utilisateurs de l'application
- **RENTALS** — Locations immobilières
- **MESSAGES** — Messages envoyés par les utilisateurs pour une location

### Étape 3 — Vérifier l'installation

```sql
USE chatop_db;
SHOW TABLES;
```

Résultat attendu :

```
+---------------------+
| Tables_in_chatop_db |
+---------------------+
| MESSAGES            |
| RENTALS             |
| USERS               |
+---------------------+
```

---

## Configuration du backend

Le backend utilise des variables d'environnement pour sa configuration. Configurez-les **avant** de lancer le serveur.

### Avec PowerShell (Windows)

```powershell
$env:DB_HOST="localhost"
$env:DB_NAME="chatop_db"
$env:DB_USER="chatop_user"
$env:DB_PASSWORD="votre_mot_de_passe"
$env:APP_JWT_SECRET="une-cle-secrete-longue-et-securisee-de-minimum-32-caracteres"
$env:SERVER_PORT="8090"
```

### Avec Bash (Linux/macOS)

```bash
export DB_HOST=localhost
export DB_NAME=chatop_db
export DB_USER=chatop_user
export DB_PASSWORD=votre_mot_de_passe
export APP_JWT_SECRET=une-cle-secrete-longue-et-securisee-de-minimum-32-caracteres
export SERVER_PORT=8090
```

### Variables disponibles

| Variable                     | Description                          | Valeur par défaut |
| ---------------------------- | ------------------------------------ | ----------------- |
| `DB_HOST`                    | Hôte MySQL                           | `localhost`       |
| `DB_NAME`                    | Nom de la base                       | `chatop_db`       |
| `DB_USER`                    | Utilisateur MySQL                    | `chatop_user`     |
| `DB_PASSWORD`                | Mot de passe MySQL                   | *(vide)*          |
| `APP_JWT_SECRET`             | Clé secrète pour signer les JWT       | *(défaut insécurisé)* |
| `APP_JWT_EXPIRATION_MS`      | Durée de validité du token (ms)      | `86400000` (24h)  |
| `SERVER_PORT`                | Port du serveur backend              | `8090`            |

> **Important** : En production, utilisez toujours une clé JWT secrète forte et unique.

---

## Lancer le backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Ou sous Linux/macOS :

```bash
cd backend
./mvnw spring-boot:run
```

Le backend démarre sur : **http://localhost:8090**

Vérifiez que le serveur fonctionne en accédant à :
- http://localhost:8090/swagger-ui.html

---

## Installation et lancement du frontend

Depuis la **racine du projet** :

### Étape 1 — Installer les dépendances

```bash
npm install
```

### Étape 2 — Lancer le serveur de développement

```bash
npm run start
```

Le frontend démarre sur : **http://localhost:4200**

> Le frontend utilise un proxy configuré (`src/proxy.config.json`) pour rediriger les appels API vers le backend sur le port 8090.

---

## Swagger — Documentation API

L'API est entièrement documentée avec **Swagger / OpenAPI 3.0**.

| Ressource        | URL                                          |
| ---------------- | -------------------------------------------- |
| **Swagger UI**   | http://localhost:8090/swagger-ui.html         |
| **OpenAPI JSON** | http://localhost:8090/v3/api-docs             |

### Tester les routes protégées

1. Créez un compte via `POST /api/auth/register` ou connectez-vous via `POST /api/auth/login`
2. Copiez le token JWT retourné dans la réponse
3. Cliquez sur le bouton **Authorize** dans Swagger UI
4. Entrez : `Bearer <votre_token>`
5. Toutes les routes protégées sont désormais accessibles

---

## Architecture du projet

```
backend/
├── src/main/java/com/chatop/backend/
│   ├── BackendApplication.java          # Point d'entrée Spring Boot
│   ├── auth/                            # Module Authentification
│   │   ├── AuthController.java          #   Endpoints register, login, me
│   │   ├── AuthService.java             #   Logique métier auth
│   │   └── dto/                         #   DTOs (LoginRequest, RegisterRequest, AuthResponse, UserResponse)
│   ├── rental/                          # Module Locations
│   │   ├── Rental.java                  #   Entité JPA
│   │   ├── RentalController.java        #   Endpoints CRUD
│   │   ├── RentalRepository.java        #   Repository JPA
│   │   ├── RentalService.java           #   Logique métier
│   │   └── dto/                         #   DTOs (RentalResponse, RentalsResponse)
│   ├── message/                         # Module Messages
│   │   ├── Message.java                 #   Entité JPA
│   │   ├── MessageController.java       #   Endpoint envoi de message
│   │   ├── MessageRepository.java       #   Repository JPA
│   │   ├── MessageService.java          #   Logique métier
│   │   └── dto/                         #   DTOs (CreateMessageRequest)
│   ├── user/                            # Module Utilisateurs
│   │   ├── User.java                    #   Entité JPA
│   │   ├── UserController.java          #   Endpoint consultation
│   │   └── UserRepository.java          #   Repository JPA
│   ├── config/                          # Configuration
│   │   ├── SecurityConfig.java          #   Spring Security + filtrage JWT
│   │   ├── JwtService.java              #   Génération/validation tokens JWT
│   │   ├── JwtAuthenticationFilter.java #   Filtre HTTP pour JWT
│   │   └── OpenApiConfig.java           #   Configuration Swagger
│   ├── exception/                       # Gestion des erreurs
│   │   ├── ApiExceptionHandler.java     #   Handler centralisé
│   │   ├── EmailAlreadyExistsException.java
│   │   ├── InvalidCredentialsException.java
│   │   ├── ResourceNotFoundException.java
│   │   └── UnauthorizedException.java
│   └── common/dto/
│       └── MessageResponse.java         # DTO réponse générique
├── src/main/resources/
│   └── application.properties           # Configuration Spring Boot
└── pom.xml                              # Dépendances Maven
```

### Stack technique

| Composant          | Technologie                       |
| ------------------ | --------------------------------- |
| Framework          | Spring Boot 3.5.12                |
| Langage            | Java 21                           |
| Sécurité           | Spring Security + JWT (jjwt 0.12.6) |
| Persistance        | Spring Data JPA + Hibernate       |
| Base de données    | MySQL 8+                          |
| Documentation API  | springdoc-openapi 2.8.6           |
| Validation         | Jakarta Bean Validation           |
| Utilitaire         | Lombok                            |

---

## Endpoints de l'API

| Méthode | Endpoint              | Accès         | Description                  |
| ------- | --------------------- | ------------- | ---------------------------- |
| POST    | `/api/auth/register`  | Public        | Inscription                  |
| POST    | `/api/auth/login`     | Public        | Connexion                    |
| GET     | `/api/auth/me`        | Authentifié   | Profil de l'utilisateur connecté |
| GET     | `/api/rentals`        | Authentifié   | Liste de toutes les locations |
| GET     | `/api/rentals/{id}`   | Authentifié   | Détail d'une location        |
| POST    | `/api/rentals`        | Authentifié   | Créer une location           |
| PUT     | `/api/rentals/{id}`   | Authentifié   | Modifier une location        |
| POST    | `/api/messages`       | Authentifié   | Envoyer un message           |
| GET     | `/api/user/{id}`      | Authentifié   | Informations d'un utilisateur |

---

## Tests

### Tests backend

Depuis le dossier `backend` :

```powershell
.\mvnw.cmd test
```

## Ressources

- **Script SQL** : `ressources/sql/script.sql`
- **Mockoon** : `ressources/mockoon/rental-oc.json` (mock API pour développement frontend)
- **Swagger UI** : http://localhost:8090/swagger-ui.html
