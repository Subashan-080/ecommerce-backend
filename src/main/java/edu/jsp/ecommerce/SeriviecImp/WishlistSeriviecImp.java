package edu.jsp.ecommerce.SeriviecImp;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.jsp.ecommerce.Response.ProductResponse;
import edu.jsp.ecommerce.Response.WishlistResponse;
import edu.jsp.ecommerce.Serviece.ProductService;
import edu.jsp.ecommerce.Serviece.WishListService;
import edu.jsp.ecommerce.entity.Product;
import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.entity.WishList;
import edu.jsp.ecommerce.execption.ResoruceNotFoundException;
import edu.jsp.ecommerce.repository.ProductRepository;
import edu.jsp.ecommerce.repository.UserRepository;
import edu.jsp.ecommerce.repository.WishListRepository;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Transactional

public class WishlistSeriviecImp  implements WishListService{
	private final UserRepository userRepository;
	private final WishListRepository wishListRepository;
	private final ProductRepository productRepository;
	private final ProductServiceImpl productServiceImpl;
	

	@Override
	public WishlistResponse getWishlist(String email) {
		WishList wishList=getOrCreate(email);
		return mapToResponse(wishList);
	}

	@Override
	public WishlistResponse addToWishlist(String email, Long productId) {

	    WishList wishList = getOrCreate(email);

	    Product product = productRepository.findById(productId)
	            .orElseThrow(() -> new ResoruceNotFoundException("Product not found"));

	    boolean alreadyExists = wishList.getProducts().stream()
	            .anyMatch(p -> p.getId().equals(productId));

	    if (!alreadyExists) {
	        wishList.getProducts().add(product);
	    }

	    return mapToResponse(wishListRepository.save(wishList));
	}

	@Override
	public WishlistResponse removeFromWishlist(String email, Long ProductId) {
		WishList wishList=getOrCreate(email);
		wishList.getProducts().removeIf(p->p.getId().equals(ProductId));
		return mapToResponse(wishListRepository.save(wishList));
	}

	@Override
	public boolean isInWishlist(String email, Long productTd) {
		WishList wishList =getOrCreate(email);
		return wishList.getProducts().stream().anyMatch(p-> p.getId().equals(productTd));
	}

	@Override
	public void clearWishlist(String email) {
		WishList wishList=getOrCreate(email);
		wishList.getProducts().clear();
		wishListRepository.save(wishList);
		
		
	}
	
	public WishList getOrCreate(String email) {

	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new ResoruceNotFoundException(email + " not found!!!"));

	    return wishListRepository.findByUserId(user.getId())
	            .orElseGet(() -> wishListRepository.save(
	                    WishList.builder().user(user).build()
	            ));
	}
	
	
	private WishlistResponse mapToResponse(WishList wl) {
		List<ProductResponse> items=wl.getProducts().stream().map(productServiceImpl::mapToResponse).toList();
		return WishlistResponse.builder().wishlistId(wl.getId()).products(items).totalItems(items.size()).build();
	}

	
	

}
