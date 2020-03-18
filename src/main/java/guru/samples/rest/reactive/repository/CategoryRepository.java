package guru.samples.rest.reactive.repository;

import guru.samples.rest.reactive.domain.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {
}
