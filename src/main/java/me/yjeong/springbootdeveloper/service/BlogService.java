package me.yjeong.springbootdeveloper.service;

import lombok.RequiredArgsConstructor;
import me.yjeong.springbootdeveloper.domain.Article;
import me.yjeong.springbootdeveloper.dto.AddArticleRequest;
import me.yjeong.springbootdeveloper.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // 빈으로 등록(=> @Component)
@RequiredArgsConstructor // final, NotNull이 붙은 필드의 생성자 추가
public class BlogService {
    private final BlogRepository blogRepository;

    public Article save(AddArticleRequest request){
        return blogRepository.save(request.toEntity());
    }

    public List<Article> findAll(){
        return blogRepository.findAll();
    }
}
