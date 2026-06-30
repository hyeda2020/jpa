package hellojpa.domain.inheritmapping;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
//@DiscriminatorValue("M") // 싱글 테이블 전략일 경우, 해당 레코드가 영화인지 구분용 값
public class Movie extends Item {

    private String director;
    private String actor;
}
