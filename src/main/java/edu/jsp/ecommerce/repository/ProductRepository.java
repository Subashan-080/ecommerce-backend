
package edu.jsp.ecommerce.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.jsp.ecommerce.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    // Get all active products with active categories
    @Query("SELECT p FROM Product p WHERE p.active = true "
            + "AND (p.category IS NULL OR p.category.active = true)")
    Page<Product> findByActiveTrue(Pageable pageable);

    // Get products by brand
    @Query("SELECT p FROM Product p WHERE p.active = true "
            + "AND p.brand.id = :brandId "
            + "AND (p.category IS NULL OR p.category.active = true)")
    Page<Product> findByActiveTrueAndBrandId(
            @Param("brandId") Long brandId, Pageable pageable);

    // Get products by category
    @Query("SELECT p FROM Product p WHERE p.active = true "
            + "AND p.category.id = :categoryId "
            + "AND p.category.active = true")
    Page<Product> findByActiveTrueAndCategoryId(
            @Param("categoryId") Long categoryId, Pageable pageable);

    // Get products by category and brand
    @Query("SELECT p FROM Product p WHERE p.active = true "
            + "AND p.category.id = :categoryId "
            + "AND p.category.active = true "
            + "AND p.brand.id = :brandId")
    Page<Product> findByActiveTrueAndCategoryIdAndBrandId(
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            Pageable pageable);

    // Get product by ID
    @Query("SELECT p FROM Product p WHERE p.id = :id "
            + "AND p.active = true "
            + "AND (p.category IS NULL OR p.category.active = true)")
    Optional<Product> findByIdAndActiveTrue(@Param("id") Long id);

    // Seller product management - keep seller's own active products
    Page<Product> findByActiveTrueAndSellerId(Long sellerId, Pageable pageable);

    // Search active products with active categories
    @Query("SELECT p FROM Product p WHERE p.active = true "
            + "AND (p.category IS NULL OR p.category.active = true) "
            + "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable);

    // Filter products with active categories
    @Query("SELECT p FROM Product p WHERE p.active = true "
            + "AND (p.category IS NULL OR p.category.active = true) "
            + "AND (:categoryId IS NULL OR p.category.id = :categoryId) "
            + "AND (:brandId IS NULL OR p.brand.id = :brandId) "
            + "AND (:minPrice IS NULL OR p.price >= :minPrice) "
            + "AND (:maxPrice IS NULL OR p.price <= :maxPrice) "
            + "AND (:keyword IS NULL OR "
            + "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))))")
    Page<Product> filterProducts(
            @Param("categoryId") Long categoryId,
            @Param("brandId") Long brandId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("keyword") String keyword,
            Pageable pageable);

    boolean existsByCategoryId(Long categoryId);

    boolean existsByBrandId(Long brandId);
}
