package edu.jsp.ecommerce.entity;

import edu.jsp.ecommerce.enums.AddressType;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address extends BaseEntity {
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @Column(name = "full_name", nullable = false)
	    private String fullName;

	    private String phone;

	    @Column(name = "address_line1", nullable = false)
	    private String addressLine1;

	    @Column(name = "address_line2")
	    private String addressLine2;

	    @Column(nullable = false)
	    private String city;

	    @Column(nullable = false)
	    private String state;

	    @Column(name = "postal_code", nullable = false)
	    private String postalCode;

	    @Column(nullable = false)
	    @Builder.Default
	    private String country = "India";

	    @Enumerated(EnumType.STRING)
	    @Builder.Default
	    private AddressType type = AddressType.HOME;

	    @Column(name = "default_address")
	    @Builder.Default
	    private boolean defaultAddress = false;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "user_id", nullable = false)
	    private User user;

}
