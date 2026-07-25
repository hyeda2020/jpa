package hellojpa.domain.embedded;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Getter @Setter
@RequiredArgsConstructor
public class Period {

    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
