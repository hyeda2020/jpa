package hellojpa;

import hellojpa.domain.Member;
import hellojpa.domain.Team;
import hellojpa.domain.inheritmapping.Movie;
import jakarta.persistence.*;

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

//            Movie movie = new Movie();
//            movie.setDirector("AAA");
//            movie.setActor("BBB");
//            movie.setName("바람과 함께 사라지다");
//            movie.setPrice(10000);
//
//            em.persist(movie);
//
//            em.flush();
//            em.clear();
//
//            Movie findMovie = em.find(Movie.class, movie.getId());
//            System.out.println("findMovie = " + findMovie);



            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
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
