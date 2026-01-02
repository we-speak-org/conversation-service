# Guide d'utilisation Emergent.sh

Ce document détaille comment utiliser ce template avec Emergent.sh pour générer votre microservice.

## 📋 Prérequis

1. Compte Emergent.sh actif
2. Spécifications fonctionnelles du service (depuis wespeak-specifications)
3. Repository GitHub créé dans l'organisation we-speak-org

## 🚀 Étapes de génération

### 1. Préparer les spécifications

Assurez-vous que les spécifications de votre service sont complètes dans le repo `wespeak-specifications` :

```
wespeak-specifications/
└── [service-name]/
    ├── specification.md
    ├── api-endpoints.md
    ├── data-models.md
    ├── kafka-events.md
    ├── diagrams.md
    └── user-stories.md
```

### 2. Créer le repository depuis le template

```bash
# Sur GitHub, cliquer sur "Use this template"
# Ou via GitHub CLI:
gh repo create we-speak-org/[service-name] \
  --template we-speak-org/springboot-service-template \
  --public \
  --clone
```

### 3. Configurer Emergent.sh

#### Prompt initial pour Emergent.sh

```markdown
# Génération du [Service Name]

Objectif: Implémenter le microservice [service-name] selon les spécifications WeSpeak.

## Stack technique
- Spring Boot 4.0
- Java 21
- MongoDB (base de données unique)
- Redis (cache)
- Kafka (messaging)
- Keycloak (authentification OAuth2/OIDC)

## Spécifications à respecter

[Coller ici le contenu de specification.md]

## Modèle de données

[Coller ici le contenu de data-models.md]

## API REST

[Coller ici le contenu de api-endpoints.md]

## Événements Kafka

[Coller ici le contenu de kafka-events.md]

## Instructions spécifiques

1. **Architecture en couches**
   - Controllers (API REST)
   - Services (logique métier)
   - Repositories (accès données MongoDB)
   - Kafka Producers/Consumers

2. **Entités MongoDB**
   - Utiliser `@Document(collection = "nom")`
   - Inclure auditing (`@CreatedDate`, `@LastModifiedDate`)
   - Ajouter indexes appropriés (`@Indexed`)

3. **DTOs**
   - Request DTOs avec validation (`@Valid`, `@NotNull`, etc.)
   - Response DTOs séparés (ne jamais exposer les entités directement)
   - Mapper avec MapStruct ou manuellement

4. **Services**
   - Annoter avec `@Service`
   - Utiliser `@Cacheable` pour les opérations de lecture fréquentes
   - Logger toutes les opérations importantes
   - Publier les événements Kafka appropriés

5. **Sécurité**
   - Endpoints protégés par défaut
   - Utiliser `@PreAuthorize("hasRole('...')")` pour les contrôles d'accès
   - Extraire l'utilisateur du JWT via SecurityContextHolder

6. **Tests**
   - Tests unitaires pour tous les services
   - Tests d'intégration avec Testcontainers
   - Couverture minimale: 80%

7. **Documentation**
   - Annoter tous les endpoints avec `@Operation`
   - Ajouter des exemples dans les DTOs
   - Documenter les codes d'erreur possibles

## Ordre d'implémentation

1. Modèle de données (entités + repositories)
2. DTOs (request + response)
3. Services (logique métier)
4. Kafka producers/consumers
5. Controllers (API REST)
6. Tests unitaires
7. Tests d'intégration
8. Documentation OpenAPI

## Critères de validation

- ✅ Tous les endpoints spécifiés sont implémentés
- ✅ Les événements Kafka sont publiés correctement
- ✅ Les tests passent (mvn verify)
- ✅ La couverture de tests est >= 80%
- ✅ Le service démarre sans erreur
- ✅ Swagger UI accessible et complet
- ✅ Health checks fonctionnels
- ✅ Métriques Prometheus exposées
```

### 4. Itérations avec Emergent.sh

#### Phase 1: Modèle de données
```
Implémente le modèle de données avec:
- Toutes les entités MongoDB selon data-models.md
- Repositories Spring Data MongoDB
- Indexes pour optimisation
- Tests unitaires des repositories
```

#### Phase 2: DTOs et Validation
```
Crée les DTOs pour:
- Toutes les requêtes API (avec validation Bean Validation)
- Toutes les réponses API
- Mappers entre entités et DTOs
```

#### Phase 3: Services
```
Implémente les services avec:
- Logique métier selon specification.md
- Cache Redis pour les lectures
- Gestion des erreurs appropriée
- Logging structuré
- Tests unitaires (avec Mockito)
```

#### Phase 4: Kafka
```
Implémente:
- Producers pour publier les événements selon kafka-events.md
- Consumers pour écouter les topics pertinents
- Sérialisation/désérialisation JSON
- Tests avec KafkaTest
```

#### Phase 5: Controllers
```
Implémente les controllers REST:
- Tous les endpoints selon api-endpoints.md
- Validation des inputs
- Gestion des erreurs (via GlobalExceptionHandler)
- Sécurité (annotations @PreAuthorize)
- Documentation OpenAPI complète
```

#### Phase 6: Tests d'intégration
```
Crée les tests d'intégration avec Testcontainers:
- MongoDB container
- Kafka container
- Redis container
- Tests end-to-end de tous les endpoints
- Vérification des événements Kafka
```

### 5. Validation finale

#### Checklist avant déploiement

```bash
# Tests
./mvnw clean verify
# Doit afficher: Tests run: X, Failures: 0, Errors: 0

# Build Docker
docker build -t test-service -f docker/Dockerfile .
# Doit se terminer sans erreur

# Démarrage local
docker-compose up -d
./mvnw spring-boot:run
# Vérifier les logs: aucune erreur

# Health checks
curl http://localhost:8081/actuator/health
# Doit retourner: {"status":"UP"}

# Swagger UI
open http://localhost:8081/swagger-ui.html
# Vérifier que tous les endpoints sont documentés

# Métriques
curl http://localhost:8081/actuator/prometheus
# Doit retourner les métriques
```

## 🐛 Dépannage courant

### Erreur: MongoDB connection failed
```bash
# Vérifier que MongoDB est démarré
docker-compose ps mongodb
# Vérifier la connection string dans .env
```

### Erreur: Kafka not available
```bash
# Vérifier Kafka et Zookeeper
docker-compose logs kafka
# Attendre que Kafka soit complètement démarré (peut prendre 30s)
```

### Erreur: JWT validation failed
```bash
# Vérifier Keycloak
curl http://localhost:8080/realms/wespeak
# Configurer le realm 'wespeak' dans Keycloak admin
```

### Tests échouent avec Testcontainers
```bash
# Vérifier Docker
docker ps
# S'assurer que Docker Desktop est démarré
# Vérifier DOCKER_HOST si WSL2
```

## 📚 Ressources Emergent.sh

### Prompts utiles

**Ajouter un nouvel endpoint:**
```
Ajoute un endpoint GET /api/resource/{id} qui:
- Récupère une ressource par ID
- Retourne 404 si non trouvé
- Cache le résultat dans Redis (TTL: 10 minutes)
- Nécessite le role USER
- Documente avec OpenAPI
- Ajoute les tests unitaires et d'intégration
```

**Optimiser les performances:**
```
Optimise les performances de [méthode/endpoint]:
- Ajoute un cache Redis approprié
- Optimise la requête MongoDB (projection, index)
- Ajoute du logging de performance
- Mesure avec Micrometer
```

**Corriger un bug:**
```
Bug: [description du problème]
Comportement attendu: [...]
Comportement actuel: [...]
Logs d'erreur: [...]

Analyse et corrige le bug en:
1. Identifiant la cause racine
2. Proposant une solution
3. Ajoutant un test pour éviter la régression
```

## ✅ Critères de succès

Un service est considéré complet quand:

- [ ] Tous les endpoints API sont implémentés et testés
- [ ] Tous les événements Kafka sont produits/consommés correctement
- [ ] Couverture de tests >= 80%
- [ ] Aucune erreur dans les logs au démarrage
- [ ] Health checks OK (MongoDB, Redis, Kafka)
- [ ] Swagger UI complet et à jour
- [ ] Métriques Prometheus exposées
- [ ] L'image Docker build sans erreur
- [ ] Le service démarre dans docker-compose
- [ ] La documentation est à jour (README, API docs)

## 🚢 Déploiement

Une fois validé localement:

```bash
# Commit et push
git add .
git commit -m "feat: implement [service-name] according to specs"
git push origin main

# Le GitHub Action build automatiquement
# Vérifier: https://github.com/we-speak-org/[service-name]/actions
```

L'image Docker sera publiée sur:
`ghcr.io/we-speak-org/[service-name]:latest`

---

**Note**: Ce guide suppose une utilisation interactive avec Emergent.sh. Adaptez les prompts selon vos besoins spécifiques et les retours de l'IA.
