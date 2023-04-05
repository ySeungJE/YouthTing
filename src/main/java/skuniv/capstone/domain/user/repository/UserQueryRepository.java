package skuniv.capstone.domain.user.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import skuniv.capstone.domain.user.Gender;
import skuniv.capstone.domain.user.MBTI;
import skuniv.capstone.domain.user.QUser;
import skuniv.capstone.domain.user.User;

import java.util.List;

import static skuniv.capstone.domain.user.Gender.*;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository {
    private final EntityManager em;
    public List<User> findAll(Gender userGender,UserSearch userSearch) { // 키와 mbti를 설정해서 동적 검색
        JPAQueryFactory query = new JPAQueryFactory(em);
        QUser user = QUser.user;

        return query // 문제는 여기서, 내가 남자면 여자만 나와야 하고 여자면 남자만 나와야 한다
                .select(user)
                .from(user)
                .where(mbtiEq(userSearch.getMbti()),
                        heightRange(userSearch.getHeightMin(), userSearch.getHeightMax()),
                        QUser.user.idle.eq(true),
                        genderDif(userGender))
                .limit(1000)
                .fetch();
    }

    private BooleanExpression mbtiEq(MBTI mbti) {
        if (mbti == null) {
            return null;
        }
        return QUser.user.mbti.eq(mbti);
    }
    private BooleanExpression idleEp(Boolean idle) {
        if (idle == false) {
            return null;
        }
        return QUser.user.idle.eq(true);
    }
    private BooleanExpression genderDif(Gender userGender) {
        if (userGender == MAN) {
            return QUser.user.gender.eq(WOMAN);
        } else {
            return QUser.user.gender.eq(MAN);
        }
    }
    private BooleanExpression heightRange(Integer heightMin, Integer heightMax) {
        if (heightMin==null && heightMax==null){
            heightMin=0; heightMax=300;
        } else if (heightMin!=null && heightMax==null) {
            heightMax=300;
        } else if (heightMin==null && heightMax!=null) {
            heightMin=0;
        }
        return QUser.user.height.between(heightMin,heightMax);
    }

}
