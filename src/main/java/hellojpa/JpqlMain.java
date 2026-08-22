package hellojpa;

import hellojpa.domain.Member;
import hellojpa.domain.Team;
import hellojpa.domain.embedded.Address;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class JpqlMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
//            Member member = new Member();
//            member.setName("member1");
//            member.setAge(30);
//            em.persist(member);

            em.flush();
            em.clear();



            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private static void pagingQuery(EntityManager em) {

        for (int i = 0; i < 100; i++) {
            Member member = new Member();
            member.setName("member" + i);
            member.setAge(i);
            em.persist(member);
        }

        em.flush();
        em.clear();

        // 페이징 API 예시
        List<Member> resultList = em.createQuery("select m from Member m order by m.age desc", Member.class)
                .setFirstResult(1)
                .setMaxResults(20)
                .getResultList();

        System.out.println("result.size = " + resultList.size());
        for (Member member : resultList) {
            System.out.println(member);
        }
    }

    private static void projection(EntityManager em) {
        /* 프로젝션 : SELECT 절에 조회할 대상을 지정하는 것  */

        // 엔티티 프로젝션
        List<Member> resultMemberList = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        // 엔티티 프로젝션
        List<Team> resultTeamList = em.createQuery("select t from Member m join m.team t", Team.class)
                .getResultList();

        // 임베디드 타입 프로젝션
        List<Address> resultAddressList = em.createQuery("select m.address from Member m", Address.class)
                .getResultList();

        // 스칼라 타입 프로젝션
        List resultList = em.createQuery("select m.username, m.age from Member m")
                .getResultList();

        // Object 타입을 통한 여러 값 조회
//            for (Object o : resultList) {
//                Object[] obj = (Object[]) o;
//                System.out.println("username : " + obj[0]);
//                System.out.println("age : " + obj[1]);
//            }

        // DTO를 통한 여러 값 조회
        List<MemberDto> resultDtoList = em.createQuery("select new MemberDto(m.username, m.age) from Member m", MemberDto.class)
                .getResultList();

        MemberDto memberDto = resultDtoList.get(0);
        System.out.println("username : " + memberDto.getUsername());
        System.out.println("age : " + memberDto.getAge());
    }

    private static void basicQuery(EntityManager em) {
        Member member = new Member();
        member.setName("member1");
        member.setAge(30);
        em.persist(member);

        TypedQuery<Member> selectMember = em.createQuery("select m from Member m", Member.class);
        TypedQuery<String> selectUsername = em.createQuery("select m.username from Member m where m.id = 10", String.class);
        Query query = em.createQuery("select m.username, m.age from Member m"); // 반환 타입이 명확하지 않을 때에는 Query 사용

        List<Member> resultList = selectMember.getResultList(); // 결과가 없으면 빈 리스트 반환
        Member singleResult = selectMember.getSingleResult(); // 결과가 정확히 하나(단일 개체) 반환 -> 없거나 결과가 둘 이상이면 익셉션 터짐

        // 쿼리문에 파라미터 바인딩
        TypedQuery<Member> parameterQuery = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1");
    }

    private static void queries(EntityManager em) {
        // 일반적인 JPQL 사용 예시 -> 단, 동적 쿼리 작성에 매우 불편함
//            List<Member> memberList = em.createQuery(
//                    "select m from Member m where m.username like '%kim%'", Member.class
//            ).getResultList();

        // Criteria 사용 예시 -> 가독성이 너무 떨어지고 복잡함
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Member> query = cb.createQuery(Member.class);

        Root<Member> m = query.from(Member.class);
        query.select(m).where(cb.equal(m.get("username"), "kim"));
        em.createQuery(query);

        // 실무에서는 QueryDSL 사용 권장

        // Native Query 사용 예시
        List<Member> resultList = em.createNamedQuery("select MEMBER_ID, city, street, zipcode, USERNAME from MEMBER", Member.class)
                .getResultList();
    }

    private static class MemberDto {
        private String username;
        private Integer age;

        public MemberDto() {
        }
        public MemberDto(String name, Integer age) {
            this.username = username;
            this.age = age;
        }

        public String getUsername() {
            return username;
        }

        public Integer getAge() {
            return age;
        }
    }
}
