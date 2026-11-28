package edu.jsp.ecommerce.Response;

import java.time.LocalDateTime;
import java.util.Set;

import edu.jsp.ecommerce.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private String phno;
	private String profilePictureUrl;
	private boolean emailVerified;
	private boolean accountEnabled;
	private boolean enabled;
	private Set<Role> roles;
	private LocalDateTime createAt;

}
