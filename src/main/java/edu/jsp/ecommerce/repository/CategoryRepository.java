package edu.jsp.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.jsp.ecommerce.entity.Category;

public interface CategoryRepository extends JpaRepository<Category,Long> {
	boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    
    List<Category> findByActiveTrue();
}


