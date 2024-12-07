package com.u3.shoppingcart.service.category;

import com.u3.shoppingcart.exceptions.AlreadyEsistsException;
import com.u3.shoppingcart.exceptions.ResourceNOtFoundException;
import com.u3.shoppingcart.model.Category;
import com.u3.shoppingcart.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new ResourceNOtFoundException("Category not found!")
        );
    }

    @Override
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category addCategory(Category category) {
        return Optional.of(category)
                .filter(c -> !categoryRepository.existsByName(category.getName()))
                .map(categoryRepository::save)
                .orElseThrow(() -> new AlreadyEsistsException(category.getName()+"not exists"));

    }

    @Override
    public Category updateCategory(Category category, Long id) {
        return Optional.ofNullable(getCategoryById(id))
                .map(oldCategory -> {oldCategory.setName(category.getName());
                return categoryRepository.save(oldCategory);
                }).orElseThrow(() -> new ResourceNOtFoundException("Category not found!"));
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.findById(id)
                .ifPresentOrElse(categoryRepository::delete, () -> {throw new ResourceNOtFoundException("Category not found!");});
    }
}
