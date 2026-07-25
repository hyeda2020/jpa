package hellojpa.domain.embedded;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter @Setter
@RequiredArgsConstructor
public class Address {

    private String city;
    private String street;
    private String zipcode;
}
