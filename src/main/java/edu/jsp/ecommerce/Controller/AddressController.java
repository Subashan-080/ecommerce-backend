package edu.jsp.ecommerce.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.ecommerce.Request.AddressRequest;
import edu.jsp.ecommerce.Response.AddressResponse;
import edu.jsp.ecommerce.Response.ApiResponse;
import edu.jsp.ecommerce.Serviece.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getMyAddresses() {

        return ResponseEntity.ok(
                ApiResponse.sucess(
                        addressService.getMyAddresses(),
                        "Addresses fetched"));
    }
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddress(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.sucess(
                        addressService.getAddressById(id),
                        "Address fetched"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @Valid @RequestBody AddressRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.sucess(
                        addressService.addAddress(request),
                        "Address added"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest request) {

        return ResponseEntity.ok(
                ApiResponse.sucess(
                        addressService.updateAddress(id, request),
                        "Address updated"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @PathVariable Long id) {

        addressService.deleteAddress(id);

        return ResponseEntity.ok(
                ApiResponse.sucess(null, "Address deleted"));
    }
    
    @PatchMapping("/{id}/default")
    public ResponseEntity<ApiResponse<AddressResponse>> setDefaultAddress(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.sucess(
                        addressService.setDefaultAddress(id),
                        "Default address updated"));
    }

}
