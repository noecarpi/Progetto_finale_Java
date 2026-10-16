package it.aulab.progetto_finale_java.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import it.aulab.progetto_finale_java.services.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired 
    private CustomUserDetailsService customUserDetailsService;

    @Autowired 
    private PasswordEncoder passwordEncoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(
                        "/admin/dashboard",
                        "/operations/career/request/detail/**",
                        "/operations/career/request/accept/**",
                        "/operations/career/request/reject/**",
                        "/categories",
                        "/categories/create",
                        "/categories/edit/**",
                        "/categories/update/**",
                        "/categories/delete/**")
                    .hasRole("ADMIN")

                .requestMatchers(
                        "/revisor/dashboard",
                        "/articles/revisor/detail/**",
                        "/articles/accept")
                    .hasRole("REVISOR")

                .requestMatchers(
                        "/writer/dashboard",
                        "/articles/create",
                        "/articles/save",
                        "/articles/writer/**",
                        "/articles/edit/**",
                        "/articles/update/**",
                        "/articles/delete/**")
                    .hasRole("WRITER")

                .requestMatchers(
                        "/register", "/register/**", "/",
                        "/images/**", "/css/**",
                        "/articles",
                        "/articles/detail/**",
                        "/articles/search/**",
                        "/categories/search/**",
                        "/search/**")
                .permitAll()
                .anyRequest().authenticated())

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .defaultSuccessUrl("/")
                .permitAll())

            .logout(logout -> logout
                .logoutRequestMatcher(
                    PathPatternRequestMatcher.pathPattern("/logout"))
                .permitAll())

            .exceptionHandling(exception -> exception
                .accessDeniedPage("/error/403"))

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .expiredUrl("/login?session-expired=true"));

        return http.build();
        }

    @Bean
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
        .passwordEncoder(passwordEncoder);
    }
}
