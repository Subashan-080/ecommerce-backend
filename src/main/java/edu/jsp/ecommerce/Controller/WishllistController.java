package edu.jsp.ecommerce.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.security.core.context.SecurityContextHolder;
import edu.jsp.ecommerce.Response.ApiResponse;
import edu.jsp.ecommerce.Response.WishlistResponse;
import edu.jsp.ecommerce.Serviece.WishListService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishllistController {
	private final WishListService wishListService;
	
	@GetMapping
	public ResponseEntity<ApiResponse<WishlistResponse>> getWishlist() {
	    return ResponseEntity.ok(
	        ApiResponse.sucess(
	            wishListService.getWishlist(getCurrentEmail()),
	            "Wishlist fetched successfully"
	        )
	    );
	}
	
	@PostMapping("/{productId}")
	public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
	        @PathVariable Long productId) {
	    return ResponseEntity.ok(
	        ApiResponse.sucess(
	            wishListService.addToWishlist(getCurrentEmail(), productId),
	            "Added to wishlist successfully"
	        )
	    );
	}
	@DeleteMapping("/{productId}")
	public ResponseEntity<ApiResponse<WishlistResponse>> remove(
	        @PathVariable Long productId) {
	    return ResponseEntity.ok(
	        ApiResponse.sucess(
	            wishListService.removeFromWishlist(getCurrentEmail(), productId),
	            "Removed from wishlist successfully"
	        )
	    );
	}
	
	
	@GetMapping("/{productId}/check")
	public ResponseEntity<ApiResponse<Map<String, Boolean>>> check(
	        @PathVariable Long productId) {
	    boolean inWl = wishListService.isInWishlist(getCurrentEmail(), productId);

	    return ResponseEntity.ok(
	        ApiResponse.sucess(Map.of("inWishlist", inWl), "Checked")
	    );
	}
	
	@DeleteMapping("/clear")
	public ResponseEntity<ApiResponse<Void>> clear() {
	    wishListService.clearWishlist(getCurrentEmail());

	    return ResponseEntity.ok(
	        ApiResponse.sucess(null, "Wishlist cleared")
	    );
	}
	
	private String getCurrentEmail() {
	    return SecurityContextHolder.getContext()
	            .getAuthentication().getName();
	}
	
	
	
	
	
	

}
