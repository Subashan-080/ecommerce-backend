package edu.jsp.ecommerce.security;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class JwtService {
	
	
	  private final JwtEncoder jwtEncoder;

	    @Value("${app.jwt.expiration}")
	    private long expiration;

	
	   public String generateToken(Authentication authentication) {

	        Instant now = Instant.now();

	        List<String> roles = authentication.getAuthorities()
	                .stream()
	                .map(authority -> authority.getAuthority())
	                .filter(authority -> authority.startsWith("ROLE_"))
	                .toList();

	        JwtClaimsSet claims = JwtClaimsSet.builder()
	                .issuer("ecommerce")
	                .issuedAt(now)
	                .expiresAt(now.plus(expiration, ChronoUnit.MILLIS))
	                .subject(authentication.getName())
	                .claim("roles", roles)
	                .build();

	        JwsHeader header = JwsHeader
	                .with(MacAlgorithm.HS256)
	                .build();

	        return jwtEncoder.encode(
	                JwtEncoderParameters.from(header, claims))
	                .getTokenValue();
	    }

}
