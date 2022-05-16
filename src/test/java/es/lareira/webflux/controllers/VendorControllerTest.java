package es.lareira.webflux.controllers;

import es.lareira.webflux.domain.Vendor;
import es.lareira.webflux.repository.VendorRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendorControllerTest {
  @Mock private VendorRepository vendorRepository;

  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    final VendorController vendorController = new VendorController(this.vendorRepository);

    this.webTestClient = WebTestClient.bindToController(vendorController).build();
  }

  @Test
  void getVendors() {
    final Vendor vendor1 =
        Vendor.builder()
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .id(UUID.randomUUID().toString())
            .build();

    final Vendor vendor2 =
        Vendor.builder()
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .id(UUID.randomUUID().toString())
            .build();

    when(this.vendorRepository.findAll()).thenReturn(Flux.just(vendor1, vendor2));
    this.webTestClient
        .get()
        .uri("/api/v1/vendors")
        .exchange()
        .expectStatus()
        .isOk()
        .expectBodyList(Vendor.class)
        .contains(vendor1)
        .contains(vendor2);
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "teaf", "my-lin"})
  void getVendor(final String id) {
    final Vendor vendor =
        Vendor.builder()
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .id(id)
            .build();

    when(this.vendorRepository.findById(id)).thenReturn(Mono.just(vendor));
    this.webTestClient
        .get()
        .uri("/api/v1/vendors/" + id)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Vendor.class)
        .isEqualTo(vendor);
  }

  @Test
  void createVendor() {
    final Vendor vendor =
        Vendor.builder()
            .lastName(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .id(UUID.randomUUID().toString())
            .build();

    final Vendor savedVendor =
        Vendor.builder()
            .lastName(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .id(UUID.randomUUID().toString())
            .build();

    when(this.vendorRepository.saveAll(any(Publisher.class))).thenReturn(Flux.just(savedVendor));

    this.webTestClient
        .post()
        .uri("/api/v1/vendors")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(vendor), Vendor.class)
        .exchange()
        .expectStatus()
        .isCreated()
        .expectBody(Vendor.class)
        .isEqualTo(savedVendor);
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "teaf", "my-lin"})
  void updateVendor(final String id) {
    final Vendor vendor =
        Vendor.builder()
            .lastName(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .build();

    final Vendor savedVendor =
        Vendor.builder()
            .lastName(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .id(UUID.randomUUID().toString())
            .build();

    when(this.vendorRepository.save(any(Vendor.class))).thenReturn(Mono.just(savedVendor));

    this.webTestClient
        .put()
        .uri("/api/v1/vendors/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(vendor), Vendor.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Vendor.class)
        .isEqualTo(savedVendor);

    final ArgumentCaptor<Vendor> argumentCaptor = ArgumentCaptor.forClass(Vendor.class);
    verify(this.vendorRepository).save(argumentCaptor.capture());
    Assertions.assertEquals(id, argumentCaptor.getValue().getId());
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "this", "test"})
  void partialUpdateVendor(final String id) {
    final Vendor savedVendor =
        Vendor.builder()
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .id(id)
            .build();
    Mockito.when(this.vendorRepository.findById(id)).thenReturn(Mono.just(savedVendor));
    final Vendor input =
        Vendor.builder()
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .build();
    final Vendor updatedVendor =
        Vendor.builder()
            .firstName(input.getFirstName())
            .lastName(input.getLastName())
            .id(id)
            .build();
    Mockito.when(this.vendorRepository.save(any(Vendor.class)))
        .thenReturn(Mono.just(updatedVendor));

    this.webTestClient
        .patch()
        .uri("/api/v1/vendors/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(input), Vendor.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Vendor.class)
        .isEqualTo(updatedVendor);

    final ArgumentCaptor<Vendor> argumentCaptor = ArgumentCaptor.forClass(Vendor.class);
    Mockito.verify(this.vendorRepository).save(argumentCaptor.capture());
    Assertions.assertEquals(input.getFirstName(), argumentCaptor.getValue().getFirstName());
    Assertions.assertEquals(input.getLastName(), argumentCaptor.getValue().getLastName());
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "this", "test"})
  void partialUpdateVendorOnlyOneAttribute(final String id) {
    final Vendor savedVendor =
        Vendor.builder()
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .id(id)
            .build();
    Mockito.when(this.vendorRepository.findById(id)).thenReturn(Mono.just(savedVendor));
    final Vendor input = Vendor.builder().lastName(UUID.randomUUID().toString()).build();
    final Vendor updatedVendor =
        Vendor.builder()
            .firstName(input.getFirstName())
            .lastName(input.getLastName())
            .id(id)
            .build();
    Mockito.when(this.vendorRepository.save(any(Vendor.class)))
        .thenReturn(Mono.just(updatedVendor));

    this.webTestClient
        .patch()
        .uri("/api/v1/vendors/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(input), Vendor.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Vendor.class)
        .isEqualTo(updatedVendor);

    final ArgumentCaptor<Vendor> argumentCaptor = ArgumentCaptor.forClass(Vendor.class);
    Mockito.verify(this.vendorRepository).save(argumentCaptor.capture());
    Assertions.assertEquals(input.getFirstName(), savedVendor.getFirstName());
    Assertions.assertEquals(input.getLastName(), argumentCaptor.getValue().getLastName());
  }
}
