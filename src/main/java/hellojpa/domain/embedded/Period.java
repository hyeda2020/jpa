package hellojpa.domain.embedded;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Getter
// @Setter // 불변 객체로 변환하여 중간에 여러 객체가 값 타입을 바꿈으로써 발생하는 Side-Effect 방지
@RequiredArgsConstructor
public class Period {

    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
}
