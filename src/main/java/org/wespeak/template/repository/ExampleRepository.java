package org.wespeak.template.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.wespeak.template.ExampleEntity;

@Repository
public interface ExampleRepository extends MongoRepository<ExampleEntity, String> {

  Optional<ExampleEntity> findByCode(String code);

  List<ExampleEntity> findByActive(boolean active);

  boolean existsByCode(String code);
}
