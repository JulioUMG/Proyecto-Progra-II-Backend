package com.hotel404.config;

import com.hotel404.repository.UsuariosRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
public class SeguridadConfig {

    // Conserva el acceso con contraseñas anteriores y cifra las nuevas.
    @Bean
    PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

            public String encode(CharSequence raw) {
                return bcrypt.encode(raw);
            }

            public boolean matches(CharSequence raw, String saved) {
                if (saved == null || raw == null) return false;
                return saved.startsWith("$2")
                    ? bcrypt.matches(raw, saved)
                    : java.security.MessageDigest.isEqual(
                          raw.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8),
                          saved.getBytes(java.nio.charset.StandardCharsets.UTF_8)
                      );
            }
        };
    }

    // Busca la cuenta y asigna los permisos de su perfil al iniciar sesión.
    @Bean
    UserDetailsService userDetailsService(UsuariosRepository usuarios) {
        return nombre -> {
            var usuario = usuarios
                .findByUsuarioIgnoreCase(nombre)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
            var perfil = usuario.getIdPerfil();
            if (
                perfil == null ||
                (!perfil.getNombre().toLowerCase().contains("admin") &&
                    !perfil.getNombre().toLowerCase().contains("recep"))
            ) {
                throw new UsernameNotFoundException("Perfil sin acceso al sistema");
            }
            String rol = perfil.getNombre().toLowerCase().contains("admin")
                ? "ADMINISTRADOR"
                : "RECEPCIONISTA";
            return User.withUsername(usuario.getUsuario())
                .password(usuario.getContrasena())
                .roles(rol)
                .disabled(!usuario.getEstado() || !perfil.getEstado())
                .build();
        };
    }

    // Protege las rutas privadas y configura login, cierre de sesión y formularios.
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf ->
            csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
        )
            .authorizeHttpRequests(auth ->
                auth
                    .requestMatchers("/error", "/api/auth/csrf", "/api/auth/login", "/api/public/**")
                    .permitAll()
                    .requestMatchers("/usuarios/**", "/perfiles/**", "/api/admin/usuarios/**")
                    .hasRole("ADMINISTRADOR")
                    .requestMatchers(HttpMethod.GET, "/habitaciones/**")
                    .authenticated()
                    .requestMatchers("/habitaciones/**")
                    .denyAll()
                    .requestMatchers(HttpMethod.POST, "/api/admin/habitaciones")
                    .hasRole("ADMINISTRADOR")
                    .requestMatchers(HttpMethod.PUT, "/api/admin/habitaciones/*/datos")
                    .hasRole("ADMINISTRADOR")
                    // Las reservas se escriben por el flujo que valida fechas y habitaciones.
                    .requestMatchers(HttpMethod.GET, "/reservaciones/**", "/detalle-reservacion/**")
                    .authenticated()
                    .requestMatchers("/reservaciones/**", "/detalle-reservacion/**")
                    .denyAll()
                    .requestMatchers(HttpMethod.DELETE, "/huespedes/**")
                    .hasRole("ADMINISTRADOR")
                    .anyRequest()
                    .authenticated()
            )
            .formLogin(form ->
                form
                    .loginProcessingUrl("/api/auth/login")
                    .successHandler((req, res, auth) -> {
                        res.setCharacterEncoding("UTF-8");
                        res.setContentType("application/json");
                        res.getWriter().write("{\"mensaje\":\"Sesión iniciada\"}");
                    })
                    .failureHandler((req, res, ex) -> {
                        res.setStatus(401);
                        res.setCharacterEncoding("UTF-8");
                        res.setContentType("application/json");
                        res.getWriter().write("{\"mensaje\":\"Usuario o contraseña incorrectos\"}");
                    })
            )
            .logout(logout ->
                logout
                    .logoutUrl("/api/auth/logout")
                    .logoutSuccessHandler((req, res, auth) -> res.setStatus(204))
            )
            .exceptionHandling(errors ->
                errors
                    .authenticationEntryPoint((req, res, ex) -> res.sendError(401))
                    .accessDeniedHandler((req, res, ex) -> res.sendError(403))
            );
        return http.build();
    }
}
