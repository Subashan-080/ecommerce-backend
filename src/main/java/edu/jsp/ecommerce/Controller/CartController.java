package edu.jsp.ecommerce.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import org.springframework.security.core.context.SecurityContextHolder;
import edu.jsp.ecommerce.Response.ApiResponse;
import edu.jsp.ecommerce.Response.CartResponse;
import edu.jsp.ecommerce.Serviece.CartService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
public class CartController {
	private final CartService cartService;
	
	@GetMapping
	public ResponseEntity<ApiResponse<CartResponse>> getCart() {
	    return ResponseEntity.ok(
	            ApiResponse.sucess(
	                    cartService.getCart(getCurrentEmail()),
	                    "Cart fetched successfully"
	            )
	    );
	}
	
	@PostMapping("/add")
	public ResponseEntity<ApiResponse<CartResponse>> addtoCart(
	        @RequestParam Long productId,
	        @RequestParam(defaultValue = "1") int quantity) {

	    return ResponseEntity.ok(
	            ApiResponse.sucess(
	                    cartService.addToCart(
	                            getCurrentEmail(), productId, quantity),
	                    "Added to cart successfully"
	            )
	    );
	}
	
	
	
	@PutMapping("/items/{cartItemId}")
	public ResponseEntity<ApiResponse<CartResponse>> updateItem(
	        @PathVariable Long cartItemId,
	        @RequestParam int quantity) {

	    return ResponseEntity.ok(
	            ApiResponse.sucess(
	                    cartService.updateCartItem(
	                            getCurrentEmail(), cartItemId, quantity),
	                    "Cart updated successfully"
	            )
	    );
	}
	
	
	
	@DeleteMapping("/items/{cartItemId}")
	public ResponseEntity<ApiResponse<CartResponse>> removeItem(
	        @PathVariable Long cartItemId) {

	    return ResponseEntity.ok(
	            ApiResponse.sucess(
	                    cartService.removeFromCart(
	                            getCurrentEmail(), cartItemId),
	                    "Item removed successfully"
	            )
	    );
	}
	
	@DeleteMapping("/clear")
	public ResponseEntity<ApiResponse<String>> clearCart() {

	    cartService.clearCart(getCurrentEmail());

	    return ResponseEntity.ok(
	            ApiResponse.sucess(
	                    "cart is empty",
	                    "Cart cleared successfully"
	            )
	    );
	}
	
	
	private String getCurrentEmail() {
	    return SecurityContextHolder.getContext()
	            .getAuthentication().getName();
	}
}
