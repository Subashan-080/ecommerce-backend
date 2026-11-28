package edu.jsp.ecommerce.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.jsp.ecommerce.entity.Seller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface SellerRepository extends JpaRepository<Seller, Long>{
	Optional<Seller> findByUserId(Long id);
	
	Page<Seller> findByApprovedFalse(Pageable pageable);
	
	long countByApprovedFalse();
	

	

}
