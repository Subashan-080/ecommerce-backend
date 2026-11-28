package edu.jsp.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.jsp.ecommerce.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long>{

}
