package hellojpa.domain;

import hellojpa.RoleType;
import hellojpa.domain.embedded.Address;
import hellojpa.domain.embedded.Period;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

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

    @Enumerated(EnumType.STRING)
    private MemberType type;

    @OneToMany(mappedBy = "member") // 연관관계 주인 설정
    private List<Order> orders = new ArrayList<>();

    // 임베디드 타입 적용
    @Embedded
    private Address homeAddress;

    /* 값 타입 컬렉션 */
    /**
     * DB는 값 타입 컬렉션을 같은 테이블에 저장 불가능
     * 따라서 컬렉션을 저장하기 위한 별도 테이블 필요
     * 참고로, 값 타입 컬렉션도 지연로딩 전략 + 영속성전이 및 고아객체 제거 사용
     * (사실상 라이프사이클을 Member에서 관리)
     */
    @ElementCollection
    @CollectionTable(name = "favorite_foods", joinColumns = @JoinColumn(name = "member_id"))
    private Set<String> favoriteFoods = new HashSet<>();

//    @ElementCollection
//    @CollectionTable(name = "address", joinColumns = @JoinColumn(name = "member_id"))
//    private List<Address> addressHistory = new ArrayList<>();

    // 값 타입 컬렉션을 엔티티 리스트로 변환
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private List<AddressEntity>  addressHistory = new ArrayList<>();

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
