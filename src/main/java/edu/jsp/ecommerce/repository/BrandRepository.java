package edu.jsp.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.jsp.ecommerce.entity.Brand;


public interface BrandRepository extends JpaRepository<Brand, Long> {
	boolean existsByNameIgnoreCase(String name);
	
	
	 boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}

