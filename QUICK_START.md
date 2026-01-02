# 🚀 Quick Start - Créer un nouveau service

Guide rapide pour créer un nouveau microservice à partir de ce template.

## 1. Créer le repository (2 min)

### Option A: Via GitHub UI
1. Allez sur https://github.com/we-speak-org/springboot-service-template
2. Cliquez sur "Use this template" → "Create a new repository"
3. Nommez votre service: `[service-name]-service` (ex: `user-service`)
4. Créez le repository dans l'organisation `we-speak-org`

### Option B: Via GitHub CLI
```bash
gh repo create we-speak-org/user-service \
  --template we-speak-org/springboot-service-template \
  --public \
  --clone

cd user-service
```

## 2. Personnaliser le service (5 min)

### Script automatique
```bash
#!/bin/bash
# Remplacez 'user' par le nom de votre service
SERVICE_NAME="user"

# Renommer dans build.gradle / settings.gradle
sed -i "s/template-service/${SERVICE_NAME}-service/g" settings.gradle

# Renommer le package Java
find src -type f -name "*.java" -exec sed -i "s/org.wespeak.template/org.wespeak.${SERVICE_NAME}/g" {} +

# Renommer dans application.yml
sed -i "s/template-service/${SERVICE_NAME}-service/g" src/main/resources/application*.yml

# Renommer les répertoires
mv src/main/java/org/wespeak/template src/main/java/org/wespeak/${SERVICE_NAME}
mv src/test/java/org/wespeak/template src/test/java/org/wespeak/${SERVICE_NAME}

# Mettre à jour README
sed -i "s/Template Service/${SERVICE_NAME^} Service/g" README.md

echo "✅ Service renommé en ${SERVICE_NAME}-service"
```

### Ou manuellement:
```bash
# 1. pom.xml
<artifactId>user-service</artifactId>
<name>User Service</name>

# 2. Package Java
org.wespeak.template → org.wespeak.user

# 3. application.yml
spring.application.name: user-service
```

## 3. Configurer l'environnement (3 min)

```bash
# Copier le fichier d'exemple
cp .env.example .env

# Éditer .env avec vos valeurs
nano .env
```

Vérifiez que les ports ne sont pas en conflit:
```env
SERVER_PORT=8081  # Changer si déjà utilisé
```

## 4. Démarrer l'infrastructure (5 min)

```bash
# Démarrer MongoDB, Redis, Kafka, Keycloak
cd docker
docker-compose up -d

# Vérifier que tout est démarré
docker-compose ps

# Attendre que Kafka soit prêt (30 secondes)
docker-compose logs -f kafka
# Ctrl+C quand vous voyez "started"
```

## 5. Tester le template (2 min)

```bash
# Lancer l'application
./gradlew bootRun

# Dans un autre terminal, tester les endpoints
curl http://localhost:8081/actuator/health
# Doit retourner: {"status":"UP"}

# Swagger UI
open http://localhost:8081/swagger-ui.html
```

## 6. Commencer le développement avec Emergent.sh

### Récupérer les spécifications
```bash
# Cloner le repo de specs
git clone https://github.com/we-speak-org/wespeak-specifications.git
cd wespeak-specifications/[service-name]

# Lire les specs
cat specification.md
cat api-endpoints.md
cat data-models.md
cat kafka-events.md
```

### Prompt initial pour Emergent.sh

Copiez ce template et remplissez avec vos specs:

```markdown
Je développe le microservice [SERVICE_NAME] pour la plateforme WeSpeak.

## Stack technique
- Spring Boot 4.0, Java 21
- MongoDB (base unique)
- Redis (cache)
- Kafka (events)
- Keycloak OAuth2/OIDC

## Architecture
Le projet suit une architecture en couches:
- Controllers (REST API)
- Services (business logic)
- Repositories (MongoDB)
- Kafka Producers/Consumers

## Spécifications fonctionnelles
[COLLER specification.md ICI]

## Modèle de données
[COLLER data-models.md ICI]

## API REST
[COLLER api-endpoints.md ICI]

## Événements Kafka
[COLLER kafka-events.md ICI]

## Première tâche
Implémente le modèle de données complet avec:
- Toutes les entités MongoDB annotées
- Repositories Spring Data
- Indexes pour performance
- Tests unitaires des repositories

Commence par l'entité [ENTITE_PRINCIPALE].
```

### Workflow itératif

**Étape 1: Modèle de données**
```
✅ Entités + Repositories + Tests
→ Vérifier: ./gradlew test
```

**Étape 2: Services**
```
✅ Logique métier + Cache + Tests
→ Vérifier: ./gradlew test
```

**Étape 3: Kafka**
```
✅ Producers + Consumers + Tests
→ Vérifier avec Kafka UI ou logs
```

**Étape 4: Controllers**
```
✅ Endpoints REST + Validation + Sécurité + Tests
→ Vérifier: Swagger UI + Postman
```

**Étape 5: Tests d'intégration**
```
✅ Tests E2E avec Testcontainers
→ Vérifier: ./gradlew check
```

## 7. Valider avant commit

```bash
# Tests complets
./gradlew clean check

# Build Docker
docker build -t my-service -f docker/Dockerfile .

# Démarrer en Docker
docker run -p 8081:8080 \
  -e MONGODB_URI=mongodb://host.docker.internal:27017/mydb \
  -e KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  my-service

# Tester
curl http://localhost:8081/actuator/health
```

## 8. Commit et déploiement

```bash
# Ajouter tous les fichiers
git add .

# Commit
git commit -m "feat: implement [service-name] MVP"

# Push (déclenche GitHub Actions)
git push origin main

# Vérifier le build
open https://github.com/we-speak-org/[service-name]/actions
```

L'image sera disponible sur:
```
ghcr.io/we-speak-org/[service-name]:latest
```

## �� Checklist finale

Avant de considérer le service "terminé":

- [ ] Tous les endpoints de api-endpoints.md sont implémentés
- [ ] Tous les événements Kafka sont gérés
- [ ] Tests unitaires > 80% couverture
- [ ] Tests d'intégration passent
- [ ] `./gradlew clean check` → SUCCESS
- [ ] Health checks OK (MongoDB, Redis, Kafka)
- [ ] Swagger UI complet et fonctionnel
- [ ] Métriques Prometheus exposées
- [ ] Logs structurés (JSON)
- [ ] Image Docker build OK
- [ ] GitHub Actions build OK
- [ ] README.md à jour
- [ ] Documentation API complète

## 📚 Ressources

- [Guide Emergent.sh](docs/EMERGENT_GUIDE.md)
- [Spécifications WeSpeak](https://github.com/we-speak-org/wespeak-specifications)
- [Spring Boot Docs](https://docs.spring.io/spring-boot/)
- [MongoDB Docs](https://docs.spring.io/spring-data/mongodb/reference/)

## 💡 Conseils

1. **Commencez simple**: Implémentez d'abord un endpoint CRUD basique
2. **Testez fréquemment**: `./gradlew test` après chaque changement
3. **Utilisez Swagger**: Testez vos endpoints directement dans l'UI
4. **Consultez les logs**: `docker-compose logs -f` pour débugger
5. **Itérez avec Emergent.sh**: Petites tâches claires et précises

## 🆘 Besoin d'aide?

- Issues GitHub: https://github.com/we-speak-org/springboot-service-template/issues
- Documentation complète: [README.md](README.md)
- Guide Emergent.sh: [EMERGENT_GUIDE.md](docs/EMERGENT_GUIDE.md)

---

**Temps total estimé pour un service basique: 3-5 heures avec Emergent.sh** 🚀
