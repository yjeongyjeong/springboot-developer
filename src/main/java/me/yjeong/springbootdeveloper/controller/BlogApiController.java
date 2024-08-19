package me.yjeong.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.yjeong.springbootdeveloper.domain.Article;
import me.yjeong.springbootdeveloper.dto.AddArticleRequest;
import me.yjeong.springbootdeveloper.dto.ArticleResponse;
import me.yjeong.springbootdeveloper.service.BlogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController //@Controller + @ResponseBody(HTTP의 body -> Java객체)
public class BlogApiController {

    private final BlogService blogService;

    //HTTP 메서드가 POST일 때, 전달받은 URL과 동일하면 메서드로 매핑
    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request){
        Article savedArticle = blogService.save(request);
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
}
