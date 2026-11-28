package edu.jsp.ecommerce.SeriviecImp;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.jsp.ecommerce.Serviece.CategoryService;
import edu.jsp.ecommerce.entity.Category;
import edu.jsp.ecommerce.execption.BadRequestException;
import edu.jsp.ecommerce.execption.ResoruceNotFoundException;
import edu.jsp.ecommerce.repository.CategoryRepository;
import edu.jsp.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;
	private final ProductRepository productRepository;

	@Override
	public Category createCategory(Category category) {
		if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
			throw new BadRequestException("Category already exists");
		}

		return categoryRepository.save(category);
	}

	@Override
	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	@Override
	public List<Category> getActiveCategories() {
		return categoryRepository.findByActiveTrue();
	}

	@Override
	public Category updateCategory(Long id, Category category) {

		Category existingCategory = categoryRepository.findById(id)
				.orElseThrow(() -> new ResoruceNotFoundException("Category not found"));

		if (categoryRepository.existsByNameIgnoreCaseAndIdNot(category.getName(), id)) {
			throw new BadRequestException("Category already exists");
		}

		existingCategory.setName(category.getName());
		existingCategory.setDescription(category.getDescription());
		existingCategory.setImageUrl(category.getImageUrl());
		existingCategory.setActive(category.isActive());

		return categoryRepository.save(existingCategory);
	}

	@Override
	public void deleteCategory(Long id) {

		Category category = categoryRepository.findById(id)
				.orElseThrow(() -> new ResoruceNotFoundException("Category not found"));

		if (productRepository.existsByCategoryId(id)) {
			throw new BadRequestException("Cannot delete category because products are assigned to it");
		}

		if (!category.getChildren().isEmpty()) {
			throw new BadRequestException("Cannot delete category because it has child categories");
		}

		categoryRepository.delete(category);
	}
}
