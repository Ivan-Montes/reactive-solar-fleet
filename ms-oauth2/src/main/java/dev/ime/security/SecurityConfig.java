package dev.ime.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	public SecurityConfig() {
    	super();
    }
	
    @Bean 
   	@Order(1)
   	SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
    	
    	OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = OAuth2AuthorizationServerConfigurer.authorizationServer(); 
    	
    	http
		.securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
		.with(authorizationServerConfigurer, authorizationServer ->
			authorizationServer
				.oidc(Customizer.withDefaults())
		);
    	
           http.exceptionHandling(exceptions -> exceptions.defaultAuthenticationEntryPointFor(
                   new LoginUrlAuthenticationEntryPoint("/login"),
                   new MediaTypeRequestMatcher(MediaType.TEXT_HTML))
           ).oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults())
           );

           return http.build();
   	}

    @Bean 
	@Order(2)
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		
		http
        .authorizeHttpRequests(auth -> {
            auth.requestMatchers("/login", "/register").permitAll();
			auth.requestMatchers("/h2-console/**").permitAll(); 
            auth.requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll();
			auth.requestMatchers("/actuator/metrics").authenticated();
			auth.requestMatchers("/actuator/**").permitAll();
            auth.anyRequest().authenticated();
        })  
        .headers(head-> head.frameOptions(f->f.sameOrigin()))
		.csrf(AbstractHttpConfigurer::disable)
		.formLogin(Customizer.withDefaults());

		return http.build();
		
	}
    
}
