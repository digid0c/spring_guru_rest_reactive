package guru.samples.rest.reactive.controller;

import guru.samples.rest.reactive.domain.Vendor;
import guru.samples.rest.reactive.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static guru.samples.rest.reactive.controller.VendorController.BASE_URL;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
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
        Vendor vendor = getVendor();
        when(vendorRepository.findById(VENDOR_ID)).thenReturn(Mono.just(vendor));

        webTestClient.get()
                .uri(BASE_URL_WITH_VENDOR_ID)
                .exchange()
                .expectBody(Vendor.class)
                .isEqualTo(vendor);
    }

    @Test
    public void shouldCreateNewVendor() {
        Vendor vendorToCreate = getVendor();
        Mono<Vendor> vendorStream = Mono.just(vendorToCreate);
        when(vendorRepository.saveAll(any(Publisher.class))).thenReturn(Flux.just(vendorToCreate));

        webTestClient.post()
                .uri(BASE_URL)
                .body(vendorStream, Vendor.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Void.class);

        verify(vendorRepository).saveAll(any(Publisher.class));
    }

    @Test
    public void shouldUpdateVendor() {
        Vendor vendorToUpdate = getVendor();
        Mono<Vendor> vendorStream = Mono.just(vendorToUpdate);
        when(vendorRepository.save(any(Vendor.class))).thenReturn(Mono.just(vendorToUpdate));

        webTestClient.put()
                .uri(BASE_URL_WITH_VENDOR_ID)
                .body(vendorStream, Vendor.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Vendor.class)
                .isEqualTo(vendorToUpdate);

        verify(vendorRepository).save(any(Vendor.class));
    }

    @Test
    public void shouldPatchVendor() {
        Vendor vendorToPatch = getVendor();
        Mono<Vendor> vendorStream = Mono.just(vendorToPatch);
        when(vendorRepository.findById(VENDOR_ID)).thenReturn(Mono.just(vendorToPatch));
        when(vendorRepository.save(any(Vendor.class))).thenReturn(Mono.just(vendorToPatch));

        webTestClient.patch()
                .uri(BASE_URL_WITH_VENDOR_ID)
                .body(vendorStream, Vendor.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Vendor.class)
                .isEqualTo(vendorToPatch);

        verify(vendorRepository).save(any(Vendor.class));
    }

    private Vendor getVendor() {
        return Vendor.builder()
                .id(VENDOR_ID)
                .firstName(VENDOR_FIRST_NAME)
                .lastName(VENDOR_LAST_NAME)
                .build();
    }
}
