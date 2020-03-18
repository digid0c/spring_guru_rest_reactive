package guru.samples.rest.reactive.controller;

import guru.samples.rest.reactive.domain.Vendor;
import guru.samples.rest.reactive.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static guru.samples.rest.reactive.controller.VendorController.BASE_URL;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToController;

public class VendorControllerWebTest {

    private static final String VENDOR_ID = "test";
    private static final String VENDOR_FIRST_NAME = "Martin";
    private static final String VENDOR_LAST_NAME = "Fowler";
    private static final String BASE_URL_WITH_VENDOR_ID = BASE_URL + "/" + VENDOR_ID;
    private static final int VENDORS_SIZE = 3;

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private VendorController tested;

    private WebTestClient webTestClient;

    @BeforeEach
    public void setUp() {
        initMocks(this);
        webTestClient = bindToController(tested).build();
    }

    @Test
    public void shouldFindAllVendors() {
        when(vendorRepository.findAll()).thenReturn(Flux.just(new Vendor(), new Vendor(), new Vendor()));

        webTestClient.get()
                .uri(BASE_URL)
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(VENDORS_SIZE);
    }

    @Test
    public void shouldFindVendorById() {
        Vendor vendor = Vendor.builder()
                .id(VENDOR_ID)
                .firstName(VENDOR_FIRST_NAME)
                .lastName(VENDOR_LAST_NAME)
                .build();
        when(vendorRepository.findById(VENDOR_ID)).thenReturn(Mono.just(vendor));

        webTestClient.get()
                .uri(BASE_URL_WITH_VENDOR_ID)
                .exchange()
                .expectBody(Vendor.class)
                .isEqualTo(vendor);
    }
}
