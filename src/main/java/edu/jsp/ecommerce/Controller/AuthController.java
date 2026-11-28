package edu.jsp.ecommerce.Controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.jsp.ecommerce.Request.LoginRequest;
import edu.jsp.ecommerce.Response.ApiResponse;
import edu.jsp.ecommerce.Response.AuthResponse;
import edu.jsp.ecommerce.security.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
	 private final AuthenticationManager authenticationManager;
	    private final JwtService jwtService;
	    
	    @Value("${app.jwt.expiration}")
	    private long expiration;
	    
	    @PostMapping("/login")
	    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request){
	    	
	    	 Authentication authentication =
	                 authenticationManager.authenticate(
	                     new UsernamePasswordAuthenticationToken(
	                         request.getEmail(),
	                         request.getPassword()
	                     )
	                 );
	    	 
	    	 
	    	 String token = jwtService.generateToken(authentication);

	         AuthResponse response = AuthResponse.builder()
	                 .accessToken(token)
	                 .tokenType("Bearer")
	                 .expiresIn(expiration / 1000)
	                 .build();
	         
	         return ResponseEntity.ok(
	                 ApiResponse.sucess(
	                     response,
	                     "Login successful"
	                 )
	         );
	    	
	    }


}
