 package edu.jsp.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.jsp.ecommerce.entity.Order;
import edu.jsp.ecommerce.entity.OrderItem;

public interface OrderRepository extends JpaRepository<Order, Long> {

	Page<Order> findByUserId(Long userId, Pageable pageable);
	Page<Order> findDistinctByItems_Product_Seller_Id(Long sellerId, Pageable pageable);
	@Query("""
		    SELECT oi FROM OrderItem oi
		    WHERE oi.id = :itemId
		    AND oi.order.id = :orderId
		    AND oi.product.seller.id = :sellerId
		""")
		Optional<OrderItem> findSellerOrderItem(
		        @Param("itemId") Long itemId,
		        @Param("orderId") Long orderId,
		        @Param("sellerId") Long sellerId);
}
