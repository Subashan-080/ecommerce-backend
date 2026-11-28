package edu.jsp.ecommerce.Serviece;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import edu.jsp.ecommerce.Request.ProductRequest;
import edu.jsp.ecommerce.Response.ProductResponse;

public interface ProductService {
	 ProductResponse createProduct(ProductRequest request, String sellerEmail, Long sellerId);

	    ProductResponse updateProduct(Long id, ProductRequest request, String sellerEmail);

	    void deleteProduct(Long id, String sellerEmail);

	    ProductResponse getProductById(Long id);

	    Page<ProductResponse> getAllProducts(Pageable pageable);

	    Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);

	    Page<ProductResponse> getProductsByBrand(Long brandId, Pageable pageable);

	    Page<ProductResponse> getProductsByCategoryAndBrand(
	            Long categoryId, Long brandId, Pageable pageable);

	    Page<ProductResponse> searchProducts(String keyword, Pageable pageable);

	    Page<ProductResponse> getSellerProducts(String sellerEmail, Pageable pageable);
	    
	    
	    Page<ProductResponse> filterProducts(
	            Long categoryId,
	            Long brandId,
	            BigDecimal minPrice,
	            BigDecimal maxPrice,
	            String keyword,
	            Pageable pageable
	    );


}
