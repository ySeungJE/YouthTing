package skuniv.capstone.domain.login.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import skuniv.capstone.domain.user.User;
import skuniv.capstone.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService{
    private final UserRepository userRepository;

    @Override
    public User login(String email, String password) {
        return userRepository.findByEmail(email).filter(u -> u.getPassword().equals(password)).orElse(null);
    }

}
