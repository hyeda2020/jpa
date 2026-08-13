package hellojpa;

import hellojpa.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
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

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }
}
