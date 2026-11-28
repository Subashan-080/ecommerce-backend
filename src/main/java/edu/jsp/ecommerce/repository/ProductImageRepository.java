package edu.jsp.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.jsp.ecommerce.entity.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

}
