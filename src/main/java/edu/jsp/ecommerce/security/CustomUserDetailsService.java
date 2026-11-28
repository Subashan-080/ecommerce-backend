package edu.jsp.ecommerce.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import edu.jsp.ecommerce.Serviece.UserService;
import edu.jsp.ecommerce.entity.User;
import edu.jsp.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	private final UserRepository userRepository;
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	 User user = userRepository.findByEmail(email)
             .orElseThrow(() ->
                 new UsernameNotFoundException(
                     "User not found: " + email));

     return org.springframework.security.core.userdetails.User
             .withUsername(user.getEmail())
             .password(user.getPassword())
             .authorities(
                 user.getRoles().stream()
                 .map(role -> {
                	    String roleName = role.getName().name();

                	    return roleName.startsWith("ROLE_")
                	            ? roleName
                	            : "ROLE_" + roleName;
                	})
                     .toArray(String[]::new)
             )
             .accountLocked(user.isAccountLocked())
             .disabled(!user.isAccountEnabled())
             .build();
	}

}
