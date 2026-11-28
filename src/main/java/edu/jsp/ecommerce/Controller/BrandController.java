package edu.jsp.ecommerce.Controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.ecommerce.Serviece.BrandService;
import edu.jsp.ecommerce.entity.Brand;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {
	private final BrandService brandService;
	
	@PostMapping
	public ResponseEntity<Brand> createBrand(@RequestBody Brand brand) {
	    return ResponseEntity.ok(brandService.createBrand(brand));
	}
	
	@GetMapping
	public ResponseEntity<List<Brand>> getAllBrands() {
	    return ResponseEntity.ok(brandService.getAllBrands());
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Brand> updateBrand(
	        @PathVariable Long id,
	        @RequestBody Brand brand) {
	    return ResponseEntity.ok(brandService.updateBrand(id, brand));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteBrand(@PathVariable Long id) {
	    brandService.deleteBrand(id);
	    return ResponseEntity.noContent().build();
	}

}
