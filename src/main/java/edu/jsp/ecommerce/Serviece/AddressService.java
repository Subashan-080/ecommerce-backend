package edu.jsp.ecommerce.Serviece;

import java.util.List;
import edu.jsp.ecommerce.Request.AddressRequest;
import edu.jsp.ecommerce.Response.AddressResponse;

public interface AddressService {

    List<AddressResponse> getMyAddresses();

    AddressResponse getAddressById(Long id);

    AddressResponse addAddress(AddressRequest request);

    AddressResponse updateAddress(Long id, AddressRequest request);

    void deleteAddress(Long id);

    AddressResponse setDefaultAddress(Long id);
}