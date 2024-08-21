package me.yjeong.springbootdeveloper.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor // 기본 생성자
@AllArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 기본키를 자동으로 1씩 증가
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @CreatedDate    //엔티티 생성 시 생성시간 저장
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate   //엔티티 수정 시 수정시간 저장
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder // 빌더 패턴으로 객체 생성
    public Article(String title, String content){
        this.title = title;
        this.content = content;
    }
    
    // update용 메서드
    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }

}
