package me.yjeong.springbootdeveloper.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.yjeong.springbootdeveloper.domain.Article;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddArticleRequest {
    @NotNull
    @Size(min = 1, max = 10)
    private String title;

    @NotNull
    private String content;

    public Article toEntity(String author){
        //생성자를 사용해 객체 생성
        return Article.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}
