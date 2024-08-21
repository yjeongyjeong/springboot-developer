package me.yjeong.springbootdeveloper.controller;

import lombok.RequiredArgsConstructor;
import me.yjeong.springbootdeveloper.domain.Article;
import me.yjeong.springbootdeveloper.dto.ArticleListViewResponse;
import me.yjeong.springbootdeveloper.dto.ArticleViewResponse;
import me.yjeong.springbootdeveloper.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BlogViewController {
    private final BlogService blogService;

    @GetMapping("/articles")
    public String getArticles(Model model){
        List<ArticleListViewResponse> articles = blogService.findAll().stream()
                .map(ArticleListViewResponse::new) // article -> new ArticleListViewResponse(article) 와 동일
                .toList();

        model.addAttribute("articles", articles);

        return "articleList"; // resource/templates/articleList.html
    }
    @GetMapping("/articles/{id}")
    public String getArticle(@PathVariable("id") Long id, Model model){
        Article article = blogService.findById(id);

        model.addAttribute("article", new ArticleViewResponse(article));

        return "article"; // resource/templates/article.html
    }
}
