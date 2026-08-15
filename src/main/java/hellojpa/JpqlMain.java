package hellojpa;

import hellojpa.domain.Member;
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

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
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
}
