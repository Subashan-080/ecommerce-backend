package edu.jsp.ecommerce.SeriviecImp;

import java.util.List;


import org.springframework.stereotype.Service;
import edu.jsp.ecommerce.Request.AddressRequest;
import edu.jsp.ecommerce.Response.AddressResponse;
import edu.jsp.ecommerce.Serviece.AddressService;
import edu.jsp.ecommerce.entity.Address;
import edu.jsp.ecommerce.entity.User;
import org.springframework.transaction.annotation.Transactional;
import edu.jsp.ecommerce.execption.ResoruceNotFoundException;
import edu.jsp.ecommerce.repository.AddressRepository;
import edu.jsp.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImp implements AddressService {
	private final AddressRepository addressRepository;
	private final UserRepository userRepository;
	
	@Override
	public List<AddressResponse> getMyAddresses() {
	    User user = findUser(getCurrentEmail());

	    return addressRepository.findByUserId(user.getId())
	            .stream()
	            .map(this::mapToResponse)
	            .toList();
	}

	@Override
	public AddressResponse getAddressById(Long id) {
	    return mapToResponse(findOwnAddress(id));
	}

	@Override
	public AddressResponse addAddress(AddressRequest request) {
	    User user = findUser(getCurrentEmail());

	    Address address = buildFromRequest(request);
	    address.setUser(user);

	    List<Address> existingAddress =
	            addressRepository.findByUserId(user.getId());

	    if (existingAddress.isEmpty()) {
	        address.setDefaultAddress(true);
	    } else if (request.isDefaultAddress()) {
	        existingAddress.forEach(a -> a.setDefaultAddress(false));
	        addressRepository.saveAll(existingAddress);
	    }

	    return mapToResponse(addressRepository.save(address));
	}

	@Override
	public AddressResponse updateAddress(Long id, AddressRequest request) {
	    Address address = findOwnAddress(id);
	    User user = address.getUser();

	    address.setFullName(request.getFullName());
	    address.setPhone(request.getPhone());
	    address.setAddressLine1(request.getAddressLine1());
	    address.setAddressLine2(request.getAddressLine2());
	    address.setCity(request.getCity());
	    address.setState(request.getState());
	    address.setPostalCode(request.getPostalCode());
	    address.setCountry(request.getCountry() != null
	            ? request.getCountry() : "India");
	    address.setType(request.getType());

	    if (request.isDefaultAddress() && !address.isDefaultAddress()) {
	        List<Address> existingAddress =
	                addressRepository.findByUserId(user.getId());

	        existingAddress.forEach(a -> a.setDefaultAddress(false));
	        addressRepository.saveAll(existingAddress);

	        address.setDefaultAddress(true);
	    }

	    return mapToResponse(addressRepository.save(address));
	}
 
	@Override
	public void deleteAddress(Long id) {
	    Address address = findOwnAddress(id);
	    User user = address.getUser();

	    boolean isDefault = address.isDefaultAddress();

	    addressRepository.delete(address);

	    if (isDefault) {
	        List<Address> existingAddress =
	                addressRepository.findByUserId(user.getId());

	        if (!existingAddress.isEmpty()) {
	            Address defaultAddress = existingAddress.get(0);
	            defaultAddress.setDefaultAddress(true);
	            addressRepository.save(defaultAddress);
	        }
	    }
	}

	@Override
	public AddressResponse setDefaultAddress(Long id) {
	    Address address = findOwnAddress(id);
	    User user = address.getUser();

	    List<Address> existingAddress =
	            addressRepository.findByUserId(user.getId());

	    existingAddress.forEach(
	            a -> a.setDefaultAddress(a.getId().equals(id))
	    );

	    addressRepository.saveAll(existingAddress);

	    return mapToResponse(address);
	}
	
	private User findUser(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(()->new ResoruceNotFoundException(email+ "not found!!"));
	}
	
	private Address findOwnAddress(Long id) {
	    User user = findUser(getCurrentEmail());

	    Address address = addressRepository.findById(id)
	            .orElseThrow(() ->
	                    new ResoruceNotFoundException(id + " not found!!"));

	    if (!Objects.equals(address.getUser().getId(), user.getId())) {
	        throw new org.springframework.security.access.AccessDeniedException(
	                "You are not allowed to access this address");
	    }

	    return address;
	}
	
	
	
	private Address buildFromRequest(AddressRequest request) {
		return Address.builder()
				.fullName(request.getFullName())
				.phone(request.getPhone())
				.addressLine1(request.getAddressLine1())
				.addressLine2(request.getAddressLine2())
				.city(request.getCity())
				.state(request.getState())
				.postalCode(request.getPostalCode())
				.country(request.getCountry() !=null? request.getCountry():"India")
				.type(request.getType())
				.defaultAddress(request.isDefaultAddress()).build();
	}
	 private AddressResponse mapToResponse(Address a) {
	        return AddressResponse.builder()
	                .id(a.getId())
	                .fullName(a.getFullName())
	                .phone(a.getPhone())
	                .addressLine1(a.getAddressLine1())
	                .addressLine2(a.getAddressLine2())
	                .city(a.getCity())
	                .state(a.getState())
	                .postalCode(a.getPostalCode())
	                .country(a.getCountry())
	                .type(a.getType())
	                .defaultAddress(a.isDefaultAddress())
	                .build();
	    }
	 
	 private String getCurrentEmail() {
		    return SecurityContextHolder.getContext()
		            .getAuthentication().getName();
		}

}
