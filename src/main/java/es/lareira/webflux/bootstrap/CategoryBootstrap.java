package es.lareira.webflux.bootstrap;

import es.lareira.webflux.domain.Category;
import es.lareira.webflux.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CategoryBootstrap implements CommandLineRunner {

  private final CategoryRepository categoryRepository;

  public CategoryBootstrap(final CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  public void run(final String... args) throws Exception {
    if (this.categoryRepository.count().block() != 0) {
      return;
    }
    final Category fruits = new Category();
    fruits.setDescription("Fruits");

    final Category exotic = new Category();
    exotic.setDescription("Exotic");

    final Category nuts = new Category();
    nuts.setDescription("Nuts");

    this.categoryRepository.save(exotic).block();
    this.categoryRepository.save(nuts).block();
    this.categoryRepository.save(fruits).block();
  }
}
