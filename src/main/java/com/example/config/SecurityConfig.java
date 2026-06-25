package com.example.config;

// Spring Security imports
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Arrays;

import com.example.model.UserViewModel;
import com.example.security.CustomUserDetails;
import com.example.service.UserService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private UserService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF for testing POST requests in Postman
                .csrf(csrf -> csrf.disable())

                // 2. Enable HTTP Basic Authentication for Postman headers
                .httpBasic(org.springframework.security.config.Customizer.withDefaults())
                
                .authorizeHttpRequests(auth -> auth
                        .antMatchers("/resources/**").permitAll()
                        .antMatchers("/css/**").permitAll()
                        .antMatchers("/images/**").permitAll()
                        .antMatchers("/js/**").permitAll()
                        .antMatchers("/user/register", "/user/login").permitAll()
                        // Activity permissions
                        .antMatchers("/resources/**", "/activity/activityList", "/activity/activityDetails/**",
                                "/activity/filterActivities")
                        .authenticated()
                        .antMatchers("/activity/addActivity", "/activity/editActivity/**").hasRole("2") // Teachers only
                        .antMatchers("/activity/addFeedback/**").hasRole("1") // Admin only
                        .antMatchers("/activity/showFeedback/**").hasAnyRole("1", "2") // Admin and Teachers
                        .antMatchers("/resource/**").hasAnyRole("1", "2", "3")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/user/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/user/dashboard")
                        .failureUrl("/user/login?error=true"))
                .logout(logout -> logout
                        .logoutUrl("/user/logout")
                        .logoutSuccessUrl("/user/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            UserViewModel user = userService.findUserByEmail(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found");
            }

            return new CustomUserDetails(
                    user.getId(),
                    user.getSchoolId(),
                    user.getEmail(),
                    user.getPassword(),
                    Arrays.asList(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}
