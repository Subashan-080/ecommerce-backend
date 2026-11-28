package edu.jsp.ecommerce.Request;

import edu.jsp.ecommerce.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank
    private String fullName;
    @NotBlank
    private String phone;
    @NotBlank
    private String addressLine1;
    private String addressLine2;
    @NotBlank
    private String city;
    @NotBlank
    private String state;
    @NotBlank
    private String postalCode;
    private String country = "India";
    private AddressType type = AddressType.HOME;
    private boolean defaultAddress;

}
