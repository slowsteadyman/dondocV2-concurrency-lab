package com.dondoc.controller;

import com.dondoc.dto.Users;
import com.dondoc.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService =userService;
    }

    @GetMapping
    public List<Users> getUsers(){
        return userService.getUsers();
    }

    // PostMapping - POST 요청을 받는 엔드포인트
    // @RequestBody - 요청 body의 JSON을 Users 객체로 변환
//    @PostMapping
//    public void createUser(@RequestBody Users user){
//        userService.createUser(user);
//    }
}
