package edu.jsp.ecommerce.Controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;




import org.springframework.security.core.Authentication;
import edu.jsp.ecommerce.Request.ProductRequest;
import edu.jsp.ecommerce.Response.ApiResponse;
import edu.jsp.ecommerce.Response.ProductResponse;
import edu.jsp.ecommerce.Serviece.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
	private final  ProductService productService;
	
	@GetMapping
	public ResponseEntity<ApiResponse<Page<ProductResponse>>> getProducts(
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false)Long brandId,
			@RequestParam(required = false) BigDecimal minprice,
			@RequestParam(required = false) BigDecimal maxprice,
			@RequestParam(required = false) String keyword,
			@RequestParam (defaultValue="newest") String sortBy,
			@RequestParam (defaultValue = "0") int page,
			@RequestParam (defaultValue = "12") int size){
		
		Pageable pageable=buildPageable(page,size,sortBy);
		
		boolean hasFilter=categoryId !=null || brandId !=null ||
				minprice !=null || maxprice !=null || 
				(keyword != null && !keyword.isBlank());
		Page<ProductResponse> result=hasFilter
				? productService.filterProducts(categoryId, brandId, minprice, maxprice, keyword, pageable)
						:productService.getAllProducts(pageable);
		return ResponseEntity.ok(ApiResponse.sucess(result, "produts fetched"));
		
	}
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<ProductResponse>> getProduct(@PathVariable Long id){
		return ResponseEntity.ok(
				ApiResponse.sucess(productService.getProductById(id),"product fetched"));
		
	}
	
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<Page<ProductResponse>>> search(
			@RequestParam String keyword,
			Pageable pageable
			){
		return ResponseEntity.ok(
				ApiResponse.sucess(
						productService.searchProducts(keyword, pageable), 
						"search results"));
		
	}
	@GetMapping("/category/{categoryId}")
	public ResponseEntity<ApiResponse<Page<ProductResponse>>> getByCategory(
			@PathVariable Long categoryId,
			Pageable pageable
			){
		return ResponseEntity.ok(ApiResponse.sucess(productService.getProductsByCategory(categoryId, pageable), "products by category"));
		
	}
	@GetMapping("/brand/{brandId}")
	public ResponseEntity<ApiResponse<Page<ProductResponse>>> getByBrand(
			@PathVariable Long brandId,
			Pageable pageable
			){
		return ResponseEntity.ok(ApiResponse.sucess(productService.getProductsByBrand(brandId, pageable), "products by brands"));
	}
	
	@GetMapping("/my-products")
	public ResponseEntity<ApiResponse<Page<ProductResponse>>> getMyProducts(
			 Authentication authentication,
			Pageable pageable
			){
		 String email = authentication.getName();
		return ResponseEntity.ok(ApiResponse.sucess(productService.getSellerProducts(email, pageable),"Seller product"));
		 
	}
	
	
	
	@PostMapping
	public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
	        @Valid @RequestBody ProductRequest request,
	        Authentication authentication) {

	    boolean isAdmin = authentication.getAuthorities().stream()
	            .anyMatch(authority ->
	                    authority.getAuthority().equals("ROLE_ADMIN"));

	    String sellerEmail = isAdmin ? null : authentication.getName();

	    Long sellerId = isAdmin ? request.getSellerId() : null;

	    return ResponseEntity.status(HttpStatus.CREATED)
	            .body(ApiResponse.sucess(
	                    productService.createProduct(
	                            request, sellerEmail, sellerId),
	                    "product created"));
	}
	
	
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
			@PathVariable Long id,
			@Valid @RequestBody ProductRequest request,
			Authentication authentication){
		String email = authentication.getName();
		
		return ResponseEntity.ok(ApiResponse.sucess(productService.updateProduct(id, request, email), "porduct updated"));
		
		
	}
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteProduct(
			@PathVariable Long id,
			Authentication authentication){
		 String email = authentication.getName();

		productService.deleteProduct(id, email);
		return ResponseEntity.ok(ApiResponse.sucess(null, "product deleted"));
		
	}
	
	
	private Pageable buildPageable(int page,int size,String sort) {
		Sort s=switch (sort) {
		case  "price_asc" ->Sort.by("price").ascending();
		case   "price_desc" ->Sort.by("price").descending();
		case  "name_asc" ->Sort.by("name").ascending();
		case  "name_desc" ->Sort.by("name").descending();
		default -> Sort.by("createAt").descending();
	};
	return PageRequest.of(page, size,s);

}
}
