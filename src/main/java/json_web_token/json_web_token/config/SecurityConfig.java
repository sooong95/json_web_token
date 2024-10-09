package json_web_token.json_web_token.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

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

        // 세션 설정, jwt 에서는 항상 세션을 state less 방식으로 관리. 이 부분이 가장 중요.
        http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
