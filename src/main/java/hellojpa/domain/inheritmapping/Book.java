package hellojpa.domain.inheritmapping;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
//@DiscriminatorValue("B") // 싱글 테이블 전략일 경우, 해당 레코드가 책인지 구분용 값
public class Book extends Item {

    private String author;
    private String isbn;
}
