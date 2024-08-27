package me.yjeong.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.yjeong.springbootdeveloper.config.jwt.JwtFactory;
import me.yjeong.springbootdeveloper.config.jwt.JwtProperties;
import me.yjeong.springbootdeveloper.domain.RefreshToken;
import me.yjeong.springbootdeveloper.domain.User;
import me.yjeong.springbootdeveloper.dto.CreateAccessTokenRequest;
import me.yjeong.springbootdeveloper.repository.RefreshTokenRepository;
import me.yjeong.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TokenApiControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        userRepository.deleteAll();
    }

    @DisplayName("createNewAccessToken: 새로운 엑세스 토큰 발급")
    @Test
    public void createNewAccessToken() throws Exception{
        //given
        final String url = "/api/token";

        User testUser = userRepository.save(User.builder()
                        .email("user@test.com")
                        .password("test")
                .build());

        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(jwtProperties);

        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));

        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);

        final String requestBody = objectMapper.writeValueAsString(request);

        //when
        ResultActions resultActions = mockMvc.perform(post(url)     // request를 받아서 유효성 검증 후 토큰 생성
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));

        //then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}