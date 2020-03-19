package guru.samples.rest.reactive.controller;

import guru.samples.rest.reactive.domain.Vendor;
import guru.samples.rest.reactive.repository.VendorRepository;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static guru.samples.rest.reactive.controller.VendorController.BASE_URL;
import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(BASE_URL)
public class VendorController {

    public static final String BASE_URL = "/api/v1/vendors";

    private final VendorRepository vendorRepository;

    @Autowired
    public VendorController(VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @GetMapping
    public Flux<Vendor> findAll() {
        return vendorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Vendor> findById(@PathVariable String id) {
        return vendorRepository.findById(id);
    }

    @ResponseStatus(CREATED)
    @PostMapping
    public Mono<Void> create(@RequestBody Publisher<Vendor> vendorStream) {
        return vendorRepository.saveAll(vendorStream).then();
    }

    @PutMapping("/{id}")
    public Mono<Vendor> update(@PathVariable String id, @RequestBody Vendor vendor) {
        vendor.setId(id);
        return vendorRepository.save(vendor);
    }

    @PatchMapping("/{id}")
    public Mono<Vendor> patch(@PathVariable String id, @RequestBody Vendor vendor) {
        return vendorRepository.findById(id)
                .map(foundVendor -> {
                    ofNullable(vendor.getFirstName()).ifPresent(foundVendor::setFirstName);
                    ofNullable(vendor.getLastName()).ifPresent(foundVendor::setLastName);
                    return foundVendor;
                })
                .flatMap(vendorRepository::save);
    }
}
