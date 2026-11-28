package edu.jsp.ecommerce.SeriviecImp;

import java.math.BigDecimal;
import java.util.List;


import org.springframework.stereotype.Service;


import edu.jsp.ecommerce.execption.BadRequestException;
import edu.jsp.ecommerce.Response.CartResponse;
import edu.jsp.ecommerce.Serviece.CartService;
import edu.jsp.ecommerce.entity.Cart;
import edu.jsp.ecommerce.entity.CartItem;
import edu.jsp.ecommerce.entity.Product;
import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.execption.ResoruceNotFoundException;
import edu.jsp.ecommerce.repository.CartRepository;
import edu.jsp.ecommerce.repository.ProductRepository;
import edu.jsp.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;
@Service
@RequiredArgsConstructor
@Transactional
public class CartSeriviecImp implements CartService {
	private final CartRepository cartRepository;
	private final UserRepository userRepository;
	private final ProductRepository productRepository;

	@Override
	public CartResponse getCart(String email) {
		Cart cart=getOrCreate(email);
		return mapToResponse(cart);
	}

	@Override
	public CartResponse addToCart(String email, Long productId, int quantity) {
		Cart cart=getOrCreate(email);
		Product product=productRepository.findById(productId)
				.orElseThrow(() -> new ResoruceNotFoundException(productId+"not found"));
		
		if(!product.isActive()) {
			throw new BadRequestException("product not avaliable");
		}
		if (quantity <= 0) {
		    throw new BadRequestException("Quantity must be greater than zero");
		}

		CartItem existingItem = cart.getItems().stream()
		        .filter(c -> c.getProduct().getId().equals(productId))
		        .findFirst()
		        .orElse(null);

		int totalQuantity = quantity;

		if (existingItem != null) {
		    totalQuantity += existingItem.getQuantity();
		}

		if (product.getInventory() == null ||
		        product.getInventory().getQuantity() < totalQuantity) {
		    throw new BadRequestException("Requested quantity not available!!");
		}

		if (existingItem != null) {
		    existingItem.setQuantity(totalQuantity);
		} else {
		    cart.getItems().add(
		            CartItem.builder()
		                    .cart(cart)
		                    .product(product)
		                    .quantity(quantity)
		                    .build()
		    );
		}
		
		
		return mapToResponse(cartRepository.save(cart));
	}

	@Override
	public CartResponse updateCartItem(String email, Long cartItemId, int quantity) {
		Cart cart=getOrCreate(email);
		CartItem cartItem=cart.getItems()
				.stream()
				.filter(c->c.getId().equals(cartItemId))
				.findFirst()
				.orElseThrow(()-> new ResoruceNotFoundException("CartItem not found"));
		
		if (quantity <= 0) {
		    throw new BadRequestException(
		            "Quantity must be greater than zero");
		}

		Product product = cartItem.getProduct();

		if (product.getInventory() == null ||
		        product.getInventory().getQuantity() < quantity) {
		    throw new BadRequestException(
		            "Requested quantity not available!!");
		}

		cartItem.setQuantity(quantity);
		
		return mapToResponse(cartRepository.save(cart));
	}

	@Override
	public CartResponse removeFromCart(String email, Long cartItemId) {

	    Cart cart = getOrCreate(email);

	    CartItem cartItem = cart.getItems()
	            .stream()
	            .filter(c -> c.getId().equals(cartItemId))
	            .findFirst()
	            .orElseThrow(() ->
	                    new ResoruceNotFoundException("CartItem not found"));

	    cart.getItems().remove(cartItem);

	    return mapToResponse(cartRepository.save(cart));
	}

	@Override
	public void clearCart(String email) {
		Cart cart=getOrCreate(email);
		cart.getItems().clear();
		cartRepository.save(cart);
		
	}
	
	private Cart getOrCreate(String email) {
	    User user = userRepository.findByEmail(email)
	            .orElseThrow(() -> new ResoruceNotFoundException(email + " not found!!"));

	    return cartRepository.findByUserId(user.getId())
	            .orElseGet(() -> cartRepository.save(
	                    Cart.builder().user(user).build()
	            ));
	}
	
	public CartResponse mapToResponse(Cart cart) {
		List<CartResponse.CartItemResponse>items=cart.getItems().stream().map(item ->{
			BigDecimal totalpricr=item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
			
			return CartResponse.CartItemResponse.builder().cartItemId(item.getId()).productId(item.getProduct().getId())
					.productName(item.getProduct().getName()).thumbnailUrl(item.getProduct().getThumbnailUrl())
					.unitPrice(item.getProduct().getPrice()).quantity(item.getQuantity()).subTotal(totalpricr).build();
		}).toList();
		
		BigDecimal totalAmount=BigDecimal.valueOf(0.0);
		int totalQuantity=0;
		for(CartResponse.CartItemResponse it:items) {
			totalAmount=totalAmount.add(it.getSubTotal());
			totalQuantity=totalQuantity+it.getQuantity();
		}
		
		return CartResponse.builder().cartId(cart.getId()).items(items).totalAmount(totalAmount).totalItems(totalQuantity).build();
		
	}

}
