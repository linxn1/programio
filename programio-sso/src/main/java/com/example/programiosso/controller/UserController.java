package com.example.programiosso.controller;

import com.example.programiocommon.pojo.to.UserInfoTO;
import com.example.programiocommon.pojo.to.UserLoginTO;
import com.example.programiosso.mapper.user.UserInfoMapper;
import com.example.programiosso.service.UserLoginService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Api(tags = "用户模块")
public class UserController {

    @Autowired
    private UserLoginService userLoginService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<UserInfoTO> userRegister(@RequestBody UserInfoTO userInfoTO) {
        // 对密码进行加密处理(照理说这里应该是在用户端就加密了这里只是为了测试)
        String hashedPassword = passwordEncoder.encode(userInfoTO.getPassword());
        userInfoTO.setPassword(hashedPassword);  // 将加密后的密码设置回 DTO

        Boolean hasRegistered = userLoginService.userRegister(userInfoTO);
        if (hasRegistered) {
            return ResponseEntity.status(HttpStatus.CREATED).body(userInfoTO);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userInfoTO);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginTO> userLogin(@RequestBody UserLoginTO userLoginTO) {
        System.out.println(userInfoMapper.findByUserAccount(userLoginTO.getUserAccount()));
        String jwt = userLoginService.userLogin(userLoginTO);
        System.out.println(jwt);
        return ResponseEntity.status(HttpStatus.CREATED).body(userLoginTO);
    }

    @PostMapping("/logout")
    public void userLogout(@RequestBody UserLoginTO userLoginTO) {
        Integer userAccount = userLoginTO.getUserAccount();
        userLoginService.userLogout(userAccount);
    }


    @Autowired
    private UserInfoMapper userInfoMapper;

    @GetMapping("/hello")// 用于测试
    public String hello() {
        return "Hello, world!";
    }
}