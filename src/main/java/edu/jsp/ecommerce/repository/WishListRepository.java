package edu.jsp.ecommerce.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import edu.jsp.ecommerce.entity.WishList;

public interface WishListRepository  extends JpaRepository<WishList, Long>{
	Optional<WishList> findByUserId(Long id);

}
