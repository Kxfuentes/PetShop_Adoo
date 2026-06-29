package com.petshop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/h2-console/**", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/", "/reportes").hasAnyRole("ADMIN", "CAJERO", "RECEPCIONISTA")
                        .requestMatchers("/ventas/**", "/productos/**", "/inventario/**").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers("/citas/**", "/clientes/**", "/mascotas/**", "/servicios/**").hasAnyRole("ADMIN", "RECEPCIONISTA")
                        .requestMatchers(HttpMethod.GET, "/api/reportes/**").hasAnyRole("ADMIN", "CAJERO", "RECEPCIONISTA")
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/api/comercial/categorias/**", "/api/comercial/productos/**",
                                "/api/comercial/inventario/**").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers("/api/comercial/ventas/**").hasAnyRole("ADMIN", "CAJERO")
                        .requestMatchers(HttpMethod.POST, "/api/servicios/clientes").hasAnyRole("ADMIN", "CAJERO", "RECEPCIONISTA")
                        .requestMatchers("/api/servicios/clientes/**", "/api/servicios/mascotas/**",
                                "/api/servicios/citas/**").hasAnyRole("ADMIN", "RECEPCIONISTA")
                        .requestMatchers("/api/servicios/servicios/**").hasAnyRole("ADMIN", "RECEPCIONISTA")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
