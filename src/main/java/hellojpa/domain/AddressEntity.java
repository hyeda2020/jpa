package hellojpa.domain;

import hellojpa.domain.embedded.Address;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class AddressEntity {

    @Id @GeneratedValue
    private Long id;

    // 엔티티로 값 타입 Wrapping
    private Address address;

    public AddressEntity() {
    }

    public AddressEntity(String city, String street, String zipcode) {
        this.address = new Address(city, street, zipcode);
    }
}
