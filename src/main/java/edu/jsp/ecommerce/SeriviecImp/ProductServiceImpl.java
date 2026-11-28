package edu.jsp.ecommerce.SeriviecImp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import edu.jsp.ecommerce.Request.ProductRequest;
import edu.jsp.ecommerce.Response.ProductResponse;
import edu.jsp.ecommerce.Response.ProductResponse.ProductImageResponse;
import edu.jsp.ecommerce.Serviece.ProductService;
import edu.jsp.ecommerce.entity.Brand;
import edu.jsp.ecommerce.entity.Category;
import edu.jsp.ecommerce.entity.Inventory;
import edu.jsp.ecommerce.entity.Product;
import edu.jsp.ecommerce.entity.ProductImage;
import edu.jsp.ecommerce.entity.Seller;
import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.enums.Role;
import edu.jsp.ecommerce.execption.BadRequestException;
import edu.jsp.ecommerce.execption.ResoruceNotFoundException;
import edu.jsp.ecommerce.repository.BrandRepository;
import edu.jsp.ecommerce.repository.CategoryRepository;
import edu.jsp.ecommerce.repository.InventoryRepository;
import edu.jsp.ecommerce.repository.ProductRepository;
import edu.jsp.ecommerce.repository.SellerRepository;
import edu.jsp.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

	private final UserRepository userRepository;
	private final SellerRepository sellerRepository;
	private final CategoryRepository categoryRepository;
	private final BrandRepository brandRepository;
	private final ProductRepository productRepository;
	private final InventoryRepository inventoryRepository;

	@Override
	public ProductResponse createProduct(ProductRequest request, String sellerEmail, Long sellerId) {

		Seller seller;
		if (sellerEmail != null) {
			// SELLER creates product
			seller = getApprovedSeller(sellerEmail);

		} else {
			// ADMIN creates product for an existing seller
			if (sellerId == null) {
				throw new BadRequestException("Seller ID is required");
			}

			seller = sellerRepository.findById(sellerId)
					.orElseThrow(() -> new ResoruceNotFoundException("Seller not found"));

			if (!seller.isApproved()) {
				throw new BadRequestException("Seller account is not approved");
			}
		}

		Product product = buildProduct(request, seller);

		if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
			saveImages(product, request.getImageUrls());
		}
		productRepository.save(product);

		Inventory inventory = Inventory.builder().product(product).quantity(request.getStockQuantity()).build();

		inventoryRepository.save(inventory);
		product.setInventory(inventory);
		return mapToResponse(product);
	}

	public Seller getSeller(String sellerEmail) {

		User user = userRepository.findByEmail(sellerEmail)
				.orElseThrow(() -> new ResoruceNotFoundException(sellerEmail + " email not found!!!"));

		return sellerRepository.findByUserId(user.getId())
				.orElseThrow(() -> new ResoruceNotFoundException("seller not found!!!"));
	}

	private Seller getApprovedSeller(String sellerEmail) {
		Seller seller = getSeller(sellerEmail);

		if (!seller.isApproved()) {
			throw new BadRequestException("Your seller account is not approved yet");
		}

		return seller;
	}

	private Product buildProduct(ProductRequest request, Seller seller) {

		Category category = request.getCategoryId() != null
		        ? getActiveCategory(request.getCategoryId())
		        : null;
		Brand brand = request.getBrandId() != null ? brandRepository.findById(request.getBrandId())
				.orElseThrow(() -> new ResoruceNotFoundException("Brand not found")) : null;

		return Product.builder().name(request.getName()).description(request.getDescription())
				.price(request.getPricre()).mrp(request.getMrp() != null ? request.getMrp() : request.getPricre())
				.category(category).brand(brand).seller(seller).thumbnailUrl(request.getThumbnailUrl()).active(true)
				.build();
	}

	private void saveImages(Product product, List<String> images) {

		for (int i = 0; i < images.size(); i++) {

			String url = images.get(i);

			if (url != null && !url.isBlank()) {

				ProductImage image = ProductImage.builder().product(product).imageurl(url).primaryImage(i == 0)
						.displayOrder(i).build();

				product.getImages().add(image);
			}
		}
	}

	public ProductResponse mapToResponse(Product p) {

		int stock = p.getInventory() != null ? p.getInventory().getQuantity() : 0;

		List<ProductImageResponse> images = new ArrayList<>();

		if (p.getImages() != null) {

			images = p.getImages().stream().sorted((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()))
					.map(img -> ProductImageResponse.builder().imageUrl(img.getImageurl()).id(img.getId())
							.primaryImage(img.isPrimaryImage()).displayOrder(img.getDisplayOrder()).build())
					.collect(Collectors.toList());
		}

		return ProductResponse.builder().id(p.getId()).name(p.getName()).description(p.getDescription())
				.price(p.getPrice()).mrp(p.getMrp()).averageRatings(p.getAverageRating())
				.totalReviews(p.getTotalReviews()).categoryId(p.getCategory() != null ? p.getCategory().getId() : null)
				.categoryName(p.getCategory() != null ? p.getCategory().getName() : null)
				.brandId(p.getBrand() != null ? p.getBrand().getId() : null)
				.brandName(p.getBrand() != null ? p.getBrand().getName() : null)
				.sellerName(p.getSeller() != null ? p.getSeller().getBusinessName() : null)
				.thumbnailUrl(p.getThumbnailUrl()).active(p.isActive()).stockQuantity(stock).createAt(p.getCreateAt())
				.images(images).build();
	}

	@Override
	public ProductResponse updateProduct(Long id, ProductRequest request, String sellerEmail) {
		User user = findUser(sellerEmail);
		Product product = findProduct(id);
		if (user.getRoles().stream().noneMatch(r -> r.getName() == Role.ROLE_ADMIN)) {
			Seller seller = getApprovedSeller(sellerEmail);

			if (!product.getSeller().getId().equals(seller.getId())) {
				throw new BadRequestException("you can update only your product");
			}
		}
		applyUpdate(product, request);
		if (request.getImageUrls() != null) {
			product.getImages().clear();
			saveImages(product, request.getImageUrls());
			productRepository.save(product);
		}
		if (product.getInventory() != null) {
			product.getInventory().setQuantity(request.getStockQuantity());
			productRepository.save(product);
		} else {
			Inventory inventory = Inventory.builder().product(product).quantity(request.getStockQuantity()).build();
			inventoryRepository.save(inventory);
			product.setInventory(inventory);
		}
		return mapToResponse(product);
	}

	@Override
	public void deleteProduct(Long id, String sellerEmail) {
		User user = findUser(sellerEmail);
		Product product = findProduct(id);
		if (user.getRoles().stream().noneMatch(r -> r.getName() == Role.ROLE_ADMIN)) {
			Seller seller = getApprovedSeller(sellerEmail);
			if (!product.getSeller().getId().equals(seller.getId())) {
				throw new BadRequestException("You can delete only your product");
			}
		}

		product.setActive(false);
		productRepository.save(product);

	}

	@Override
	public ProductResponse getProductById(Long id) {
		Product product = productRepository.findByIdAndActiveTrue(id)
				.orElseThrow(() -> new ResoruceNotFoundException("Product not found"));

		return mapToResponse(product);
	}

	@Override
	public Page<ProductResponse> getAllProducts(Pageable pageable) {

		return productRepository.findByActiveTrue(pageable).map(x -> mapToResponse(x));
	}

	@Override
	public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
		return productRepository.findByActiveTrueAndCategoryId(categoryId, pageable).map(this::mapToResponse);
	}

	@Override
	public Page<ProductResponse> getProductsByBrand(Long brandId, Pageable pageable) {

		return productRepository.findByActiveTrueAndBrandId(brandId, pageable).map(this::mapToResponse);
	}

	@Override
	public Page<ProductResponse> getProductsByCategoryAndBrand(Long categoryId, Long brandId, Pageable pageable) {
		return productRepository.findByActiveTrueAndCategoryIdAndBrandId(categoryId, brandId, pageable)
				.map(this::mapToResponse);
	}

	@Override
	public Page<ProductResponse> searchProducts(String keyword, Pageable pageable) {

		return productRepository.searchByKeyword(keyword, pageable).map(this::mapToResponse);
	}

	@Override
	public Page<ProductResponse> getSellerProducts(String sellerEmail, Pageable pageable) {
		Seller seller = getApprovedSeller(sellerEmail);
		return productRepository.findByActiveTrueAndSellerId(seller.getId(), pageable).map(this::mapToResponse);
	}

	private Category getActiveCategory(Long categoryId) {

		Category category = categoryRepository.findById(categoryId)
				.orElseThrow(() -> new ResoruceNotFoundException("Category not found"));

		if (!category.isActive()) {
			throw new BadRequestException("Cannot assign an inactive category");
		}

		return category;
	}

	@Override
	public Page<ProductResponse> filterProducts(Long categoryId, Long brandId, BigDecimal minPrice, BigDecimal maxPrice,
			String keyword, Pageable pageable) {
		return productRepository.filterProducts(categoryId, brandId, minPrice, maxPrice, keyword, pageable)
				.map(this::mapToResponse);
	}

	private Product findProduct(Long id) {
		return productRepository.findById(id).orElseThrow(() -> new ResoruceNotFoundException("product not found"));
	}

	private User findUser(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResoruceNotFoundException(email + "user not found"));
	}

	private void applyUpdate(Product product, ProductRequest request) {
		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setPrice(request.getPricre());
		product.setMrp(request.getMrp() != null ? request.getMrp() : request.getPricre());
		product.setThumbnailUrl(request.getThumbnailUrl());
		if (request.getCategoryId() != null) {
			Category category = getActiveCategory(request.getCategoryId());
			product.setCategory(category);
		}

		if (request.getBrandId() != null) {
			Brand brand = brandRepository.findById(request.getBrandId())
					.orElseThrow(() -> new ResoruceNotFoundException("Brand not found"));
			product.setBrand(brand);
		}

	}

}
