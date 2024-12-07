package com.example.programiosso.mapper.user;

import com.example.programiocommon.pojo.dto.UserInfoDTO;
import org.mapstruct.Mapper;

@Mapper
public interface UserInfoMapper {

    public Integer countUserByUsername(Integer userAccount);

    public void insertUser(UserInfoDTO userInfoDTO);

    UserInfoDTO findByUserAccount(Integer userAccount);
}
