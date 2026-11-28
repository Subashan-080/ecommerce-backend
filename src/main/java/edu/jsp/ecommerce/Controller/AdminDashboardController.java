
package edu.jsp.ecommerce.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.ecommerce.Response.ApiResponse;
import edu.jsp.ecommerce.repository.UserRepository;
import edu.jsp.ecommerce.repository.SellerRepository;
import edu.jsp.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final SellerRepository sellerRepository;
    private final ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Long>>> getDashboardStats() {

        Map<String, Long> stats = Map.of(
            "totalUsers", userRepository.count(),
            "totalSellers", sellerRepository.count(),
            "pendingSellers", sellerRepository.countByApprovedFalse(),
            "totalProducts", productRepository.count()
        );

        return ResponseEntity.ok(
            ApiResponse.sucess(stats, "Dashboard statistics fetched successfully")
        );
    }
}
