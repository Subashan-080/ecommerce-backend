package edu.jsp.ecommerce.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
	private Long id;
	private String name;
	private String description;
	private BigDecimal price;
	private BigDecimal mrp;
	private double averageRatings;
	private int totalReviews;
	private Long categoryId;
	private String categoryName;
	private Long brandId;
	private String brandName;
	private String sellerName;
	private String thumbnailUrl;
	private boolean active;
	private int stockQuantity;
	private LocalDateTime createAt;
	private List<ProductImageResponse> images;
	
	
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public  static class ProductImageResponse {
		private Long id;
		private String imageUrl;
		private boolean primaryImage;
		private int displayOrder;
		

	}


}
