package com.example.programiosso.service;

import com.example.programiocommon.pojo.dto.UserInfoDTO;
import com.example.programiocommon.pojo.dto.UserTokenDTO;
import com.example.programiocommon.pojo.to.UserInfoTO;
import com.example.programiocommon.pojo.to.UserLoginTO;
import com.example.programiosso.utils.JwtUtils;
import com.example.programiosso.mapper.user.UserInfoMapper;
import com.example.programiosso.mapper.user.UserTokenMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Service
public class UserLoginService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Autowired
    private UserTokenMapper userTokenMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;  // 注入密码加密和校验工具

    public Boolean userRegister(UserInfoTO userInfoTO) {
        Integer userAccount = userInfoTO.getUserAccount();
        Integer countNum = userInfoMapper.countUserByUsername(userAccount);
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        if (countNum.equals(0)) {
            BeanUtils.copyProperties(userInfoTO, userInfoDTO);
            System.out.println(userInfoDTO);
            userInfoMapper.insertUser(userInfoDTO);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public String userLogin(UserLoginTO userLoginDTO) {
        // 1. 查找用户是否存在
        System.out.println(userLoginDTO);
        UserInfoDTO user = userInfoMapper.findByUserAccount(userLoginDTO.getUserAccount());
        if (user == null) { // 改为 null 检查
            throw new RuntimeException("用户不存在");
        }

        // 比对密码
        if (user.getPassword() == null || userLoginDTO.getPassword() == null ||
                !passwordEncoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 3. 生成JWT令牌
        String token = JwtUtils.generate(user.getUserAccount());

        // 4. 获取当前时间和过期时间
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime expiresAtLocalDateTime = localDateTime.plusHours(24);
        Timestamp expiresAt = Timestamp.valueOf(expiresAtLocalDateTime);

        // 5. 查询当前用户的 Token 信息
        UserTokenDTO existingToken = userTokenMapper.findUserByAccount(user.getUserAccount());

        if (existingToken != null) {
            // 如果 Token 存在，检查是否过期
            if (existingToken.getExpiresAt().before(Timestamp.valueOf(localDateTime))) {
                // 如果 Token 已过期，更新 Token 和过期时间
                existingToken.setToken(token);
                existingToken.setExpiresAt(expiresAt);
                userTokenMapper.updateUserToken(existingToken);
            }
        } else {
            // 如果 Token 不存在，新增 Token
            UserTokenDTO userTokenDTO = new UserTokenDTO();
            userTokenDTO.setUserAccount(user.getUserAccount());
            userTokenDTO.setToken(token);
            userTokenDTO.setExpiresAt(expiresAt);
            userTokenDTO.setPermission(user.getPermission());

            userTokenMapper.insertUserToken(userTokenDTO);
        }

        return token;
    }



    public void userLogout(Integer userAccount) {
        userTokenMapper.deleteUserByAccount(userAccount);
    }
}
