package edu.jsp.ecommerce.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Setter
@Getter
@Table(name="category")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Category extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false,unique = true)
	private String name;
	
	private String description;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="parent_id")
	private Category parent;
	
	@Builder.Default
	@OneToMany(mappedBy = "parent")
	private List<Category> children = new ArrayList<>();
	
	@Column(name="image_url")
	private String imageUrl;
	@Builder.Default
	private boolean active=true;

}
