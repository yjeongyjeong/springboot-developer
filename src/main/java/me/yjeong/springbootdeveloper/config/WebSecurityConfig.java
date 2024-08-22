package me.yjeong.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import me.yjeong.springbootdeveloper.service.UserDetailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration  // 설정파일임을 암시
public class WebSecurityConfig {
    private final UserDetailService userService;

    // 스프링 시큐리티 기능 비활성화
    @Bean
    public WebSecurityCustomizer configure(){
        return (web) -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/static/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .authorizeRequests()    // 인증, 인가 설정
                .requestMatchers("/login", "/signup", "/user").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin(formLogin -> formLogin   // 폼 기반 로그인 설정
                        .loginPage("/login")
                        .defaultSuccessUrl("/articles")
                )
                .logout(logout -> logout    // 로그아웃 설정
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true) // 로그아웃 후 세션 전체 삭제
                )
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    // 인증 관리자 관련 설정
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailService userDetailService) throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);

        AuthenticationManagerBuilder authManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authManagerBuilder.authenticationProvider(authProvider);

        return authManagerBuilder.build();
    }

    // 패스워드 인코더로 사용할 빈 생성
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
