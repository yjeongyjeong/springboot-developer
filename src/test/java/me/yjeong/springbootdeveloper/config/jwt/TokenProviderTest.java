package me.yjeong.springbootdeveloper.config.jwt;

import io.jsonwebtoken.Jwts;
import me.yjeong.springbootdeveloper.domain.User;
import me.yjeong.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtProperties jwtProperties;

    // generateToken() 검증 테스트 : 입력한 유저 정보대로 토큰이 생성되는지
    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰 생성이 가능")
    @Test
    public void generateToken(){
        //given
        User testUser = userRepository.save(User.builder()
                .email("testUser@email.com")
                .password("test")
                .build());

        //when
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        //then
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        //검증
        assertThat(userId).isEqualTo(testUser.getId());
    }

    // validToken() 검증 테스트 : 복호화 과정에서 에러가 발생하는 경우(서명이 올바르지 않거나 토큰 만료, 변조 등) 검증에 실패, 발생하지 않는 경우 검증 성공
    @DisplayName("validToken(): 만료된 토큰인 경우 유효성 검증에 실패")
    @Test
    public void validToken_invalidToken(){
        //given
        String token = JwtFactory.builder()
                .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build()
                .createToken(jwtProperties);

        //when
        boolean result = tokenProvider.validToken(token);

        //then
        assertThat(result).isFalse();
    }

    @DisplayName("vaildToken(): 유효한 토큰인 경우 유효성 검증에 성공")
    @Test
    public void validToken_validToken(){
        //given
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);

        //when
        boolean result = tokenProvider.validToken(token);

        //then
        assertThat(result).isTrue();
    }

    // getAuthentication() 검증 테스트 : 생성된 토큰을 기반으로 유저의 정보가 잘 가져와 지는가 검증
    @DisplayName("getAuthentication(): 토큰 기반으로 인증 정보를 가져옴")
    @Test
    public void getAuthentication(){
        //given
        String userEmail = "userTest@test.com";
        String token = JwtFactory.builder()
                .subject(userEmail)
                .build()
                .createToken(jwtProperties);

        //when
        Authentication authentication = tokenProvider.getAuthentication(token);

        //then
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    // getUserId() 검증 테스트 : 토큰 기반으로 유저 Id를 반환하도록 하며, private 메서드인 getClaims()를 호출
    @DisplayName("getUserId(): 토큰으로 유저 Id를 가져올 수 있음")
    @Test
    public void getUserId(){
        //given
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build()
                .createToken(jwtProperties);

        //when
        Long userIdByToken = tokenProvider.getUserId(token);

        //then
        assertThat(userIdByToken).isEqualTo(userId);
    }
}
