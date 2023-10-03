package com.adobe.acrobatsign.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.adobe.acrobatsign.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	private DataSource dataSource;

	 public static final String[] ENDPOINTS_WHITELIST = {
	            "/css/**",
	            "/",
	            "/login",
	            "/chat",
	            "/conversation",
	            "/index"
	    };
	 
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		  http.authorizeRequests().antMatchers(ENDPOINTS_WHITELIST).authenticated().anyRequest().
		  permitAll().and().formLogin()
		  .usernameParameter("email").defaultSuccessUrl("/index").permitAll().and().
		  logout().logoutSuccessUrl("/") .permitAll();
		  
		  http.cors().and().csrf().disable();
		 
		/* 
		 * http.cors().and().csrf().disable();
		 * http.authorizeRequests().anyRequest().permitAll();
		 */
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	@Bean
	public UserDetailsService userDetailsService() {
		return new CustomUserDetailsService();
	}

}
