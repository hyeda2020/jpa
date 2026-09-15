package hellojpa;

import hellojpa.domain.Member;
import hellojpa.domain.MemberType;
import hellojpa.domain.Team;
import hellojpa.domain.embedded.Address;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.Collection;
import java.util.List;

public class JpqlMain {

    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
        EntityManager em = emf.createEntityManager();

        EntityTransaction tx = em.getTransaction();
        tx.begin();

        try {
            Team teamA = new  Team();
            teamA.setName("Team A");
            em.persist(teamA);

            Team teamB = new  Team();
            teamB.setName("Team B");
            em.persist(teamB);

            Member memberA = new  Member();
            memberA.setName("Member A");
            memberA.setTeam(teamA);
            em.persist(memberA);

            Member memberB = new  Member();
            memberB.setName("Member B");
            memberB.setTeam(teamA);
            em.persist(memberB);

            Member  memberC = new  Member();
            memberC.setName("Member C");
            memberC.setTeam(teamB);
            em.persist(memberC);

            em.flush();
            em.clear();

            String query = "select t From Team t";
            List<Team> resultList = em.createQuery(query, Team.class)
                            .setFirstResult(0)
                            .setMaxResults(2)
                            .getResultList();

            System.out.println(resultList.size());

            for (Team team : resultList) {
                System.out.println("team = " + team.getName() + "|members = " + team.getMembers());
                for (Member member : team.getMembers()) {
                    System.out.println("-> member = " + member);
                }
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }

        emf.close();
    }

    private static void fetchJoinBasic(EntityManager em) {
        Team teamA = new  Team();
        teamA.setName("Team A");
        em.persist(teamA);

        Team teamB = new  Team();
        teamB.setName("Team B");
        em.persist(teamB);

        Member memberA = new  Member();
        memberA.setName("Member A");
        memberA.setTeam(teamA);
        em.persist(memberA);

        Member memberB = new  Member();
        memberB.setName("Member B");
        memberB.setTeam(teamA);
        em.persist(memberB);

        Member  memberC = new  Member();
        memberC.setName("Member C");
        memberC.setTeam(teamB);
        em.persist(memberC);

        em.flush();
        em.clear();

        // 일반 쿼리
        String query = "select m from Member m";
        List<Member> resultList = em.createQuery(query, Member.class).getResultList();
        for (Member member : resultList) {
            System.out.println(member.getName() + ", "+ member.getTeam().getName());
            // 회원A : 팀A(SQL)
            // 회원B : 팀A(1차 캐시)
            // 회원C : 팀B(SQL)

            // 회원 100명이 각가 다른 팀 소속일 경우 -> N + 1 문제 발생
        }

        // 페치 조인
        String fetchJoinQuery = "select m from Member m join fetch m.team";
        List<Member> fetchJoinResultList = em.createQuery(fetchJoinQuery, Member.class).getResultList();
        for (Member member : fetchJoinResultList) {
            // fetch 조인으로 회원과 팀을 함께 조회해서 지연 로딩X
            System.out.println(member.getName() + ", "+ member.getTeam().getName());
        }

        // 일대다 관계, 컬렉션 페치 조인
        String fetchJoinQuery2 = "select t from Team t join fetch t.members";
        List<Team> collectionFetchJoinResultList = em.createQuery(fetchJoinQuery2, Team.class).getResultList();
        for (Team team : collectionFetchJoinResultList) {
            System.out.println(team.getName() + ", " + team.getMembers().size());
            for (Member member : team.getMembers()) {
                System.out.println(member.getName() + ", " + member.getTeam().getName());
            }
        }
    }

    private static void pathExpressionQuery(EntityManager em) {
        Team team = new Team();
        em.persist(team);

        Member member = new Member();
        member.setName("member1");
        member.setTeam(team);
        member.setAge(30);
        em.persist(member);

        em.flush();
        em.clear();

        /* 단일 값 연관 경로로, 묵시적 내부 조인(Member와 Team의 inner join) 발생(실무에서는 사용 자제) */
        String query1 = "select m.team from Member m";
        List<Team> resultTeamList = em.createQuery(query1, Team.class).getResultList();

        /* 컬렉션 값 연관 경로로, 묵시적 내부 조인 발생 X */
//            String query2 = "select t.members from Team t";
        String query3 = "select m.username from Team t join t.members m"; // From절에서 명시적 조인을 통해 별칭을 얻으면 별칭으로 탐색 가능
        List<Collection> resultCollectionList = em.createQuery(query3, Collection.class).getResultList();
    }

    private static void caseQuery(EntityManager em) {
        Member member = new Member();
        member.setName("member1");
        member.setAge(30);

        em.persist(member);

        em.flush();
        em.clear();

        String query =
                "select " +
                        "case when m.age <= 10 then '학생요금' " +
                        "     when m.age >= 60 then '경로요금' " +
                        "     else '일반요금' " +
                        "end" +
                        "from Member m";
        List<String> resultList = em.createQuery(query, String.class).getResultList();
    }

    private static void typeQuery(EntityManager em) {
        Member member = new Member();
        member.setName("member1");
        member.setAge(30);
        member.setType(MemberType.ADMIN);

        em.persist(member);

        em.flush();
        em.clear();

        String query = "select m.username, 'HELLO', true from Member m where m.type = hellojpa.domain.MemberType.ADMIN";
        List<Member> resultList = em.createQuery(query, Member.class)
                .getResultList();
    }

    private static void subQuery(EntityManager em) {
        Member member = new Member();
        member.setName("member1");
        member.setAge(30);

        em.persist(member);

        em.flush();
        em.clear();

        // 나이가 평균보다 많은 회원 조건 서브쿼리 예시
        String subQuery = "select m from Member m where m.age > (select avg(m2.age) from Member m2)";

        List<Member> resultList = em.createQuery(subQuery, Member.class)
                .getResultList();
    }

    private static void joinQuery(EntityManager em) {
        Team team = new Team();
        team.setName("TeamA");
        em.persist(team);

        Member member = new Member();
        member.setName("member1");
        member.setAge(30);
        member.setTeam(team);

        em.persist(member);

        em.flush();
        em.clear();

        String innerJoinQuery = "select m from Member m inner join m.team t"; // 내부 조인
        String leftJoinQuery = "select m from Member m left join m.team t on t.name = 'teamA'"; // (left)아우터 조인 + 조인 대상 필터링
        String thetaJoinQuery = "select m from Member m, Team t where m.username = t.name"; // 세타 조인

        List<Member> resultList = em.createQuery(leftJoinQuery, Member.class)
                .getResultList();
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
