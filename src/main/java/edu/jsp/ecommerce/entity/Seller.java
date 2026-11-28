package edu.jsp.ecommerce.entity;

import java.math.BigDecimal;

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
@Getter
@Setter
@Table(name="sellers")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seller extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	private Long id;
	
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="user_id",nullable = false ,unique = true)
	private User user;
	
	@Column(name="business_name",nullable = false)
	private String businessName;
	
	@Column(name="gst_number",unique = true)
	private String gstNumber;
	
	@Column(name="bussiness_address")
	private String businessAddress;
	
	@Column(name="total_earnings",precision=15,scale = 2)
	@Builder.Default
	private BigDecimal totalEarnings=BigDecimal.ZERO;
	
	@Builder.Default
	private boolean approved =false;
	
	
	private String description;
	

}
