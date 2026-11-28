package edu.jsp.ecommerce.SeriviecImp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import org.springframework.security.crypto.password.PasswordEncoder;

import edu.jsp.ecommerce.Request.RegisterRequest;
import edu.jsp.ecommerce.Response.UserResponse;
import edu.jsp.ecommerce.Serviece.UserService;
import edu.jsp.ecommerce.entity.RoleEntity;
import edu.jsp.ecommerce.entity.Seller;
import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.enums.Role;
import edu.jsp.ecommerce.repository.RoleEntityRepository;
import edu.jsp.ecommerce.repository.SellerRepository;
import edu.jsp.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {
	private final UserRepository userRepository;
	private final RoleEntityRepository roleEntityRepository;
	private final SellerRepository sellerRepository;

	private final PasswordEncoder passwordEncoder;

	public UserResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException("Email already registered");
		}
		if (request.getRoles() != null && request.getRoles().size() > 1) {
		    throw new RuntimeException("Select only one role");
		}
		HashSet<RoleEntity> roles = new HashSet<RoleEntity>();

		if (request.getRoles() != null && !request.getRoles().isEmpty()) {

			boolean invalidRole = request.getRoles().stream()
					.anyMatch(r -> r != Role.ROLE_CUSTOMER && r != Role.ROLE_SELLER);

			if (invalidRole) {
				throw new RuntimeException("Invalid role requested");
			}

			request.getRoles().forEach(r -> {
				RoleEntity role = roleEntityRepository.findByName(r)
						.orElseThrow(() -> new RuntimeException("Role not found: " + r));

				roles.add(role);
			});
		}

		if (roles.isEmpty()) {
			RoleEntity customerRole = roleEntityRepository.findByName(Role.ROLE_CUSTOMER)
					.orElseThrow(() -> new RuntimeException("Customer role not found"));

			roles.add(customerRole);
		}

		User user = User.builder().email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
				.firstName(request.getFirstName()).lastName(request.getLastName()).phno(request.getPhno()).roles(roles)
				.build();
		user = userRepository.save(user);

		Boolean isSeller = roles.stream().anyMatch(r -> r.getName() == Role.ROLE_SELLER);
		if (isSeller) {
			Seller seller = Seller.builder().user(user).businessName(user.getFirstName() + " store!!").approved(false)
					.build();
			sellerRepository.save(seller);

		}
		return maptoResponse(user);

	}

	@Override
	public UserResponse getProfile(String email) {
		User u = userRepository.findByEmail(email).orElseThrow();
		return maptoResponse(u);
	}

	@Override
	public UserResponse getUserById(long id) {
		return maptoResponse(findById(id));
	}

	@Override
	public List<UserResponse> getAllUser() {
		return userRepository.findAll().stream().map(u -> maptoResponse(u)).toList();
	}

	@Override
	public void addRole(long userid, Role role) {
		User u = findById(userid);
		RoleEntity re = roleEntityRepository.findByName(role).orElseThrow();
		u.getRoles().add(re);
		userRepository.save(u);

	}

	@Override
	public void removeRole(long userid, Role role) {
		User u = findById(userid);
		if (u.getRoles().size() <= 1) {
			throw new RuntimeException("User should have atleast one role");
		}
		RoleEntity re = roleEntityRepository.findByName(role).orElseThrow();
		u.getRoles().removeIf(r -> r.getName() == re.getName());
		userRepository.save(u);

	}

	@Override
	public void toggleUserEnabled(long userid) {
		User u = userRepository.findById(userid).orElseThrow();
		u.setAccountEnabled(!u.isAccountEnabled());
		userRepository.save(u);

	}

	private User findById(long id) {
		return userRepository.findById(id).orElseThrow();

	}

	public UserResponse maptoResponse(User u) {
		HashSet<Role> roles = new HashSet<Role>();
		Set<RoleEntity> re = u.getRoles();

		for (RoleEntity r : re) {
			roles.add(r.getName());
		}

		return UserResponse.builder().id(u.getId()).email(u.getEmail()).firstName(u.getFirstName())
				.lastName(u.getLastName()).phno(u.getPhno()).profilePictureUrl(u.getProfilePictureurl())
				.emailVerified(u.isEmailverified()).accountEnabled(u.isAccountEnabled()).roles(roles)
				.createAt(u.getCreateAt()).build();
	}

}
