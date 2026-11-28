package edu.jsp.ecommerce.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.ManyToAny;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="wishlists")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WishList extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id",nullable = false,unique = true)
	private User user;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name="wishlist_items",joinColumns = @JoinColumn(name="wishlist_id"),inverseJoinColumns = @JoinColumn(name="product_id"))
	@Builder.Default  
	private Set<Product>  products=new HashSet<>();

}
