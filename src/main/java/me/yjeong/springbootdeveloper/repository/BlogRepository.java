package me.yjeong.springbootdeveloper.repository;

import me.yjeong.springbootdeveloper.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Article, Long> {
}
