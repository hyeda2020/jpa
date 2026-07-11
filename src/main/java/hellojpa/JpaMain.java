package hellojpa;

import hellojpa.domain.Member;
import hellojpa.domain.Team;
import hellojpa.domain.inheritmapping.Movie;
import jakarta.persistence.*;
import org.hibernate.Hibernate;

import java.util.List;

public class JpaMain {

    public static void main(String[] args) {

        // 엔티티 매니저 팩토리는 하나만 생성해서 애플리케이션 전체 공유
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager(); // 엔티티 매니저는 쓰레드간 공유 X (사용하고 바로 close)

        EntityTransaction tx = em.getTransaction(); // JPA의 모든 데이터 변경은 트랜잭션 내에서 실행
        tx.begin();

        //code
        try {
//            contextTest(em);
//            proxytest(emf, em);

            Team team = new Team();
            team.setName("teamA");

            Member member1 = new Member();
            member1.setName("member1");
            member1.setTeam(team);
            em.persist(member1);

            em.flush();
            em.clear();

//            Member m = em.find(Member.class, member1.getId());
//            System.out.println(m.getTeam().getName()); // 초기화

            List<Member> members = em.createQuery("select m from Member m join fetch m.team",  Member.class)
                    .getResultList();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    private static void proxytest(EntityManagerFactory emf, EntityManager em) {
        Member member = new  Member();
        member.setName("hello");

        em.persist(member);

        em.flush();
        em.clear();

//            Member member1 = em.find(Member.class,member.getId());
//            System.out.println("member1 = " + member1.getClass());

        /* 영속성 컨텍스트에 찾는 엔티티가 있으면 실제 엔티티 반환 */
//            Member memberRef = em.getReference(Member.class,member1.getId());
//            System.out.println("reference = " + memberRef.getClass());
//
//            System.out.println("member1 == reference = " + (member1 == memberRef));


        Member memberRef = em.getReference(Member.class,member.getId()); // Proxy
        System.out.println("memberRef = " + memberRef.getClass());
        Hibernate.initialize(memberRef); // 프록시 강제 초기화(단, JPA 표준 기능은 아님)
        // 프록시 인스턴스의 초기화 여부 반환
        System.out.println("memberRef is loaded = " + emf.getPersistenceUnitUtil().isLoaded(memberRef));

        Member findMember = em.find(Member.class,member.getId()); // Member
        System.out.println("findMember = " + findMember.getClass());

        // JPA는 두 객체가 동일함을 보장해야 되므로, 실제 Member를 조회해도 Proxy를 반환
        System.out.println("memberRef == findMember : " + (memberRef == findMember));

        em.clear(); // 영속성 컨텍스트 초기화

        /*
         * 더이상 영속성 컨텍스트의 도움을 받을 수 없는
         * 준영속 상태에서는 프록시를 초기화 할 경우 예외 발생
         * */
        String name = memberRef.getName();
    }

    private static void equalsLogic(Member member1, Member member2) {
        /* 파라미터로 넘어오는 Member가 프록시인지 알 수 없기 때문에 instanceof 로 비교해야 됨 */
//        System.out.println(member1.getName() == member2.getName());
        System.out.println(member1 instanceof Member);
        System.out.println(member2 instanceof Member);
    }

    /* 영속성 컨텍스트 테스트 */
    private static void contextTest(EntityManager em) {

        Member memberA = new Member();
        memberA.setName("MemberA");

        em.persist(memberA);

        Member findMemberA = em.find(Member.class, 1L);
        System.out.println("find : " + findMemberA.getName());

        findMemberA.setName("MemberB"); // Setter 로 업데이트 하면 바로 DB에 반영(변경 감지)

        Member findMemberB = em.find(Member.class, 1L);
        System.out.println("find : " + findMemberB.getName());

        // JPQL 활용 예시(JPQL 은 엔티티 객체를 대상으로 한 쿼리)
        // JPA는 SQL을 추상화한 JPQL 이라는 객체 지향 쿼리 언어 제공
        //  List<Member> memberList = em.createQuery("select m from Member m", Member.class)
        //        .getResultList();

        //  for (Member m : memberList) {
        //    System.out.println("memberList : " + m.getName());
        //  }


        /* 양방향 연관관계 매핑 테스트 */
        Team team = new Team();
        team.setName("TeamA");
        em.persist(team);

        Member member = new Member();
        member.setName("member1");
//            member.setTeam(team); // 반드시 연관관계 주인에 값을 세팅해줘야 매핑됨

        // 단, 객체지향 관계를 고려하면 반대쪽(비주인)에도 값을 세팅해주는 것이 바람직
        // 즉, 양쪽 모두에 값을 세팅해주는 것이 좋음
//            team.getMembers().add(member);

        // 연관관계 편의 메서드 활용
        member.changeTeam(team);

        em.persist(member);
        em.flush();

        Member findMember = em.find(Member.class, memberA.getId());
        List<Member> members = findMember.getTeam().getMembers();

        for (Member m : members) {
            System.out.println(m.getName());
        }
    }
}
