package me.yjeong.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.yjeong.springbootdeveloper.config.jwt.TokenProvider;
import me.yjeong.springbootdeveloper.domain.User;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Log4j2
@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken){
        // 토큰 유효성 검사에 실패하면 예외 발생
        if (!tokenProvider.validToken(refreshToken)){
            log.error("[TokenService.createNewAccessToken] : Error");
            log.error("[TokenService.createNewAccessToken] : refreshToken >> " + refreshToken);
            throw new IllegalArgumentException("Unexpected token");
        }
        // 토큰이 유효하면 새로운 token 생성
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
