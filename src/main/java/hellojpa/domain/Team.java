package hellojpa.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Team extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    private String name;

    /**
     * 외래키가 있는 Member.team 을 연관관계 매핑의 주인으로 설정
     * 보통 DB의 다대일/일대다 중에서 '다'(Many) 쪽, 즉 외래키가 있는 쪽이 연관관계의 주인이 되는 것이 바람직한 구조
     * 이를 통해 Member 클래스의 team 을 바꾸면 Member 테이블의 값이 바뀐다고 바로 쉽게 이해할 수 있음
     *  */
    @OneToMany(mappedBy = "team")
    private List<Member> members = new ArrayList<>();
}
