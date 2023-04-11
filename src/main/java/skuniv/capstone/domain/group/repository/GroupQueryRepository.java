package skuniv.capstone.domain.group.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import skuniv.capstone.domain.group.Group;
import skuniv.capstone.domain.group.QGroup;
import skuniv.capstone.domain.user.Gender;
import skuniv.capstone.domain.user.MBTI;
import skuniv.capstone.domain.user.QUser;

import java.util.List;

import static skuniv.capstone.domain.user.Gender.MAN;
import static skuniv.capstone.domain.user.Gender.WOMAN;

@Repository
@RequiredArgsConstructor
public class GroupQueryRepository {
    private final EntityManager em;
    public List<Group> findAll(Gender gender, Integer headCount, String univAddress) { // 키와 mbti를 설정해서 동적 검색
        JPAQueryFactory query = new JPAQueryFactory(em);
        QGroup group = QGroup.group;

        return query // 성별이 달라야 하고 인원수, 주소가 같아야만 출력
                .select(group)
                .from(group)
                .where( group.userList.size().eq(headCount),
                        genderDif(gender),
                        group.master.univ.univAddress.eq(univAddress),
                        group.idle.eq(true))
                .limit(500)
                .fetch();
    }
    private BooleanExpression genderDif(Gender userGender) {
        if (userGender == MAN) {
            return QGroup.group.master.gender.eq(WOMAN);
        } else {
            return QGroup.group.master.gender.eq(MAN);
        }
    }
}
