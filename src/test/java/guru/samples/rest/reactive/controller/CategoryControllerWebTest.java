package guru.samples.rest.reactive.controller;

import guru.samples.rest.reactive.domain.Category;
import guru.samples.rest.reactive.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static guru.samples.rest.reactive.controller.CategoryController.BASE_URL;
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
        Category category = Category.builder()
                .id(CATEGORY_ID)
                .name(CATEGORY_NAME)
                .build();
        when(categoryRepository.findById(CATEGORY_ID)).thenReturn(Mono.just(category));

        webTestClient.get()
                .uri(BASE_URL_WITH_CATEGORY_ID)
                .exchange()
                .expectBody(Category.class)
                .isEqualTo(category);
    }
}
