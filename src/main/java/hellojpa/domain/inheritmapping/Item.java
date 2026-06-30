package hellojpa.domain.inheritmapping;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
//@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // JPA 기본 전략은 싱글테이블 전략
@Inheritance(strategy = InheritanceType.JOINED)
// 싱글 테이블 전략일 경우, 해당 레코드가 앨범인지, 영화인지, 책인지 구분하는 값 용도로 사용
//@DiscriminatorColumn(name = "D_TYPE")
public abstract class Item {

    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private int price;
}
