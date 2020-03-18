package guru.samples.rest.reactive.repository;

import guru.samples.rest.reactive.domain.Vendor;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface VendorRepository extends ReactiveMongoRepository<Vendor, String> {
}
