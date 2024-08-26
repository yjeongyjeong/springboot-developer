package me.yjeong.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static java.util.Collections.emptyMap;

@Getter
public class JwtFactory {
    private String subject = "text@email.com";
    private Date issuedAt = new Date();
    private Date expiration = new Date(new Date().getTime() + Duration.ofDays(14).toMillis());
    private Map<String, Object> claims = emptyMap();

    // 빌더 패턴을 이용해 설정이 필요한 곳만 설정
    @Builder
    public JwtFactory(String subject, Date issuedAt, Date expiration, Map<String, Object> claims){
        this.subject = subject != null ? subject : this.subject;
        this.issuedAt = issuedAt != null ? issuedAt : this.issuedAt;
        this.expiration = expiration != null ? expiration : this.expiration;
        this.claims = claims != null ? claims : this.claims;
    }

    public static JwtFactory withDefaultValues(){
        return JwtFactory.builder().build();
    }

    // jwt 라이브러리를 통해 JWT 토큰 생성
    public String createToken(JwtProperties jwtProperties){
        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)   // header typ : JWT
                .setIssuer(jwtProperties.getIssuer())   // payload_iss : propertise 파일에서 설정한 issuer 값 (JwtProperties의 issuer)
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)  
                .setSubject(subject)
                .addClaims(claims)  // add로 값을 넣어줌
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())   // signature : 비밀값(propertise 파일의 secret_key)과 해시값을 HS256 방식으로 암호화
                .compact();
    }
}
