package org.wespeak.template.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.wespeak.template.CreateExampleRequest;
import org.wespeak.template.ExampleEntity;
import org.wespeak.template.ExampleResponse;
import org.wespeak.template.exception.ResourceNotFoundException;
import org.wespeak.template.repository.ExampleRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExampleService {

  private final ExampleRepository repository;

  @Cacheable(value = "examples", key = "#id")
  public ExampleResponse getById(String id) {
    log.info("Fetching example with id: {}", id);
    ExampleEntity entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Example", "id", id));
    return toResponse(entity);
  }

  public ExampleResponse getByCode(String code) {
    log.info("Fetching example with code: {}", code);
    ExampleEntity entity =
        repository
            .findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Example", "code", code));
    return toResponse(entity);
  }

  public List<ExampleResponse> findAll() {
    log.info("Fetching all examples");
    return repository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
  }

  public ExampleResponse create(CreateExampleRequest request) {
    log.info("Creating example with code: {}", request.getCode());

    if (repository.existsByCode(request.getCode())) {
      throw new IllegalArgumentException(
          "Example with code " + request.getCode() + " already exists");
    }

    ExampleEntity entity =
        ExampleEntity.builder()
            .code(request.getCode())
            .name(request.getName())
            .description(request.getDescription())
            .active(true)
            .build();

    ExampleEntity saved = repository.save(entity);
    log.info("Example created with id: {}", saved.getId());

    return toResponse(saved);
  }

  @CacheEvict(value = "examples", key = "#id")
  public void delete(String id) {
    log.info("Deleting example with id: {}", id);
    ExampleEntity entity =
        repository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Example", "id", id));

    repository.delete(entity);
    log.info("Example deleted: {}", id);
  }

  private ExampleResponse toResponse(ExampleEntity entity) {
    return ExampleResponse.builder()
        .id(entity.getId())
        .code(entity.getCode())
        .name(entity.getName())
        .description(entity.getDescription())
        .active(entity.isActive())
        .createdAt(entity.getCreatedAt())
        .updatedAt(entity.getUpdatedAt())
        .build();
  }
}
