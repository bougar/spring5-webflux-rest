package es.lareira.webflux.bootstrap;

import es.lareira.webflux.domain.Vendor;
import es.lareira.webflux.repository.VendorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class VendorBootstrap implements CommandLineRunner {

  private final VendorRepository vendorRepository;

  public VendorBootstrap(final VendorRepository vendorRepository) {
    this.vendorRepository = vendorRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
    if (this.vendorRepository.count().block() != 0) {
      return;
    }

    final Vendor jonh = new Vendor();
    jonh.setFirstName("Jonh");
    jonh.setLastName("Kenedy");

    final Vendor amy = new Vendor();
    amy.setFirstName("Amy");
    amy.setLastName("Farrafauler");

    this.vendorRepository.save(jonh).block();
    this.vendorRepository.save(amy).block();
  }
}
