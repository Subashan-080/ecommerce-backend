
package edu.jsp.ecommerce.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import edu.jsp.ecommerce.Response.ApiResponse;
import edu.jsp.ecommerce.entity.Seller;
import edu.jsp.ecommerce.repository.SellerRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@RestController
@RequestMapping("/api/admin/sellers")
@RequiredArgsConstructor
public class AdminController {

    private final SellerRepository sellerRepository;

    @PatchMapping("/{sellerId}/approve")
    public ResponseEntity<ApiResponse<String>> approveSeller(
            @PathVariable Long sellerId) {

        Seller seller = sellerRepository.findByUserId(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));

        seller.setApproved(true);
        sellerRepository.save(seller);

        return ResponseEntity.ok(
                ApiResponse.sucess("Seller approved successfully",
                        "Seller approval completed"));
    }
    
    
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<Page<Seller>>> getPendingSellers(
            Pageable pageable) {

        Page<Seller> sellers = sellerRepository.findByApprovedFalse(pageable);

        return ResponseEntity.ok(
                ApiResponse.sucess(sellers, "Pending sellers fetched successfully")
        );
    }
}
