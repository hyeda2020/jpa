package hellojpa.domain;

import hellojpa.RoleType;
import hellojpa.domain.embedded.Address;
import hellojpa.domain.embedded.Period;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "name") // 컬럼 매핑
    private String name;

    @ManyToOne(fetch =  FetchType.LAZY) // 지연로딩 LAZY 를 사용해서 프록시 조회
//    @ManyToOne(fetch =  FetchType.EAGER) // 즉시로딩 사용(Member 조회 시 항상 Team도 조회하는 경우)
    @JoinColumn(name = "team_id") // Team 클래스의 team_id 컬럼과 조인
    private Team team;

    @OneToMany(mappedBy = "member") // 연관관계 주인 설정
    private List<Order> orders = new ArrayList<>();

    // 임베디드 타입 적용
    @Embedded
    private Address homeAddress;

    // 동일 타입 임베디드 타입 추가 적용 예시
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "city",
                column=@Column(name = "WORK_CITY")),
            @AttributeOverride(name = "street",
                column=@Column(name = "WORK_STREET")),
            @AttributeOverride(name = "zipcode",
                column=@Column(name = "WORK_ZIPCODE"))
    })
    private Address workAddress;

    @Embedded
    private Period workPeriod;

    private Integer age;

    @Enumerated(EnumType.STRING) // enum 타입 매핑
    private RoleType roleType;

//    @Temporal(TemporalType.TIMESTAMP) // 날짜 타입 매핑
//    private Date createdDate;

//    @Temporal(TemporalType.TIMESTAMP)
//    private Date lastModifiedDate;

    @Lob // BLOB, CLOB 매핑
    private String description;

//    @Transient // 특정 필드를 컬럼에 매핑하지 않음(매핑 무시)
//    private String temp;

    // 연관관계 편의 메서드 선언하여 활용
    // (단, 이러한 편의 메서드는 주인/비주인 중 하나에만 선언하기를 권장)
    public void changeTeam(Team team) {
        this.team = team;
        team.getMembers().add(this);
    }
}
