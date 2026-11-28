package edu.jsp.ecommerce.SeriviecImp;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.jsp.ecommerce.Request.OrderRequest;
import edu.jsp.ecommerce.Response.OrderResponse;
import edu.jsp.ecommerce.Response.SellerOrderResponse;
import edu.jsp.ecommerce.Serviece.CartService;
import edu.jsp.ecommerce.Serviece.OrderService;
import edu.jsp.ecommerce.entity.Address;
import edu.jsp.ecommerce.entity.Cart;
import edu.jsp.ecommerce.entity.CartItem;
import edu.jsp.ecommerce.entity.Inventory;
import edu.jsp.ecommerce.entity.Order;
import edu.jsp.ecommerce.entity.OrderItem;
import edu.jsp.ecommerce.entity.Seller;
import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.enums.OrderStatus;
import edu.jsp.ecommerce.enums.PaymentMethod;
import edu.jsp.ecommerce.execption.BadRequestException;
import edu.jsp.ecommerce.execption.ResoruceNotFoundException;
import edu.jsp.ecommerce.repository.AddressRepository;
import edu.jsp.ecommerce.repository.CartRepository;
import edu.jsp.ecommerce.repository.OrderRepository;
import edu.jsp.ecommerce.repository.SellerRepository;
import edu.jsp.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderSeriviecImp implements OrderService {

	private final UserRepository userRepository;
	private final OrderRepository orderRepository;
	private final CartRepository cartRepositroy;
	private final AddressRepository addressRepository;
	private final CartService cartService;
	private final SellerRepository sellerRepository;

	@Transactional
	@Override
	public OrderResponse placeOrder(String email, OrderRequest request) {

		if (request.getPaymentMethod() != PaymentMethod.COD) {
			throw new BadRequestException("Only Cash on Delivery is supported");
		}

		User user = findUser(email);

		Cart cart = cartRepositroy.findByUserId(user.getId())
				.orElseThrow(() -> new ResoruceNotFoundException("Cart Not found!!!"));

		if (cart.getItems().isEmpty()) {
			throw new BadRequestException("Cart is empty!!!!");
		}

		Address address;
		if (request.getShippingAddressId() != null) {

			address = addressRepository.findById(request.getShippingAddressId())
					.orElseThrow(() -> new ResoruceNotFoundException("Address Not found!!!"));

			if (!address.getUser().getId().equals(user.getId())) {
				throw new BadRequestException("Access  denied!!!!");
			}
		} else if (request.getNewAddress() != null) {

			List<Address> addresses = addressRepository.findByUserId(user.getId());

			address = Address.builder().user(user).fullName(request.getNewAddress().getFullName())
					.phone(request.getNewAddress().getPhone()).addressLine1(request.getNewAddress().getAddressLine1())
					.addressLine2(request.getNewAddress().getAddressLine2()).city(request.getNewAddress().getCity())
					.state(request.getNewAddress().getState()).postalCode(request.getNewAddress().getPostalCode())
					.country(request.getNewAddress().getCountry())
					.defaultAddress(request.getNewAddress().isDefaultAddress()).build();

			if (request.getNewAddress().isDefaultAddress()) {
				addresses.stream().forEach(a -> a.setDefaultAddress(false));
				addressRepository.saveAll(addresses);
			}

			address = addressRepository.save(address);

		} else {
			List<Address> addresses = addressRepository.findByUserId(user.getId());
			if (addresses.isEmpty()) {
				throw new BadRequestException("Please add a shipping address first");
			}
			address = addresses.stream().filter(a -> a.isDefaultAddress()).findFirst().orElse(addresses.get(0));
		}

		List<CartItem> items = cart.getItems();

		List<OrderItem> orderItems =

				items.stream().map(item -> {
					Inventory inv = item.getProduct().getInventory();
					if (inv == null || inv.getQuantity() < item.getQuantity()) {
						throw new BadRequestException("Insufficient Quantuty!!!!");
					}
					inv.setQuantity(inv.getQuantity() - item.getQuantity());

					BigDecimal totalprice = item.getProduct().getPrice()
							.multiply(BigDecimal.valueOf(item.getQuantity()));

					return OrderItem.builder().product(item.getProduct()).quantity(item.getQuantity())
							.unitPrice(item.getProduct().getPrice()).totalPrice(totalprice)
							.status(OrderStatus.CONFIRMED).build();

				}).toList();

		BigDecimal total = BigDecimal.ZERO;

		for (OrderItem it : orderItems) {
			total = total.add(it.getTotalPrice());
		}

		Order order = Order.builder().user(user).totalAmount(total).shippingAddress(address)
				.paymentMethod(request.getPaymentMethod())
				.trackingNumber("TRK - " + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
				.status(OrderStatus.CONFIRMED).items(orderItems).build();

		orderItems.forEach(o -> o.setOrder(order));
		Order saved = orderRepository.save(order);

		cartService.clearCart(email);

		return mapToResponse(saved);
	}
	
	@Transactional(readOnly = true)
	@Override
	public OrderResponse getOrderById(Long id, String email) {

		User user = findUser(email);

		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResoruceNotFoundException("Order not found!!!"));

		boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.getName().name().equals("ROLE_ADMIN"));

		if (!isAdmin && !order.getUser().getId().equals(user.getId())) {
			throw new BadRequestException("Access denied!!");
		}

		return mapToResponse(order);
	}
    
	
	@Transactional(readOnly = true)
	@Override
	public Page<OrderResponse> getUserOrders(String email, Pageable pageable) {
		User user = findUser(email);

		return orderRepository.findByUserId(user.getId(), pageable).map(this::mapToResponse);
	}
    
	@Transactional(readOnly = true)
	@Override
	public Page<OrderResponse> getAllOrders(Pageable pageable) {

		return orderRepository.findAll(pageable).map(this::mapToResponse);
	}

	@Transactional(readOnly = true)
	@Override
	public Page<SellerOrderResponse> getSellerOrders(String email, Pageable pageable) {

		// Find the logged-in user
		User user = findUser(email);

		// Find the seller profile belonging to this user
		Seller seller = sellerRepository.findByUserId(user.getId())
				.orElseThrow(() -> new ResoruceNotFoundException("Seller profile not found"));

		// Fetch orders containing this seller's products
		return orderRepository.findDistinctByItems_Product_Seller_Id(seller.getId(), pageable)
				.map(order -> mapToSellerResponse(order, seller.getId()));
	}

	@Transactional(readOnly = true)
	@Override
	public SellerOrderResponse getSellerOrderById(Long orderId, String email) {

		User user = findUser(email);

		Seller seller = sellerRepository.findByUserId(user.getId())
				.orElseThrow(() -> new ResoruceNotFoundException("Seller profile not found"));

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResoruceNotFoundException("Order not found"));

		boolean hasSellerProduct = order.getItems().stream()
				.anyMatch(item -> item.getProduct().getSeller().getId().equals(seller.getId()));

		if (!hasSellerProduct) {
			throw new ResoruceNotFoundException("Order not found for this seller");
		}

		return mapToSellerResponse(order, seller.getId());
	}

	@Override
	@Transactional
	public SellerOrderResponse updateSellerOrderItemStatus(Long orderId, Long itemId, String email, OrderStatus status,
			String trackingNumber) {

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResoruceNotFoundException("User not found"));

		Seller seller = sellerRepository.findByUserId(user.getId())
				.orElseThrow(() -> new ResoruceNotFoundException("Seller not found"));

		OrderItem item = orderRepository.findSellerOrderItem(itemId, orderId, seller.getId())
				.orElseThrow(() -> new ResoruceNotFoundException("Order item not found"));

		OrderStatus currentStatus = item.getStatus();

		boolean validTransition = (currentStatus == OrderStatus.CONFIRMED && status == OrderStatus.PROCESSING)
				|| (currentStatus == OrderStatus.PROCESSING && status == OrderStatus.SHIPPED)
				|| (currentStatus == OrderStatus.SHIPPED && status == OrderStatus.OUT_FOR_DELIVERY)
				|| (currentStatus == OrderStatus.OUT_FOR_DELIVERY && status == OrderStatus.DELIVERED);

		if (!validTransition) {
			throw new BadRequestException("Invalid order item status transition");
		}

		if (status == OrderStatus.SHIPPED && (trackingNumber == null || trackingNumber.isBlank())) {
			throw new BadRequestException("Tracking number is required when shipping an item");
		}

		item.setStatus(status);

		if (trackingNumber != null && !trackingNumber.isBlank()) {
			item.setTrackingNumber(trackingNumber);
		}

		orderRepository.save(item.getOrder());

		return mapToSellerResponse(item.getOrder(), seller.getId());
	}

	@Transactional
	@Override
	public OrderResponse updateOrderStatus(Long id, OrderStatus status) {

		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResoruceNotFoundException("Order not found!!!"));

		OrderStatus currentStatus = order.getStatus();

		boolean isValidTransition = switch (currentStatus) {
		case PENDING -> status == OrderStatus.CONFIRMED || status == OrderStatus.CANCELLED;

		case CONFIRMED -> status == OrderStatus.PROCESSING || status == OrderStatus.CANCELLED;

		case PROCESSING -> status == OrderStatus.SHIPPED || status == OrderStatus.CANCELLED;

		case SHIPPED -> status == OrderStatus.OUT_FOR_DELIVERY;

		case OUT_FOR_DELIVERY -> status == OrderStatus.DELIVERED;

		case DELIVERED -> status == OrderStatus.RETURNED;

		case CANCELLED, RETURNED, REFUNDED -> false;
		};

		if (!isValidTransition) {
			throw new BadRequestException("Invalid order status transition: " + currentStatus + " -> " + status);
		}

		if (status == OrderStatus.CANCELLED) {
			restoreInventory(order);
		}

		order.setStatus(status);

		return mapToResponse(orderRepository.save(order));
	}

	@Transactional
	@Override
	public void cancelOrder(Long id, String email, String reason) {

		User user = findUser(email);
		Order order = orderRepository.findById(id)
				.orElseThrow(() -> new ResoruceNotFoundException("Order not found: " + id));

		if (!order.getUser().getId().equals(user.getId())) {
			throw new BadRequestException("Access denied");
		}

		if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.CONFIRMED
				&& order.getStatus() != OrderStatus.PROCESSING) {

			throw new BadRequestException("Cannot cancel order in status: " + order.getStatus());
		}

		order.setStatus(OrderStatus.CANCELLED);
		order.setCancellationReason(reason);

		restoreInventory(order);

		orderRepository.save(order);

	}

	private User findUser(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResoruceNotFoundException("User not found: " + email));
	}

	public OrderResponse mapToResponse(Order order) {

		List<OrderResponse.OrderItemResponse> items = order.getItems().stream().map(item -> {
			return OrderResponse.OrderItemResponse.builder().productId(item.getProduct().getId())
					.productName(item.getProduct().getName()).thumbnailUrl(item.getProduct().getThumbnailUrl())
					.quantity(item.getQuantity()).unitPrice(item.getUnitPrice()).totalPrice(item.getTotalPrice())
					.build();
		}).toList();

		Address adrs = order.getShippingAddress();

		String shipadrs = adrs != null
				? adrs.getFullName() + " , " + adrs.getAddressLine1() + " , " + adrs.getAddressLine2() + " , "
						+ adrs.getCity() + " , " + adrs.getState() + " , " + adrs.getPostalCode() + " , "
						+ adrs.getPhone()
				: "";

		return OrderResponse.builder().id(order.getId()).status(order.getStatus()).totalAmount(order.getTotalAmount())
				.trackingNumber(order.getTrackingNumber()).paymentMethod(order.getPaymentMethod()).items(items)
				.shippingAddress(shipadrs).createdAt(order.getCreateAt()).build();

	}

	private SellerOrderResponse mapToSellerResponse(Order order, Long sellerId) {

		List<SellerOrderResponse.SellerOrderItemResponse> items = order.getItems().stream()
				.filter(item -> item.getProduct().getSeller().getId().equals(sellerId))
				.map(item -> SellerOrderResponse.SellerOrderItemResponse.builder().itemId(item.getId())
						.productId(item.getProduct().getId()).productName(item.getProduct().getName())
						.thumbnailUrl(item.getProduct().getThumbnailUrl()).quantity(item.getQuantity())
						.unitPrice(item.getUnitPrice()).totalPrice(item.getTotalPrice()).status(item.getStatus())
						.trackingNumber(item.getTrackingNumber()).build())
				.toList();

		BigDecimal subtotal = items.stream().map(SellerOrderResponse.SellerOrderItemResponse::getTotalPrice)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Address address = order.getShippingAddress();

		String shippingAddress = address != null
				? address.getFullName() + " , " + address.getAddressLine1() + " , " + address.getAddressLine2() + " , "
						+ address.getCity() + " , " + address.getState() + " , " + address.getPostalCode() + " , "
						+ address.getPhone()
				: "";

		return SellerOrderResponse.builder().orderId(order.getId()).status(order.getStatus()).sellerSubtotal(subtotal)
				.trackingNumber(order.getTrackingNumber()).paymentMethod(order.getPaymentMethod())
				.shippingAddress(shippingAddress).items(items).createdAt(order.getCreateAt()).build();
	}

	private void restoreInventory(Order order) {

		order.getItems().forEach(item -> {

			Inventory inventory = item.getProduct().getInventory();

			if (inventory != null) {
				inventory.setQuantity(inventory.getQuantity() + item.getQuantity());
			}
		});
	}

}
