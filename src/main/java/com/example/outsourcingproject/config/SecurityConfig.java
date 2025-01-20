package com.example.outsourcingproject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.outsourcingproject.exception.AuthenticationEntryPoint;
import com.example.outsourcingproject.exception.JwtAccessDeniedHandler;
import com.example.outsourcingproject.filter.JwtFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration

@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // @PreAuthorize 활성화
public class SecurityConfig {

	private final JwtFilter jwtFilter;

	// AuthenticationManger Bean 등록
	@Bean
	public AuthenticationManager authenticationManager(
		AuthenticationConfiguration authenticationConfiguration
	) throws
		Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	// SecurityFilterChain 설정
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		commonConfigureations(http);
		configureStores(http);

		http.authorizeHttpRequests(auth -> auth
			.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
			.requestMatchers("/users/login", "/users/signup").permitAll()
			.anyRequest().authenticated())
			.exceptionHandling(exceptions -> exceptions
			.authenticationEntryPoint(new AuthenticationEntryPoint())
			.accessDeniedHandler(new JwtAccessDeniedHandler())
			)
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build(); // HttpSecurity 객체를 SecurityFilterChain으로 변환
	}

	//-------------------------- private --------------------------
	private void commonConfigureations(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
			.httpBasic(basic -> basic.disable())
			.formLogin(form -> form.disable());
	}

	private void configureStores(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth
			.requestMatchers(HttpMethod.GET, "/api/stores/**").hasAnyRole("CUSTOMER", "OWNER")
			.requestMatchers("/api/stores/**").hasRole("OWNER"));
	}


}
