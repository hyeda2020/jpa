package hellojpa.domain.inheritmapping;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
//@DiscriminatorValue("A") // 싱글 테이블 전략일 경우, 해당 레코드가 앨범인지 구분용 값
public class Album extends Item {

    private String artist;
}
