package es.lareira.webflux.controllers;

import es.lareira.webflux.domain.Category;
import es.lareira.webflux.repository.CategoryRepository;
import org.reactivestreams.Publisher;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
  private final CategoryRepository categoryRepository;

  public CategoryController(final CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @GetMapping
  public Flux<Category> getCategories() {
    return this.categoryRepository.findAll();
  }

  @GetMapping("/{id}")
  public Mono<Category> getCategory(@PathVariable final String id) {
    return this.categoryRepository.findById(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Void> createCategory(@RequestBody final Publisher<Category> categoryPublisher) {
    return this.categoryRepository.saveAll(categoryPublisher).then();
  }

  @PutMapping("/{id}")
  public Mono<Category> updateCategory(
      @PathVariable final String id, @RequestBody final Category category) {
    category.setId(id);
    return this.categoryRepository.save(category);
  }

  @PatchMapping("/{id}")
  public Mono<Category> partialUpdateCategory(
      @PathVariable final String id, @RequestBody final Category category) {
    return this.categoryRepository
        .findById(id)
        .map(
            savedCategory -> {
              BeanUtils.copyProperties(category, savedCategory);
              return savedCategory;
            })
        .flatMap(this.categoryRepository::save);
  }
}
