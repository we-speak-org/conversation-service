# Spring Cloud Stream - Guide d'utilisation

## Architecture Event-Driven avec Spring Cloud Stream

Ce template utilise **Spring Cloud Stream** avec le style fonctionnel pour la gestion des événements Kafka. Cette approche moderne et déclarative simplifie la configuration et sépare clairement les responsabilités.

## Pattern CloudEvent

### Qu'est-ce qu'un CloudEvent ?

CloudEvent est un format standardisé pour décrire des événements de manière interopérable. Notre implémentation suit cette spécification pour garantir la cohérence entre tous les microservices.

```java
public class CloudEvent<T> {
    private String eventType;        // Type d'événement (ex: "user.registered")
    private String source;           // Service source (ex: "auth-service")
    private String id;               // ID unique de l'événement
    private Instant time;            // Horodatage
    private T data;                  // Payload métier
    private String correlationId;    // ID de corrélation pour le tracing
    private String tenantId;         // ID du tenant (multi-tenancy)
}
```

## Comment créer un Event Listener

### 1. Définir le Payload

Créez un record ou une classe pour votre payload :

```java
public record UserRegisteredPayload(
    String userId,
    String email,
    String displayName
) {}
```

### 2. Créer le Consumer Bean

Dans `EventListeners.java`, ajoutez un bean Consumer :

```java
@Bean
public Consumer<CloudEvent<UserRegisteredPayload>> userRegisteredListener(UserService userService) {
    return event -> {
        log.info("Received user.registered event: {}", event.getId());
        
        try {
            UserRegisteredPayload data = event.getData();
            userService.handleUserRegistered(data);
            log.info("Successfully processed event: {}", event.getId());
        } catch (Exception e) {
            log.error("Error processing event: {}", event.getId(), e);
            throw new RuntimeException("Failed to process event", e);
        }
    };
}
```

### 3. Configurer le Binding dans application.properties

```properties
# Ajouter le nom de la fonction à la définition
spring.cloud.function.definition=userRegisteredListener,otherListener

# Configurer le binding input
spring.cloud.stream.bindings.userRegisteredListener-in-0.destination=user.events
spring.cloud.stream.bindings.userRegisteredListener-in-0.group=my-service-name
spring.cloud.stream.bindings.userRegisteredListener-in-0.consumer.max-attempts=3

# Configurer la DLQ
spring.cloud.stream.kafka.bindings.userRegisteredListener-in-0.consumer.enable-dlq=true
spring.cloud.stream.kafka.bindings.userRegisteredListener-in-0.consumer.dlq-name=user.events.dlq
```

### 4. Implémenter la logique métier dans un Service

```java
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    public void handleUserRegistered(UserRegisteredPayload payload) {
        // Logique métier ici
        log.info("Processing user registration for: {}", payload.email());
        
        // Exemple: sauvegarder en base
        UserEntity user = new UserEntity();
        user.setId(payload.userId());
        user.setEmail(payload.email());
        userRepository.save(user);
    }
}
```

## Configuration Avancée

### Retry et Backoff

```properties
# Nombre maximum de tentatives
spring.cloud.stream.bindings.myListener-in-0.consumer.max-attempts=5

# Délai initial entre les tentatives (ms)
spring.cloud.stream.bindings.myListener-in-0.consumer.back-off-initial-interval=1000

# Multiplicateur de backoff (exponentiel)
spring.cloud.stream.bindings.myListener-in-0.consumer.back-off-multiplier=2.0

# Délai maximum entre tentatives (ms)
spring.cloud.stream.bindings.myListener-in-0.consumer.back-off-max-interval=30000
```

### Dead Letter Queue (DLQ)

Les messages qui échouent après toutes les tentatives sont automatiquement envoyés vers la DLQ :

```properties
spring.cloud.stream.kafka.bindings.myListener-in-0.consumer.enable-dlq=true
spring.cloud.stream.kafka.bindings.myListener-in-0.consumer.dlq-name=my-topic.dlq
```

### Partitionnement

Pour garantir l'ordre des messages avec la même clé :

```properties
spring.cloud.stream.kafka.bindings.myListener-in-0.consumer.configuration.partition.assignment.strategy=org.apache.kafka.clients.consumer.RangeAssignor
```

## Production d'événements (optionnel)

Si vous devez produire des événements, utilisez `StreamBridge` :

```java
@Service
@RequiredArgsConstructor
public class EventProducerService {
    
    private final StreamBridge streamBridge;
    
    public void publishUserCreated(UserCreatedPayload payload) {
        CloudEvent<UserCreatedPayload> event = CloudEvent.<UserCreatedPayload>builder()
            .eventType("user.created")
            .source("my-service")
            .id(UUID.randomUUID().toString())
            .time(Instant.now())
            .data(payload)
            .build();
            
        streamBridge.send("user-out-0", event);
    }
}
```

Configuration du binding output :

```properties
spring.cloud.stream.bindings.user-out-0.destination=user.events
```

## Bonnes Pratiques

### ✅ À FAIRE

1. **Séparer les responsabilités** : Le listener reçoit l'événement et délègue au service
2. **Idempotence** : Gérer les messages en double (utiliser l'ID d'événement)
3. **Logging structuré** : Logger les IDs d'événement, correlation IDs
4. **Gestion d'erreurs** : Re-throw les exceptions pour déclencher le retry/DLQ
5. **Validation** : Valider le payload avant traitement

### ❌ À ÉVITER

1. **Logique métier dans le listener** : Garder le listener léger
2. **Transactions longues** : Risque de timeout Kafka
3. **Ignorer les erreurs** : Ne pas avaler les exceptions
4. **Bloquer le thread** : Pas d'opérations synchrones lentes

## Exemple Complet

Voir le code dans :
- `src/main/java/org/wespeak/template/messaging/EventListeners.java`
- `src/main/java/org/wespeak/template/service/SampleService.java`
- `src/main/resources/application.properties`

## Migration depuis @KafkaListener

Si vous migrez depuis l'ancien pattern `@KafkaListener` :

**Avant** :
```java
@KafkaListener(topics = "user.events", groupId = "my-service")
public void handleUserEvent(String message) {
    // ...
}
```

**Après** :
```java
@Bean
public Consumer<CloudEvent<UserPayload>> userEventListener(UserService service) {
    return event -> service.handleUserEvent(event.getData());
}
```

Configuration dans `application.properties` :
```properties
spring.cloud.function.definition=userEventListener
spring.cloud.stream.bindings.userEventListener-in-0.destination=user.events
spring.cloud.stream.bindings.userEventListener-in-0.group=my-service
```

## Ressources

- [Spring Cloud Stream Documentation](https://spring.io/projects/spring-cloud-stream)
- [CloudEvents Specification](https://cloudevents.io/)
- [Kafka Binder Reference](https://docs.spring.io/spring-cloud-stream/docs/current/reference/html/spring-cloud-stream-binder-kafka.html)
