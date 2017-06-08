package se.claremont.backend.user.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import se.claremont.backend.user.repository.UserRepository;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private ApplicationContext appContext;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/")
		.permitAll().antMatchers(HttpMethod.POST, "/login")
		.permitAll().anyRequest().authenticated().and()
		// We filter the api/login requests
		.addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
				UsernamePasswordAuthenticationFilter.class)
		// And filter other requests to check the presence of JWT in header
		.addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Create a default account
		UserRepository userDao = (UserRepository)appContext.getBean("userDao");
		CustomAuthenticationProvider customAuthenticationProvider = new CustomAuthenticationProvider();
		customAuthenticationProvider.setUserDao(userDao);
		auth.authenticationProvider(customAuthenticationProvider);
		auth.inMemoryAuthentication().withUser("admin").password("password").roles("ADMIN");
	}

	public ApplicationContext getAppContext() {
		return appContext;
	}

	public void setAppContext(ApplicationContext appContext) {
		this.appContext = appContext;
	}

}