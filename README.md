# WeSpeak Spring Boot Microservice Template

🚀 Template standardisé pour tous les microservices backend WeSpeak

## 📋 Vue d'ensemble

Ce template fournit une base prête à l'emploi pour créer un nouveau microservice Spring Boot 4 dans l'écosystème WeSpeak. Il inclut toutes les dépendances, configurations et patterns recommandés.

## 🎯 Fonctionnalités incluses

### Core Framework
- **Spring Boot 4.0.x** - Framework de base
- **Java 21** - Version LTS
- **Gradle 9.2.1** - Gestion des dépendances
- **MongoDB** - Base de données unifiée
- **Spring Data MongoDB** - Accès aux données

### API & Documentation
- **Spring Web** - REST Controllers
- **SpringDoc OpenAPI 3** - Documentation API automatique (Swagger UI)
- **Validation** - Bean Validation avec annotations

### Messaging & Events
- **Spring Cloud Stream** - Style fonctionnel avec Consumer/Function beans
- **Kafka Binder** - Intégration Kafka
- **CloudEvent Pattern** - Format d'événement standardisé
- **Avro** - Sérialisation des messages
- **DLQ Support** - Dead Letter Queue pour messages en échec

### Cache & Performance
- **Spring Cache** - Abstraction de cache
- **Redis** - Cache distribué
- **Caffeine** - Cache local

### Security
- **Spring Security** - Sécurisation des endpoints
- **Keycloak Adapter** - Intégration Keycloak OAuth2/OIDC
- **JWT Token validation** - Validation des tokens

### Observabilité
- **Spring Actuator** - Health checks et métriques
- **Micrometer** - Métriques format Prometheus
- **Logback** - Logging structuré (JSON)
- **MDC Context** - Correlation IDs pour tracing

### Code Quality
- **Spotless** - Formatage automatique (Google Java Format)
- **Lombok** - Réduction du boilerplate

### Testing
- **JUnit 5** - Framework de tests
- **Mockito** - Mocking
- **Testcontainers** - Tests d'intégration (MongoDB, Kafka, Redis)
- **REST Assured** - Tests API REST

### DevOps
- **Dockerfile multi-stage** - Build optimisé
- **GitHub Actions workflow** - CI/CD automatisé (PR check + publish)
- **Docker Compose** - Environnement de développement local
- **Health checks** - Prêt pour production

## 📁 Structure du projet

```
springboot-service-template/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/wespeak/template/
│   │   │       ├── config/           # Configurations Spring
│   │   │       │   ├── SecurityConfig.java
│   │   │       │   └── OpenApiConfig.java
│   │   │       ├── controller/       # REST Controllers
│   │   │       │   └── HealthController.java
│   │   │       ├── model/            # Entités & DTOs
│   │   │       │   ├── entity/
│   │   │       │   └── dto/
│   │   │       ├── repository/       # MongoDB Repositories
│   │   │       ├── service/          # Business Logic
│   │   │       │   └── SampleService.java
│   │   │       ├── messaging/        # Event listeners (Spring Cloud Stream)
│   │   │       │   ├── CloudEvent.java       # CloudEvent DTO
│   │   │       │   └── EventListeners.java   # Functional listeners
│   │   │       ├── exception/        # Exception handling
│   │   │       │   └── GlobalExceptionHandler.java
│   │   │       └── TemplateApplication.java
│   │   └── resources/
│   │       ├── application.properties       # Configuration principale
│   │       ├── application-dev.properties   # Config développement
│   │       ├── application-prod.properties  # Config production
│   │       └── logback-spring.xml          # Configuration logs
│   └── test/
│       └── java/
│           └── org/wespeak/template/
│               ├── integration/      # Tests d'intégration
│               └── unit/             # Tests unitaires
├── .github/
│   └── workflows/
│       └── build-and-publish.yml     # CI/CD pipeline
├── docker/
│   ├── Dockerfile                    # Image production
│   └── docker-compose.yml            # Env local complet
├── docs/
│   ├── API.md                        # Documentation API
│   └── ARCHITECTURE.md               # Architecture du service
├── build.gradle                      # Gradle configuration
├── settings.gradle                   # Gradle settings
├── gradlew                           # Gradle wrapper (Unix)
├── gradlew.bat                       # Gradle wrapper (Windows)
├── .gitignore
├── .dockerignore
└── README.md
```

## 🚀 Démarrage rapide

### 1. Créer un nouveau service depuis ce template

```bash
# Sur GitHub, cliquez sur "Use this template" 
# Ou clonez et renommez:
git clone https://github.com/we-speak-org/springboot-service-template.git my-new-service
cd my-new-service
```

### 2. Personnaliser pour votre service

```bash
# Renommer le package Java
find src -type f -name "*.java" -exec sed -i 's/org.wespeak.template/org.wespeak.myservice/g' {} +

# Mettre à jour build.gradle
sed -i "s/rootProject.name = 'template-service'/rootProject.name = 'my-new-service'/g" settings.gradle

# Mettre à jour application.yml
sed -i 's/spring.application.name: template-service/spring.application.name: my-new-service/g' src/main/resources/application.yml
```

### 3. Configurer les variables d'environnement

Copier `.env.example` vers `.env` et renseigner:

```env
# MongoDB
MONGODB_URI=mongodb://localhost:27017/myservice
MONGODB_DATABASE=myservice

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Keycloak
KEYCLOAK_SERVER_URL=http://localhost:8080
KEYCLOAK_REALM=wespeak
KEYCLOAK_CLIENT_ID=my-service
KEYCLOAK_CLIENT_SECRET=xxxxx

# Application
SERVER_PORT=8081
LOG_LEVEL=INFO
```

### 4. Lancer en développement

```bash
# Démarrer l'infrastructure (MongoDB, Kafka, Redis, Keycloak)
docker-compose up -d

# Lancer l'application
./gradlew bootRun

# Accéder à Swagger UI
open http://localhost:8081/swagger-ui.html
```

## 🏗️ Architecture & Patterns

### Layered Architecture

```
┌─────────────────────────────────────┐
│        REST Controllers             │  ← API Layer
├─────────────────────────────────────┤
│         Services                    │  ← Business Logic
├─────────────────────────────────────┤
│        Repositories                 │  ← Data Access
├─────────────────────────────────────┤
│   MongoDB | Redis | Kafka           │  ← Infrastructure
└─────────────────────────────────────┘
```

### Conventions de code

#### Entités MongoDB
```java
@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;
    
    @Indexed(unique = true)
    private String email;
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
}
```

#### DTOs de requête/réponse
```java
@Data
@Builder
public class CreateUserRequest {
    @NotBlank
    @Email
    private String email;
    
    @NotBlank
    @Size(min = 2, max = 100)
    private String displayName;
}

@Data
@Builder
public class UserResponse {
    private String id;
    private String email;
    private String displayName;
    private Instant createdAt;
}
```

#### Services avec cache
```java
@Service
@Slf4j
public class UserService {
    private final UserRepository repository;
    
    @Cacheable(value = "users", key = "#id")
    public UserResponse getUser(String id) {
        return repository.findById(id)
            .map(this::toResponse)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(String id) {
        repository.deleteById(id);
    }
}
```

#### Kafka Producer
```java
@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventProducer {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    
    public void publishUserCreated(User user) {
        UserEvent event = UserEvent.builder()
            .eventType("user.created")
            .userId(user.getId())
            .timestamp(Instant.now())
            .build();
            
        kafkaTemplate.send("user.events", user.getId(), event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send event", ex);
                } else {
                    log.info("Event sent: {}", event);
                }
            });
    }
}
```

#### Kafka Consumer
```java
@Component
@Slf4j
public class UserEventConsumer {
    @KafkaListener(
        topics = "user.events",
        groupId = "my-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(UserEvent event) {
        log.info("Received event: {}", event);
        // Process event
    }
}
```

## 🔒 Sécurité avec Keycloak

Le template est pré-configuré pour utiliser Keycloak:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt())
            .build();
    }
}
```

Endpoints protégés par rôles:
```java
@PreAuthorize("hasRole('USER')")
@GetMapping("/profile")
public UserResponse getProfile() { ... }

@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/users/{id}")
public void deleteUser(@PathVariable String id) { ... }
```

## 📊 Observabilité

### Health Checks
```bash
# Health check global
curl http://localhost:8081/actuator/health

# Détails par composant
curl http://localhost:8081/actuator/health/mongo
curl http://localhost:8081/actuator/health/redis
curl http://localhost:8081/actuator/health/kafka
```

### Métriques Prometheus
```bash
curl http://localhost:8081/actuator/prometheus
```

### Logging structuré (JSON)
```json
{
  "timestamp": "2025-01-02T14:30:00.123Z",
  "level": "INFO",
  "thread": "http-nio-8081-exec-1",
  "logger": "org.wespeak.service.UserService",
  "message": "User created",
  "context": {
    "correlationId": "abc-123",
    "userId": "user-456"
  }
}
```

## 🧪 Tests

### Tests unitaires
```bash
./gradlew test
```

### Tests d'intégration (avec Testcontainers)
```bash
./gradlew integrationTest
```

### Test de l'API
```java
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Testcontainers
class UserControllerIntegrationTest {
    @Container
    static MongoDBContainer mongodb = new MongoDBContainer("mongo:7.0");
    
    @Test
    void shouldCreateUser() {
        given()
            .contentType(ContentType.JSON)
            .body(new CreateUserRequest("test@example.com", "Test User"))
        .when()
            .post("/api/users")
        .then()
            .statusCode(201)
            .body("email", equalTo("test@example.com"));
    }
}
```

## 🐳 Docker

### Build de l'image
```bash
docker build -t ghcr.io/we-speak-org/my-service:latest .
```

### Run local
```bash
docker run -p 8081:8081 \
  -e MONGODB_URI=mongodb://host.docker.internal:27017/myservice \
  -e KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  ghcr.io/we-speak-org/my-service:latest
```

## 🚢 CI/CD avec GitHub Actions

Le workflow `.github/workflows/build-and-publish.yml` est automatiquement déclenché sur:
- Push sur `main` → Build + Test + Publish image
- Pull Request → Build + Test uniquement
- Manual dispatch → Permet de choisir le tag

L'image est publiée sur GitHub Container Registry: `ghcr.io/we-speak-org/my-service`

## 📦 Dépendances principales

| Dépendance | Version | Usage |
|------------|---------|-------|
| Spring Boot | 4.0.x | Framework core |
| Java | 21 | Runtime |
| MongoDB | 7.0+ | Base de données |
| Kafka | 3.6+ | Event streaming |
| Redis | 7.2+ | Cache distribué |
| Keycloak | 23+ | Authentification |

## 🤝 Contribution

1. Forkez ce template
2. Créez une branche (`git checkout -b feature/amazing`)
3. Committez vos changements (`git commit -m 'Add amazing feature'`)
4. Pushez (`git push origin feature/amazing`)
5. Ouvrez une Pull Request

## 📝 Checklist pour nouveau service

- [ ] Cloner le template
- [ ] Renommer packages et artefacts
- [ ] Configurer variables d'environnement
- [ ] Définir le modèle de données (entités)
- [ ] Créer les repositories
- [ ] Implémenter les services
- [ ] Créer les REST controllers
- [ ] Documenter les endpoints (OpenAPI)
- [ ] Configurer les Kafka topics (producer/consumer)
- [ ] Ajouter les tests unitaires
- [ ] Ajouter les tests d'intégration
- [ ] Configurer le pipeline GitHub Actions
- [ ] Tester en local avec docker-compose
- [ ] Créer la documentation API
- [ ] Déployer en environnement de développement

## 📚 Documentation complémentaire

- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data MongoDB](https://docs.spring.io/spring-data/mongodb/reference/)
- [Spring Kafka](https://docs.spring.io/spring-kafka/reference/)
- [Keycloak Spring Adapter](https://www.keycloak.org/docs/latest/securing_apps/#_spring_boot_adapter)

## 📞 Support

Pour toute question ou problème avec ce template:
- Ouvrir une issue sur GitHub
- Consulter la documentation WeSpeak
- Contacter l'équipe technique

---

**Version**: 1.0.0  
**Maintenu par**: WeSpeak Team  
**License**: MIT

