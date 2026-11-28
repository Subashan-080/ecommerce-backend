package edu.jsp.ecommerce.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import edu.jsp.ecommerce.enums.OrderStatus;
import edu.jsp.ecommerce.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class OrderResponse {
	private Long id;
	private OrderStatus status;
	private BigDecimal totalAmount;
	private String trackingNumber;
	private PaymentMethod paymentMethod;
	private String shippingAddress;
	private List<OrderItemResponse>items;
	private LocalDateTime createdAt;
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class OrderItemResponse{
		private Long productId;
		private String productName;
		private String thumbnailUrl;
		private int quantity;
		private BigDecimal unitPrice;
		private BigDecimal totalPrice;
	}

}
