package skuniv.capstone.domain.login.service;


import skuniv.capstone.domain.user.User;

public interface LoginService {
    public User login(String Id, String password);
}
