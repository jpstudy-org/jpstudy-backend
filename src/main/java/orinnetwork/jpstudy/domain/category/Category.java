package orinnetwork.jpstudy.domain.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private Category(String name) {
        this.name = name;
    }

    // TODO: 점검하는데 이거 카테고리 로직 안만들어져있음. 팩토리 메서드 적용은 하는데 수정 필요 (버그는 없으니 일단 체크만 해두기)
    public static Category create(String name) {
        return new Category(name);
    }
}
