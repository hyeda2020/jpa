package hellojpa.domain.embedded;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
@Getter
// @Setter // 불변 객체로 변환하여 중간에 여러 객체가 값 타입을 바꿈으로써 발생하는 Side-Effect 방지
@RequiredArgsConstructor
public class Period {

    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    // 값 타입 비교는 인스턴스가 서로 달라도 내부 필드 값이 동일하면 서로 같은 것으로 봐야 함
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Period period = (Period) o;
        return Objects.equals(startDate, period.startDate) && Objects.equals(endDate, period.endDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }
}
