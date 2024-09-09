package me.yjeong.springbootdeveloper.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.yjeong.springbootdeveloper.domain.Article;
import me.yjeong.springbootdeveloper.domain.User;
import me.yjeong.springbootdeveloper.dto.AddArticleRequest;
import me.yjeong.springbootdeveloper.dto.UpdateArticleRequest;
import me.yjeong.springbootdeveloper.repository.BlogRepository;
import me.yjeong.springbootdeveloper.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest //테스트용 애플리케이션 컨텍스트
@AutoConfigureMockMvc //MockMvc를 생성
@Log4j2
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper; //직렬화, 역직렬화를 위한 클래스!

    @Autowired
    private WebApplicationContext context; // 모든 정보를 담고있는 context (mockMvc에서 사용하기 위해)

    @Autowired
    private BlogRepository blogRepository;

    @Autowired
    private UserRepository userRepository;
    User user;

    @BeforeEach //테스트 실행전 메서드
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
        blogRepository.deleteAll();
    }

    @BeforeEach
    public void setSecurityContext(){
        // 원활한 테스트를 위해 기존 유저 삭제 후 새로 입력
        userRepository.deleteAll();
        user = userRepository.save(User.builder()
                        .email("userTest@test.com")
                        .password("test")
                .build());

        // SecurityContextHolder에 설정 추가 (principal, credentials, authorities)
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities()));
        log.info("user.getAuthorities >> " + user.getAuthorities());
    }

    @DisplayName("addArticle : 블로그 글 추가")
    @Test
    public void addArticle() throws Exception{
        //given
        final String url = "/api/articles";
        final String title = "title";
        final String content = "content";
        final AddArticleRequest userRequest = new AddArticleRequest(title, content);

        //객체를 JSON으로 직렬화
        final String requestBody = objectMapper.writeValueAsString(userRequest);

        // 테스트 목적으로 principal 모킹 및 getName시 username을 반환하도록 설정
        Principal principal = Mockito.mock(Principal.class);
        Mockito.when(principal.getName()).thenReturn("username");

        //when
        //설정한 내용을 바탕으로 요청 전송
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .principal(principal)
                .content(requestBody));

        //then
        result.andExpect(status().isCreated());

        List<Article> articles = blogRepository.findAll();

        assertThat(articles.size()).isEqualTo(1); //크기가 1인지(@beforeEach로 전체 삭제 후 데이터 하나 추가했으므로)
        assertThat(articles.get(0).getTitle()).isEqualTo(title);
        assertThat(articles.get(0).getContent()).isEqualTo(content);
        assertThat(principal.getName()).isEqualTo("username");

    }

    @DisplayName("findAllArticles : 블로그 글 전체 조회")
    @Test
    public void findAllArticles() throws Exception{
        //given
        final String url = "/api/articles";
        Article savedArticle = createDefaultArticle();

        //when
        final ResultActions result = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(savedArticle.getTitle()))
                .andExpect(jsonPath("$[0].content").value(savedArticle.getContent()));
    }

    @DisplayName("findArticle: 블로그 글 조회(findById)")
    @Test
    public void findArticle() throws Exception{
        //given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        //when
        final ResultActions result = mockMvc.perform(get(url, savedArticle.getId()));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(savedArticle.getTitle()))
                .andExpect(jsonPath("$.content").value(savedArticle.getContent()));
    }

    @DisplayName("deleteArticle: 블로그 글 삭제(deleteById)")
    @Test
    public void deleteArticle() throws Exception{
        //given
        final String url = "/api/articles/{id}";
        Article savedArticle = createDefaultArticle();

        //when
        mockMvc.perform(delete(url, savedArticle.getId()))
                .andExpect(status().isOk());

        //then
        List<Article> articles = blogRepository.findAll();
        assertThat(articles).isEmpty();
    }

    @DisplayName("updateArticle: 블로그 글 수정")
    @Test
    public void updateArticle() throws Exception{
        //given
        final String url = "/api/articles/{id}";
        final String newTitle = "new title";
        final String newContent = "new content";
        Article savedArticle = createDefaultArticle();

        UpdateArticleRequest request = new UpdateArticleRequest(newTitle, newContent);

        //when
        ResultActions result = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request))); //직렬화

        //then
        result.andExpect(status().isOk());

        Article article = blogRepository.findById(savedArticle.getId()).get(); //savedArticle이 request내용으로 수정되어야 함

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);
    }

    private Article createDefaultArticle() {
        return blogRepository.save(Article.builder()
                        .title("title")
                        .author(user.getUsername())
                        .content("content")
                .build());
    }
}