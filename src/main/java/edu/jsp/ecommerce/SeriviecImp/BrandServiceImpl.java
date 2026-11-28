package edu.jsp.ecommerce.SeriviecImp;

import java.util.List;

import org.springframework.stereotype.Service;

import edu.jsp.ecommerce.Serviece.BrandService;
import edu.jsp.ecommerce.entity.Brand;
import edu.jsp.ecommerce.execption.BadRequestException;
import edu.jsp.ecommerce.execption.ResoruceNotFoundException;
import edu.jsp.ecommerce.repository.BrandRepository;
import edu.jsp.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BrandServiceImpl implements BrandService {
	
	 private final BrandRepository brandRepository;
	 private final ProductRepository productRepository;

	@Override
	public Brand createBrand(Brand brand) {
		 if (brandRepository.existsByNameIgnoreCase(brand.getName())) {
		        throw new BadRequestException("Brand already exists");
		    }

		 return brandRepository.save(brand);
	}

	@Override
	public List<Brand> getAllBrands() {
		
		return brandRepository.findAll();
	}

	@Override
	public Brand updateBrand(Long id, Brand brand) {

	    Brand existingBrand = brandRepository.findById(id)
	            .orElseThrow(() ->
	                new ResoruceNotFoundException("Brand not found"));

	    if (brandRepository.existsByNameIgnoreCaseAndIdNot(brand.getName(), id)) {
	        throw new BadRequestException("Brand already exists");
	    }

	    existingBrand.setName(brand.getName());
	    existingBrand.setLogourl(brand.getLogourl());
	    existingBrand.setActive(brand.isActive());

	    return brandRepository.save(existingBrand);
	}

	@Override
	public void deleteBrand(Long id) {

	    Brand brand = brandRepository.findById(id)
	            .orElseThrow(() ->
	                new ResoruceNotFoundException("Brand not found"));

	    if (productRepository.existsByBrandId(id)) {
	        throw new BadRequestException(
	            "Cannot delete brand because products are assigned to it");
	    }

	    brandRepository.delete(brand);
	}

}
