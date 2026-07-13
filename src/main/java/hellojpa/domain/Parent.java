package hellojpa.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Parent {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "parent"
            , cascade = CascadeType.ALL // 연관된 엔티티에 대해 영속성 전이
            , orphanRemoval = true // 부모 엔티티와 연관관계가 끊어진 자식 엔티티 자동 삭제
    ) // 두 옵션 활성화 시, 부모 엔티티를 통해 자식의 생명 주기 관리 가능
    private List<Child> childList = new ArrayList<>();

    public void addChild(Child child) {
        childList.add(child);
        child.setParent(this);
    }
}
