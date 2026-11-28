package edu.jsp.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name="inventory")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="product_id",nullable = false,unique = true)
	private Product product;
	
	@Column(nullable = false)
	@Builder.Default
	private int quantity=0;
	
	
	@Column(name="low_stock_threshold")
	@Builder.Default
	private int lowStockThreshold=10;

}
 