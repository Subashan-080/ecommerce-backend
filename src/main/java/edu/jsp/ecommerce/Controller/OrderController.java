package edu.jsp.ecommerce.Controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.ecommerce.Response.SellerOrderResponse;
import edu.jsp.ecommerce.Request.SellerOrderStatusRequest;
import org.springframework.security.core.Authentication;
import edu.jsp.ecommerce.Request.OrderRequest;
import edu.jsp.ecommerce.Response.ApiResponse;
import edu.jsp.ecommerce.Response.OrderResponse;
import edu.jsp.ecommerce.Serviece.OrderService;
import edu.jsp.ecommerce.enums.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(Authentication authentication,
			@Valid @RequestBody OrderRequest request) {
		String email = authentication.getName();

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.sucess(orderService.placeOrder(email, request), "Order placed"));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<Page<OrderResponse>>> getMyOrders(Authentication authentication,
			Pageable pageable) {
		String email = authentication.getName();

		return ResponseEntity.ok(ApiResponse.sucess(orderService.getUserOrders(email, pageable), "Orders fetched"));
	}

	@GetMapping("/seller")
	public ResponseEntity<ApiResponse<Page<SellerOrderResponse>>> getSellerOrders(Authentication authentication,
			Pageable pageable) {

		String email = authentication.getName();

		return ResponseEntity.ok(ApiResponse.sucess(orderService.getSellerOrders(email, pageable),
				"Seller orders fetched successfully"));
	}

	@GetMapping("/seller/{orderId}")
	public ResponseEntity<ApiResponse<SellerOrderResponse>> getSellerOrderById(@PathVariable Long orderId,
			Authentication authentication) {

		String email = authentication.getName();

		return ResponseEntity.ok(ApiResponse.sucess(orderService.getSellerOrderById(orderId, email),
				"Seller order details fetched successfully"));
	}
	
	@PatchMapping("/seller/{orderId}/items/{itemId}/status")
	public ResponseEntity<ApiResponse<SellerOrderResponse>> updateSellerOrderItemStatus(
	        @PathVariable Long orderId,
	        @PathVariable Long itemId,
	        @Valid @RequestBody SellerOrderStatusRequest request,
	        Authentication authentication) {

	    String email = authentication.getName();

	    SellerOrderResponse response = orderService.updateSellerOrderItemStatus(
	            orderId,
	            itemId,
	            email,
	            request.getStatus(),
	            request.getTrackingNumber()
	    );

	    return ResponseEntity.ok(
	            ApiResponse.sucess(response, "Seller order item status updated successfully")
	    );
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@PathVariable Long id, Authentication authentication) {
		String email = authentication.getName();

		return ResponseEntity.ok(ApiResponse.sucess(orderService.getOrderById(id, email), "Order fetched"));
	}

	@PatchMapping("/{id}/cancel")
	public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable Long id, Authentication authentication,
			@RequestParam(required = false) String reason) {
		String email = authentication.getName();

		orderService.cancelOrder(id, email, reason);

		return ResponseEntity.ok(ApiResponse.sucess(null, "Order cancelled"));
	}

	@GetMapping("/admin")
	public ResponseEntity<ApiResponse<Page<OrderResponse>>> getAllOrders(Pageable pageable) {

		return ResponseEntity.ok(ApiResponse.sucess(orderService.getAllOrders(pageable), "All orders fetched"));
	}

	@PatchMapping("/{id}/status")
	public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(@PathVariable Long id,
			@RequestParam OrderStatus status) {

		return ResponseEntity
				.ok(ApiResponse.sucess(orderService.updateOrderStatus(id, status), "Order status updated"));
	}
}
