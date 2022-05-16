package es.lareira.webflux.controllers;

import es.lareira.webflux.domain.Vendor;
import es.lareira.webflux.repository.VendorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/vendors")
public class VendorController {
  private final VendorRepository vendorRepository;

  public VendorController(final VendorRepository vendorRepository) {
    this.vendorRepository = vendorRepository;
  }

  @GetMapping
  public Flux<Vendor> getVendors() {
    return this.vendorRepository.findAll();
  }

  @GetMapping("/{id}")
  public Mono<Vendor> getVendor(@PathVariable final String id) {
    return this.vendorRepository.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Vendor> createVendor(@RequestBody final Mono<Vendor> vendor) {
    return this.vendorRepository.saveAll(vendor).next();
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Vendor> updateVendor(
      @PathVariable final String id, @RequestBody final Vendor vendor) {
    vendor.setId(id);
    return this.vendorRepository.save(vendor);
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Vendor> partialUpdateVendor(
      @PathVariable final String id, @RequestBody final Vendor vendor) {
    return this.vendorRepository
        .findById(id)
        .map(
            savedCategory -> {
              BeanUtils.copyProperties(vendor, savedCategory);
              return savedCategory;
            })
        .flatMap(this.vendorRepository::save);
  }
}
