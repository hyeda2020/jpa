package hellojpa.domain;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@MappedSuperclass
@Getter @Setter
public class BaseEntity { // 엔티티 X, 테이블과 매핑 X, 조회/검색 불가

    /* 도메인 전체적으로 공통적으로 사용하는 컬럼 */
    private String createdBy;
    private Date createdDate;
    private String updatedBy;
    private Date updatedDate;
}
