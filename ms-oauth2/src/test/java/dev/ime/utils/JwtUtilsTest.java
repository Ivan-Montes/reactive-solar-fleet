package dev.ime.utils;


import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;

import dev.ime.model.User;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

	@Mock
    private JwtEncoder jwtEncoder;
	
	@Mock
    private AuthorizationServerSettings authorizationServerSettings;

	@InjectMocks
	private JwtUtils jwtUtils;
	
	private User user;
	private final String name = "B2";
	private final String lastname = "Yorha";
	private final String email = "email@domain.tk";
	private final String password = "pass";
	private final String issuer = "localhost:port";
	private String tokenValue;
	
	@BeforeEach
	private void setUp() {		
		
		user = new User();
		user.setName(name);
		user.setLastname(lastname);
		user.setEmail(email);
		user.setPassword(password);
		
		tokenValue = UUID.randomUUID().toString();
	}
		
	@Test
	void generateToken_WithUser_ReturnString() {
		
		Jwt jwt = Mockito.mock(Jwt.class);		
		Mockito.when(authorizationServerSettings.getIssuer()).thenReturn(issuer);
		Mockito.when(jwtEncoder.encode(Mockito.any(JwtEncoderParameters.class))).thenReturn(jwt);
		Mockito.when(jwt.getTokenValue()).thenReturn(tokenValue);
		
		String result = jwtUtils.generateToken(user);
		
		org.junit.jupiter.api.Assertions.assertAll(
				()-> Assertions.assertThat(result).isNotNull(),
				()-> Assertions.assertThat(result).isNotBlank()
				);
	}
	

}
