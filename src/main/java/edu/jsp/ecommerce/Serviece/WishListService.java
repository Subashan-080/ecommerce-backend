package edu.jsp.ecommerce.Serviece;

import edu.jsp.ecommerce.Response.WishlistResponse;

public interface WishListService {

    WishlistResponse getWishlist(String email);

    WishlistResponse addToWishlist(String email, Long productId);

    WishlistResponse removeFromWishlist(String email, Long productId);

    boolean isInWishlist(String email, Long productId);

    void clearWishlist(String email);
}