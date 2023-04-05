package skuniv.capstone.web.room.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import skuniv.capstone.domain.user.service.UserService;

@RestController
@RequiredArgsConstructor
public class RoomController {
    private final UserService userService;



}
