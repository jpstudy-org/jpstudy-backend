package orinnetwork.jpstudy.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class MemberGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 등급 명칭
    @Column(nullable = false, unique = true)
    private String name;

    // 설명
    private String description;

    // 아이콘
    private String iconUrl;

    // 정렬 순서
    private int gradeOrder;
}