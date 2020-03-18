package guru.samples.rest.reactive.bootstrap;

import guru.samples.rest.reactive.domain.Category;
import guru.samples.rest.reactive.domain.Vendor;
import guru.samples.rest.reactive.repository.CategoryRepository;
import guru.samples.rest.reactive.repository.VendorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

import static java.lang.Long.valueOf;
import static java.util.stream.Collectors.toList;

@Slf4j
@Component
public class BootstrapDataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;

    @Autowired
    public BootstrapDataLoader(CategoryRepository categoryRepository, VendorRepository vendorRepository) {
        this.categoryRepository = categoryRepository;
        this.vendorRepository = vendorRepository;
    }

    @Override
    public void run(String... args) {
        loadCategories();
        loadVendors();
    }

    private void loadCategories() {
        if (!valueOf(0).equals(categoryRepository.count().block())) {
            return;
        }
        log.debug("Loading categories...");

        List<Category> categories = Stream.of("Fruits", "Dried", "Fresh", "Exotic", "Nuts")
                .map(categoryName -> Category.builder().name(categoryName).build())
                .collect(toList());

        categoryRepository.saveAll(categories).subscribe();

        log.debug("Categories loaded!");
    }

    private void loadVendors() {
        if (!valueOf(0).equals(vendorRepository.count().block())) {
            return;
        }
        log.debug("Loading vendors...");

        List<Vendor> vendors = Stream.of(Pair.of("Michael", "Weston"), Pair.of("Sam", "Axe"))
                .map(vendorNamePair -> Vendor.builder()
                        .firstName(vendorNamePair.getFirst())
                        .lastName(vendorNamePair.getSecond())
                        .build())
                .collect(toList());

        vendorRepository.saveAll(vendors).subscribe();

        log.debug("Vendors loaded!");
    }
}
