package edu.jsp.ecommerce.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import edu.jsp.ecommerce.enums.OrderStatus;
import edu.jsp.ecommerce.enums.PaymentMethod;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name="orders")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order  extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id",nullable = false)
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private OrderStatus status=OrderStatus.PENDING;
	
	@Column(name="total_amount",nullable = false,precision = 12,scale = 2)
	private BigDecimal totalAmount;
	
	@Column(name="tracking_number",unique = true)
	private String trackingNumber;
	
	
	@Enumerated(EnumType.STRING)
	@Column(name="payment_method")
	private  PaymentMethod paymentMethod;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="shipping_address_id")
	private Address shippingAddress;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
	        orphanRemoval = true, fetch = FetchType.LAZY)
	private List<OrderItem> items = new ArrayList<>();
	
	@Column(name="cancellation_reson")
	private String cancellationReason;

}
