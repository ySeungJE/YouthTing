package skuniv.capstone.domain.userrequest;

import jakarta.persistence.*;
import lombok.*;
import skuniv.capstone.domain.request.Request;
import skuniv.capstone.domain.user.User;

import static jakarta.persistence.FetchType.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor @Builder
public class UserRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_request_id")
    private Long id;
    @ManyToOne(fetch = LAZY)
    private User sendUser;
    @ManyToOne(fetch = LAZY)
    private User receiveUser;
    @OneToOne
    @JoinColumn(name = "request_id")
    private Request request;
}
