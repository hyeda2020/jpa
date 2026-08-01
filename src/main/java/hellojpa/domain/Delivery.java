package hellojpa.domain;

import hellojpa.domain.embedded.Address;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    // 값 타입 적용
    @Embedded
    private Address address;

    private DeliveryStatus status;

    @OneToOne(mappedBy = "delivery")
    private Order order;
}
