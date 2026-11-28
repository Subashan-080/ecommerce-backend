package edu.jsp.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.jsp.ecommerce.entity.RoleEntity;
import edu.jsp.ecommerce.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
	Optional<User> findByEmail(String email);
	
	
	

}
