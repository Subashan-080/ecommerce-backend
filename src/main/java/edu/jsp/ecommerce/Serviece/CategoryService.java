package edu.jsp.ecommerce.Serviece;

import java.util.List;

import edu.jsp.ecommerce.entity.Category;

public interface CategoryService {

    Category createCategory(Category category);

    List<Category> getAllCategories();

    List<Category> getActiveCategories();

    Category updateCategory(Long id, Category category);

    void deleteCategory(Long id);
}