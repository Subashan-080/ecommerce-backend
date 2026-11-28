package edu.jsp.ecommerce.Serviece;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import edu.jsp.ecommerce.Request.OrderRequest;
import edu.jsp.ecommerce.Response.OrderResponse;
import edu.jsp.ecommerce.Response.SellerOrderResponse;
import edu.jsp.ecommerce.enums.OrderStatus;

public interface OrderService  {
	OrderResponse placeOrder(String email, OrderRequest request);
    OrderResponse getOrderById(Long id, String email);
    Page<OrderResponse> getUserOrders(String email, Pageable pageable);
    Page<OrderResponse> getAllOrders(Pageable pageable);
    OrderResponse updateOrderStatus(Long id, OrderStatus status);
    void cancelOrder(Long id, String email, String reason);
    Page<SellerOrderResponse> getSellerOrders(String email, Pageable pageable);
    SellerOrderResponse getSellerOrderById(Long orderId, String email);
    SellerOrderResponse updateSellerOrderItemStatus(
            Long orderId,
            Long itemId,
            String email,
            OrderStatus status,
            String trackingNumber);

}
