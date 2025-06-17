package com.example.logistics_company.config;

import com.example.logistics_company.services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация на Spring Security за приложението.
 * Задава правила за достъп до URL-и, HTTP Basic аутентикация
 * и предоставя бинове за хеширане на пароли и зареждане на потребители.
 */
@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService uds) {
        this.userDetailsService = uds;
    }

    /**
     * Създава PasswordEncoder, който използва BCrypt алгоритъм.
     * Използва се за хеширане и проверка на пароли.
     *
     * @return BCryptPasswordEncoder бин за енкодиране на пароли
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Конфигурира DaoAuthenticationProvider, който използва нашата имплементация
     * на UserDetailsService за зареждане на потребител по потребителско име
     * и PasswordEncoder за проверка на паролата.
     *
     * @return DaoAuthenticationProvider бин, който Spring Security използва за аутентикация
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    /**
     * Дефинира SecurityFilterChain, който конфигурира:
     * - Забрана на CSRF (подходящо при stateless API)
     * - Публични пътища (статични ресурси и регистрация на потребител)
     * - Задължителна аутентикация за всички /api/** endpoints
     * - HTTP Basic аутентикация
     * - Използване на предварително конфигурирания DaoAuthenticationProvider
     *
     * Формата за вход/регистрация и цялата фронт-енд логика (HTML/CSS/JS)
     * се зареждат без нужда от предварителен логин
     *
     * Аутентикацията се извършва по HTTP Basic, като за да валидираме
     * потребителя и паролата, ползваме нашия DaoAuthenticationProvider
     * (който на свой ред използва CustomUserDetailsService + BCryptPasswordEncoder)
     *
     * @param http HttpSecurity обект за конфигуриране на защитата
     * @return SecurityFilterChain бин, който Spring Security регистрира
     * @throws Exception при грешка в конфигурацията
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf(cs -> cs.disable()).authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index.html", "/favicon.ico",
                                "/css/**", "/js/**",
                                "/api/users/register"
                        ).permitAll()
                        .requestMatchers("/api/**").authenticated()
                )

                .authenticationProvider(authenticationProvider())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}