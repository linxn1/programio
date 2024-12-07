package com.example.programiosso.mapper.user;

import com.example.programiocommon.pojo.dto.UserInfoDTO;
import com.example.programiocommon.pojo.dto.UserTokenDTO;
import org.mapstruct.Mapper;

@Mapper
public interface UserTokenMapper {

    UserTokenDTO findUserByAccount(Integer userAccount);

    void insertUserToken(UserTokenDTO userTokenDTO);

    void deleteUserByAccount(Integer userAccount);
}
