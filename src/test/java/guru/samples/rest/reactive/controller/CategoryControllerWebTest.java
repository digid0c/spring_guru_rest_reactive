package guru.samples.rest.reactive.controller;

import guru.samples.rest.reactive.domain.Category;
import guru.samples.rest.reactive.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static guru.samples.rest.reactive.controller.CategoryController.BASE_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToController;

public class CategoryControllerWebTest {

    private static final String CATEGORY_ID = "test";
    private static final String CATEGORY_NAME = "testName";
    private static final String BASE_URL_WITH_CATEGORY_ID = BASE_URL + "/" + CATEGORY_ID;
    private static final int CATEGORIES_SIZE = 3;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryController tested;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        webTestClient = bindToController(tested).build();
    }

    @Test
    public void shouldFindAllCategories() {
        when(categoryRepository.findAll()).thenReturn(Flux.just(new Category(), new Category(), new Category()));

        webTestClient.get()
                .uri(BASE_URL)
                .exchange()
                .expectBodyList(Category.class)
                .hasSize(CATEGORIES_SIZE);
    }

    @Test
    public void shouldFindCategoryById() {
        Category category = getCategory();
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Mono.just(category));

        webTestClient.get()
                .uri(BASE_URL_WITH_CATEGORY_ID)
                .exchange()
                .expectBody(Category.class)
                .isEqualTo(category);
    }

    @Test
    public void shouldCreateNewCategory() {
        Category categoryToCreate = getCategory();
        Mono<Category> categoryStream = Mono.just(categoryToCreate);
        when(categoryRepository.saveAll(any(Publisher.class))).thenReturn(Flux.just(categoryToCreate));

        webTestClient.post()
                .uri(BASE_URL)
                .body(categoryStream, Category.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Void.class);

        verify(categoryRepository).saveAll(any(Publisher.class));
    }

    @Test
    public void shouldUpdateCategory() {
        Category categoryToUpdate = getCategory();
        Mono<Category> categoryStream = Mono.just(categoryToUpdate);
        when(categoryRepository.save(any(Category.class))).thenReturn(Mono.just(categoryToUpdate));

        webTestClient.put()
                .uri(BASE_URL_WITH_CATEGORY_ID)
                .body(categoryStream, Category.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Category.class)
                .isEqualTo(categoryToUpdate);

        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    public void shouldPatchCategory() {
        Category categoryToPatch = getCategory();
        Mono<Category> categoryStream = Mono.just(categoryToPatch);
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Mono.just(categoryToPatch));
        when(categoryRepository.save(any(Category.class))).thenReturn(Mono.just(categoryToPatch));

        webTestClient.patch()
                .uri(BASE_URL_WITH_CATEGORY_ID)
                .body(categoryStream, Category.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Category.class)
                .isEqualTo(categoryToPatch);

        verify(categoryRepository).save(any(Category.class));
    }

    private Category getCategory() {
        return Category.builder()
                .id(CATEGORY_ID)
                .name(CATEGORY_NAME)
                .build();
    }
}
