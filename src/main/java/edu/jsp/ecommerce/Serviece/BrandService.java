package edu.jsp.ecommerce.Serviece;

import java.util.List;
import edu.jsp.ecommerce.entity.Brand;

public interface BrandService {

    Brand createBrand(Brand brand);

    List<Brand> getAllBrands();

    Brand updateBrand(Long id, Brand brand);

    void deleteBrand(Long id);
}