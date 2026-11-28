package edu.jsp.ecommerce.Response;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse{
	private Long cartId;
	private List<CartItemResponse> items;
	private BigDecimal totalAmount;
	private int totalItems;
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CartItemResponse{
		private Long cartItemId;
		private Long productId;
		private String productName;
		private String thumbnailUrl;
		private BigDecimal unitPrice;
		private int quantity;
		private BigDecimal subTotal;
		
	}

}
