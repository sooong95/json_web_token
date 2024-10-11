package json_web_token.json_web_token.config;

import jakarta.servlet.http.HttpServletRequest;
import json_web_token.json_web_token.jwt.JwtFilter;
import json_web_token.json_web_token.jwt.JwtUtil;
import json_web_token.json_web_token.jwt.LoginFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collection;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // AuthenticationManager 가 인자로 받을 AuthenticationConfiguration 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean // AuthenticationManager bean 등록
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // cors 설정
        http.cors(cors -> cors
                .configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();
                        // 허용할 프론트엔드 서버 포트
                        configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                        // get, post 와 같은 모든 메서드를 허용
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        // 프론트에서 Credential 설정을 하면 true 로 해주어야 한다.
                        configuration.setAllowCredentials(true);
                        // 허용할 헤더
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        // 허용할 시간
                        configuration.setMaxAge(3600L);

                        // 서버에서 클라이언트로 헤더를 보내줄때 Authorization 에 jwt 를 넣어 보내줄 것이기 때문에, Authorization 헤더도 허용
                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                })
        );

        // csrf disable
        http.csrf((auth) -> auth.disable());

        // form 로그인 방식 disable
        http.formLogin((auth) -> auth.disable());

        // http basic 인증 방식 disable
        http.httpBasic((auth) -> auth.disable());

        // 경로별 인가 작업
        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/login", "/", "/join").permitAll() // 해당 하는 경로는 모든 권한을 허용.
                .requestMatchers("/admin").hasRole("ADMIN") // /admin 경로는 ADMIN 권한을 가진 이용자만 접근 가능.
                .anyRequest().authenticated() // 그외 경로는 로그인한 사용자만 접근 가능.
        );

        // addFilterBefore 특정 필터 앞에 필터를 추가
        http.addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

        // addFilterAt => 기존에 있던 UsernamePasswordAuthenticationFilter 대신 커스텀한 LoginFilter 로 대체
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 세션 설정, jwt 에서는 항상 세션을 state less 방식으로 관리. 이 부분이 가장 중요.
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
