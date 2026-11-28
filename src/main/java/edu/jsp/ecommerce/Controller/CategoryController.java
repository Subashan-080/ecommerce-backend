package edu.jsp.ecommerce.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.ecommerce.Serviece.CategoryService;
import edu.jsp.ecommerce.entity.Category;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryService categoryService;
	
	@PostMapping
	public ResponseEntity<Category> createCategory(
	        @RequestBody Category category) {
	    return ResponseEntity.ok(categoryService.createCategory(category));
	}
	
	
	@GetMapping
	public ResponseEntity<List<Category>> getAllCategories() {
	    return ResponseEntity.ok(categoryService.getAllCategories());
	}
	
	
	@GetMapping("/active")
	public ResponseEntity<List<Category>> getActiveCategories() {
	    return ResponseEntity.ok(categoryService.getActiveCategories());
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Category> updateCategory(
	        @PathVariable Long id,
	        @RequestBody Category category) {
	    return ResponseEntity.ok(categoryService.updateCategory(id, category));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
	    categoryService.deleteCategory(id);
	    return ResponseEntity.noContent().build();
	}

}
