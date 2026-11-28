package edu.jsp.ecommerce.Serviece;

import java.util.List;

import edu.jsp.ecommerce.Request.RegisterRequest;
import edu.jsp.ecommerce.Response.UserResponse;
import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.enums.Role;

public interface UserService  {
	UserResponse getProfile(String email);
	UserResponse getUserById(long id);
	List<UserResponse> getAllUser();
	
	void addRole(long userid ,Role role);
	void removeRole(long userid ,Role role);
	void toggleUserEnabled(long userid);
	 UserResponse register(RegisterRequest request);

}
