package me.yjeong.springbootdeveloper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import me.yjeong.springbootdeveloper.domain.Article;
import me.yjeong.springbootdeveloper.dto.AddArticleRequest;
import me.yjeong.springbootdeveloper.dto.UpdateArticleRequest;
import me.yjeong.springbootdeveloper.repository.BlogRepository;
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

    public Article findById(Long id){
        return blogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
    }

    public void delete(Long id){
        blogRepository.deleteById(id);
    }

    @Transactional // 트랜잭션 메서드를 통해 수정을 보장 (원자성)
    public Article update(Long id, UpdateArticleRequest request){
        Article article = blogRepository.findById(id) //findById를 통해 영속성 컨텍스트에 의한 관리가 시작됨!
                .orElseThrow(() -> new IllegalArgumentException("not found: " + id));
        //영속성 컨텍스트에서 관리 중에 상태가 변하였으므로 JPA의 변경 감지(Dirty Checking)가 실행(상태변화 감지해서 DB 반영)
        //그러나 Transacional로 인해 이 시점이 아니라 메서드가 종료되고 나서 반영
        article.update(request.getTitle(), request.getContent());
        // request의 title과 content가 담긴(수정된 내용을 담은) Article 객체를 반환
        return article;
    }
}
