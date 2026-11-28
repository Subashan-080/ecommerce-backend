package edu.jsp.ecommerce.entity;


import org.apache.el.parser.AstFalse;

import edu.jsp.ecommerce.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder; 
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="Roles")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false,unique = true,length = 20)
	private Role name;
	
	
	public  RoleEntity(Role name) {
		this.name=name;
	}

}
