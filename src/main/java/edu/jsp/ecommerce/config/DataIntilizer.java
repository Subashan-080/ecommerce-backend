package edu.jsp.ecommerce.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import edu.jsp.ecommerce.entity.Brand;
import edu.jsp.ecommerce.entity.Category;
import edu.jsp.ecommerce.entity.RoleEntity;
import edu.jsp.ecommerce.enums.Role;
import edu.jsp.ecommerce.repository.BrandRepository;
import edu.jsp.ecommerce.repository.CategoryRepository;
import edu.jsp.ecommerce.repository.RoleEntityRepository;
import lombok.AllArgsConstructor;
@Configuration
@AllArgsConstructor
public class DataIntilizer implements CommandLineRunner {
	private RoleEntityRepository roleEntityRepository;
	private BrandRepository brandRepository;
	private CategoryRepository categoryRepository;

	@Override
	public void run(String... args) throws Exception {
		seedRoles();
		seedBrand();
		
	}
	public void seedRoles() {
		Role roles[]=Role.values();
		Arrays.stream(roles).forEach(r ->{
			if(roleEntityRepository.findByName(r).isEmpty()) {
				roleEntityRepository.save(new RoleEntity(r));
			}
			
		});
	}
	
	
	
	
	public void seedBrand() {
		if(brandRepository.count()>0) {
			return;
		}
		List <Brand> brands=List.of(
				brand("Samsung","https://seeklogo.com/vector-logo/122017/samsung"),
				brand("Apple","https://rabbitlogo.com/wp-content/uploads/2026/01/apple-1.jpg"),
				brand("Nike","https://i.insider.com/53d29d5c6bb3f7a80617ada8?width=800&format=jpeg&auto=webp"),
				brand("Adidas","https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQQYg_WFeBA7bIlMqahjoACB6kzwFFHGVFQ2d69uKycJIqj7Nt8q-3tTkc&s=10"),
				brand("Sony","https://1000logos.net/wp-content/uploads/2017/06/Sony-Logo.jpg"),
				brand("Allen_Solly","https://i.pinimg.com/736x/a4/48/66/a448663f61e275ec5b8b6f4fe7d114c3.jpg"),
				brand("Louis Vuitton","https://media.designrush.com/inspiration_images/732076/conversions/Louis-Vuitton-Logo-SVG-mobile-webp.webp")
				);
		brandRepository.saveAll(brands);
	}
	
	
	
	public Brand brand(String name,String logourl) {
		return Brand.builder().name(name).logourl(logourl).active(true).build();
	}
	
	
	private Category cat(String name, String description ,Category parent ,String imageUrl) {
		return Category.builder().name(name).description(description).parent(parent).imageUrl(imageUrl).active(true).build();
	}
	
	private Category save(Category category) {
		return categoryRepository.save(category);
	}
	
	
	 public void seedCategory() {

	        if (categoryRepository.count() > 0) {
	            return;
	        }

	        Category electronics = save(cat("Electronics",
	                "Consumer electronics & gadgets", null,
	                "https://img.icons8.com/color/96/electronics.png"));

	        Category fashion = save(cat("Fashion",
	                "Clothing, footwear & accessories", null,
	                "https://img.icons8.com/color/96/shopping-bag.png"));

	        Category home = save(cat("Home & Kitchen",
	                "Home decor, kitchen & appliances", null,
	                "https://img.icons8.com/color/96/home.png"));

	        Category sports = save(cat("Sports",
	                "Sports, fitness & outdoors", null,
	                "https://img.icons8.com/color/96/sports.png"));

	        Category books = save(cat("Books",
	                "Books, e-books & stationery", null,
	                "https://img.icons8.com/color/96/book.png"));

	        Category beauty = save(cat("Beauty",
	                "Beauty, health & personal care", null,
	                "https://img.icons8.com/color/96/makeup.png"));

	        Category toys = save(cat("Toys & Baby",
	                "Toys, games & baby products", null,
	                "https://img.icons8.com/color/96/teddy-bear.png"));

	        save(cat("Mobiles", "Smartphones & accessories",
	                electronics, null));
	        save(cat("Laptops", "Laptops, tablets & accessories",
	                electronics, null));
	        save(cat("Cameras", "DSLR, mirrorless & accessories",
	                electronics, null));
	        save(cat("Audio", "Headphones, earbuds & speakers",
	                electronics, null));
	        save(cat("Televisions", "Smart TVs & accessories",
	                electronics, null));
	        save(cat("Wearables", "Smartwatches & fitness bands",
	                electronics, null));

	        save(cat("Men's Clothing", "Shirts, trousers & ethnic wear",
	                fashion, null));
	        save(cat("Women's Clothing", "Kurtas, sarees & western wear",
	                fashion, null));
	        save(cat("Footwear", "Shoes, sandals & sneakers",
	                fashion, null));
	        save(cat("Accessories", "Watches, bags & sunglasses",
	                fashion, null));

	        save(cat("Kitchen Appliances", "Mixers, OTGs & microwaves",
	                home, null));
	        save(cat("Furniture", "Sofas, beds & storage",
	                home, null));
	        save(cat("Decor", "Lamps, clocks & art",
	                home, null));

	        save(cat("Gym & Fitness", "Dumbbells, mats & equipment",
	                sports, null));
	        save(cat("Cricket", "Bats, balls & gear",
	                sports, null));
	        save(cat("Cycling", "Cycles, helmets & accessories",
	                sports, null));
	    }

}
