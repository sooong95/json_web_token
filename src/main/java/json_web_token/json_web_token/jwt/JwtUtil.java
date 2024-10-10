package json_web_token.json_web_token.jwt;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // 시크릿 키를 저장할 객체
    private SecretKey secretKey;

    // application.properties 에 있는 암호키를 불러옴.
    // jwt 는 string 타입의 키를 사용하지 않기 때문에, 그걸 기반으로 Secret 타입의 객체키를 만듦.
    public JwtUtil(@Value("${spring.jwt.secret.song}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    // username 검증
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    // role 검증
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    // 만료일 검증
    public Boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    // jwt 생성 - expiredMS -> 토큰이 살아있을 시간
    public String createJwt(String username, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis())) // 현재 발행 시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 시간 = 현재 발행 시간 + 인자로 받은 토큰 생존 시간
                .signWith(secretKey) // 시그니쳐
                .compact();
    }

}
