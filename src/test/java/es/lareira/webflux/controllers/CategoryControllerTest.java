package es.lareira.webflux.controllers;

import es.lareira.webflux.domain.Category;
import es.lareira.webflux.repository.CategoryRepository;
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

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {
  @Mock private CategoryRepository categoryRepository;

  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    final CategoryController categoryController = new CategoryController(this.categoryRepository);
    this.webTestClient = WebTestClient.bindToController(categoryController).build();
  }

  @Test
  void getCategories() {
    final Category category1 = Category.builder().description(UUID.randomUUID().toString()).build();
    final Category category2 = Category.builder().description(UUID.randomUUID().toString()).build();
    Mockito.when(this.categoryRepository.findAll()).thenReturn(Flux.just(category1, category2));
    this.webTestClient
        .get()
        .uri("/api/v1/categories")
        .exchange()
        .expectBodyList(Category.class)
        .contains(category1)
        .contains(category2)
        .hasSize(2)
        .contains(category1)
        .contains(category2);
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "test", "linux"})
  void getCategory(final String categoryId) {
    final Category category =
        Category.builder().description(UUID.randomUUID().toString()).id(categoryId).build();
    Mockito.when(this.categoryRepository.findById(categoryId)).thenReturn(Mono.just(category));
    this.webTestClient
        .get()
        .uri("/api/v1/categories/" + categoryId)
        .exchange()
        .expectBody(Category.class)
        .isEqualTo(category);
  }

  @Test
  void createCategory() {
    final Category category = Category.builder().description(UUID.randomUUID().toString()).build();
    final Category savedCategory =
        Category.builder()
            .description(UUID.randomUUID().toString())
            .id(UUID.randomUUID().toString())
            .build();
    Mockito.when(this.categoryRepository.saveAll(any(Publisher.class)))
        .thenReturn(Flux.just(savedCategory));
    this.webTestClient
        .post()
        .uri("/api/v1/categories")
        .body(Mono.just(category), Category.class)
        .exchange()
        .expectStatus()
        .isCreated();
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "this", "test"})
  void updateCategory(final String id) {
    final Category category = Category.builder().description(UUID.randomUUID().toString()).build();
    final Category savedCategory =
        Category.builder().description(UUID.randomUUID().toString()).id(id).build();
    Mockito.when(this.categoryRepository.save(any(Category.class)))
        .thenReturn(Mono.just(savedCategory));
    this.webTestClient
        .put()
        .uri("/api/v1/categories/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(category), Category.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Category.class)
        .isEqualTo(savedCategory);
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "this", "test"})
  void partialUpdateCategory(final String id) {
    final Category savedCategory =
        Category.builder().description(UUID.randomUUID().toString()).id(id).build();
    Mockito.when(this.categoryRepository.findById(id)).thenReturn(Mono.just(savedCategory));
    final Category input = Category.builder().description(UUID.randomUUID().toString()).build();
    final Category updatedCategory =
        Category.builder().description(input.getDescription()).id(id).build();
    Mockito.when(this.categoryRepository.save(any(Category.class)))
        .thenReturn(Mono.just(updatedCategory));

    this.webTestClient
        .patch()
        .uri("/api/v1/categories/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(input), Category.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(Category.class)
        .isEqualTo(updatedCategory);

    final ArgumentCaptor<Category> argumentCaptor = ArgumentCaptor.forClass(Category.class);
    Mockito.verify(this.categoryRepository).save(argumentCaptor.capture());
    Assertions.assertEquals(input.getDescription(), argumentCaptor.getValue().getDescription());
  }
}
