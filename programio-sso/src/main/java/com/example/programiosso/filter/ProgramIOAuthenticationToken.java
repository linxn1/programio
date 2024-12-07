package com.example.programiosso.filter;

import com.example.programiocommon.pojo.dto.UserTokenDTO;
import com.example.programiosso.utils.UserRoleUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class ProgramIOAuthenticationToken implements Authentication {

    private final UserTokenDTO userToken;  // 你自定义的用户Token类
    private boolean authenticated;

    public ProgramIOAuthenticationToken(UserTokenDTO userToken) {
        this.userToken = userToken;
        this.authenticated = true;  // 默认为认证成功
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;  // 认证是否通过
    }

    @Override
    public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        this.authenticated = authenticated;  // 设置认证状态
    }

    @Override
    public Object getPrincipal() {
        return userToken;  // 返回自定义的用户对象
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String permission = userToken.getPermission();
        if (permission.equals("0")) {
            return UserRoleUtils.getUserAuthorities();  // 返回用户的权限/角色信息
        } else if (permission.equals("1")) {
            return UserRoleUtils.getAdminAuthorities();
        }
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;  // 如果有额外的信息可以返回
    }


    @Override
    public String getName() {
        return null;  // 返回用户的用户名
    }
}
