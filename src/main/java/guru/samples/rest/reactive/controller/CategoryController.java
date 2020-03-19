package guru.samples.rest.reactive.controller;

import guru.samples.rest.reactive.domain.Category;
import guru.samples.rest.reactive.repository.CategoryRepository;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static guru.samples.rest.reactive.controller.CategoryController.BASE_URL;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(BASE_URL)
public class CategoryController {

    public static final String BASE_URL = "/api/v1/categories";

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public Flux<Category> findAll() {
        return categoryRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Category> findById(@PathVariable String id) {
        return categoryRepository.findById(id);
    }

    @ResponseStatus(CREATED)
    @PostMapping
    public Mono<Void> create(@RequestBody Publisher<Category> categoryStream) {
        return categoryRepository.saveAll(categoryStream).then();
    }

    @PutMapping("/{id}")
    public Mono<Category> update(@PathVariable String id, @RequestBody Category category) {
        category.setId(id);
        return categoryRepository.save(category);
    }

    @PatchMapping("/{id}")
    public Mono<Category> patch(@PathVariable String id, @RequestBody Category category) {
        return categoryRepository.findById(id)
                .map(foundCategory -> {
                    ofNullable(category.getName()).ifPresent(foundCategory::setName);
                    return foundCategory;
                })
                .flatMap(categoryRepository::save);
    }
}
