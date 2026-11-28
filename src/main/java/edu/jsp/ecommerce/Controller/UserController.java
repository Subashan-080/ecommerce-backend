package edu.jsp.ecommerce.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.Authentication;




import edu.jsp.ecommerce.Request.RegisterRequest;
import edu.jsp.ecommerce.Response.ApiResponse;
import edu.jsp.ecommerce.Response.UserResponse;
import edu.jsp.ecommerce.Serviece.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor

public class UserController {
	@Autowired
	private  final UserService userService;
	@PostMapping
	public ResponseEntity<ApiResponse<UserResponse>> register(
			@Valid @RequestBody RegisterRequest registerRequest){
		
		ApiResponse<UserResponse> apiResponse=
				ApiResponse.sucess(userService.register(registerRequest), "user data saved!!");
		return ResponseEntity.ok(apiResponse);
	}
	
	
	
	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserResponse>> getProfile(Authentication authentication){
		ApiResponse<UserResponse> apiResponse=
				ApiResponse.sucess(userService.getProfile(authentication.getName()), "user data received");
		return ResponseEntity.ok(apiResponse);
	}
	
	
	
	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId){
		ApiResponse<UserResponse> apiResponse=ApiResponse.sucess(userService.getUserById(userId), "user data received");
		return ResponseEntity.ok(apiResponse);
	}
	


} 
