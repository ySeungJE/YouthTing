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
    public List<Group> findAll(Gender gender, Integer headCount, String univAddress, GroupSearch groupSearch) { // 키와 mbti를 설정해서 동적 검색
        JPAQueryFactory query = new JPAQueryFactory(em);
        QGroup group = QGroup.group;

        return query // 성별이 달라야 하고 인원수, 주소가 같아야만 출력
                .select(group)
                .from(group)
                .where(group.userList.size().eq(headCount),
                        genderDif(gender),
                        univNameEq(groupSearch.getUnivName()),
//                        group.master.univ.univAddress.eq(univAddress),
                        ageRange(groupSearch.getGroupAgeMin(), groupSearch.getGroupAgeMax()),
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
    private BooleanExpression ageRange(Integer ageMin, Integer ageMax) {
        if (ageMin==null && ageMax==null){
            ageMin=0; ageMax=35;
        } else if (ageMin!=null && ageMax==null) {
            ageMax=35;
        } else if (ageMin==null && ageMax!=null) {
            ageMin=0;
        }
        return QGroup.group.groupAge.between(ageMin,ageMax);
    }
    private BooleanExpression univNameEq(String univName) {
        if (univName == null) {
            return null;
        }
        return QGroup.group.master.univ.univName.eq(univName);
    }

}
