package edu.jsp.ecommerce.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

import java.util.List;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import edu.jsp.ecommerce.security.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Value("${app.jwt.secret}")
	private String jwtSecret;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationProvider authenticationProvider(CustomUserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) {

		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

		provider.setPasswordEncoder(passwordEncoder);

		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

		return configuration.getAuthenticationManager();
	}

	private SecretKey getSecretKey() {

		byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);

		if (decodedKey.length < 32) {
			throw new IllegalStateException("JWT secret must contain at least 256 bits");
		}

		return new SecretKeySpec(decodedKey, "HmacSHA256");
	}

	@Bean
	public JwtEncoder jwtEncoder() {

		return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
	}

	@Bean
	public JwtDecoder jwtDecoder() {

		return NimbusJwtDecoder.withSecretKey(getSecretKey()).macAlgorithm(MacAlgorithm.HS256).build();
	}

	@Bean
	public JwtAuthenticationConverter jwtAuthenticationConverter() {

		JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();

		authoritiesConverter.setAuthoritiesClaimName("roles");
		authoritiesConverter.setAuthorityPrefix("");

		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

		converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);

		return converter;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(List.of("http://localhost:5173"));

		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

		configuration.setAllowedHeaders(List.of("*"));

		configuration.setAllowCredentials(false);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider,
			JwtAuthenticationConverter jwtAuthenticationConverter) throws Exception {

		http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(csrf -> csrf.disable())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.authenticationProvider(authenticationProvider)

				.authorizeHttpRequests(auth -> auth

						// Seller product management
						.requestMatchers(HttpMethod.GET, "/api/products/my-products").hasRole("SELLER")

						// Sellers and admins can create products
						.requestMatchers(HttpMethod.POST, "/api/products").hasAnyRole("SELLER", "ADMIN")

						// Sellers and admins can update products
						.requestMatchers(HttpMethod.PUT, "/api/products/*").hasAnyRole("SELLER", "ADMIN")

						// Sellers and admins can delete products
						.requestMatchers(HttpMethod.DELETE, "/api/products/*").hasAnyRole("SELLER", "ADMIN")
						
						// Public registration and login
						.requestMatchers(HttpMethod.POST, "/api/user", "/api/auth/login").permitAll()

						// Public product browsing
						.requestMatchers(HttpMethod.GET, "/api/products", "/api/products/*", "/api/products/search",
								"/api/products/category/**", "/api/products/brand/**")
						.permitAll()

						// Category management

						// Anyone can view categories
						.requestMatchers(HttpMethod.GET, "/api/categories").permitAll()

						// Only ADMIN can manage categories
						.requestMatchers(HttpMethod.POST, "/api/categories").hasRole("ADMIN")

						.requestMatchers(HttpMethod.PUT, "/api/categories/**").hasRole("ADMIN")

						.requestMatchers(HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")

						// Brand management

						// Anyone can view brands
						.requestMatchers(HttpMethod.GET, "/api/brands").permitAll()

						// Only ADMIN can manage brands
						.requestMatchers(HttpMethod.POST, "/api/brands").hasRole("ADMIN")

						.requestMatchers(HttpMethod.PUT, "/api/brands/**").hasRole("ADMIN")

						.requestMatchers(HttpMethod.DELETE, "/api/brands/**").hasRole("ADMIN")

						// Order management

						// Only ADMIN can view all orders
						.requestMatchers(HttpMethod.GET, "/api/orders/admin").hasRole("ADMIN")

						// Only SELLER can view their own orders
						.requestMatchers(HttpMethod.GET, "/api/orders/seller/**").hasRole("SELLER")

						.requestMatchers(HttpMethod.GET, "/api/orders/seller/*").hasRole("SELLER")

						// Only ADMIN can update order status
						.requestMatchers(HttpMethod.PATCH, "/api/orders/*/status").hasRole("ADMIN")

						.requestMatchers(HttpMethod.PATCH, "/api/orders/seller/*/items/*/status").hasRole("SELLER")

						// All other order endpoints require authentication
						.requestMatchers("/api/orders/**").authenticated()

						// User management
						.requestMatchers("/api/user/me/**").authenticated()

						.requestMatchers("/api/user/**").hasRole("ADMIN")

						// Admin-only endpoints
						.requestMatchers("/api/admin/**").hasRole("ADMIN")

						// All remaining API endpoints require authentication
						.anyRequest().authenticated())

				.oauth2ResourceServer(
						oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));

		return http.build();
	}

}
