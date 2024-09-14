package me.yjeong.springbootdeveloper.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.yjeong.springbootdeveloper.domain.Article;
import me.yjeong.springbootdeveloper.dto.AddArticleRequest;
import me.yjeong.springbootdeveloper.dto.ArticleResponse;
import me.yjeong.springbootdeveloper.dto.UpdateArticleRequest;
import me.yjeong.springbootdeveloper.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@RestController //@Controller + @ResponseBody(HTTP의 body -> Java객체)
public class BlogApiController {

    private final BlogService blogService;

    //HTTP 메서드가 POST일 때, 전달받은 URL과 동일하면 메서드로 매핑
    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody @Valid AddArticleRequest request, Principal principal){
        Article savedArticle = blogService.save(request, principal.getName());
        // 요청한 자원이 성공적으로 생성되었으며 저장된 블로그 글 정보를 응답 객체에 담아 전송
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
    }

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles(){
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new) //메서드 레퍼런스 사용 -> map(article -> new ArticleResponse(article))와 동일
                .toList();

        return ResponseEntity.ok().body(articles);
    }

    @GetMapping("/api/articles/{id}") //@PathVariable을 통해 URL에서 {id}에 해당하는 값이 id로 들어옴
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable("id") Long id){
        Article article = blogService.findById(id);

        return ResponseEntity.ok().body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable("id") Long id){
        blogService.delete(id);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/api/articles/{id}")
    //@RequestBody를 통해 HTTP의 body를 Java 객체로 역직렬화
    public ResponseEntity<Article> updateArticle(@PathVariable("id") Long id, @RequestBody UpdateArticleRequest request){
        Article updatedArticle = blogService.update(id, request);

        return ResponseEntity.ok().body(updatedArticle);
    }
}
