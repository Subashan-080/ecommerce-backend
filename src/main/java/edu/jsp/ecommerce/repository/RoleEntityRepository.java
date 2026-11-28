package edu.jsp.ecommerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.jsp.ecommerce.entity.RoleEntity;
import edu.jsp.ecommerce.enums.Role;

public interface RoleEntityRepository extends JpaRepository<RoleEntity, Long> {
Optional<RoleEntity>findByName(Role name);

}
