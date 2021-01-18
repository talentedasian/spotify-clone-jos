package com.jos.spotifyclone.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO Auto-generated method stub
		
		http.httpBasic().disable().
		headers()
		.contentSecurityPolicy("child-src 'self")
		.and()
		.xssProtection().block(true).disable()
		.frameOptions().deny();
	}

	
	
}
