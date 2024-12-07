package com.example.programiosso.configuration;

import com.example.programiosso.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public WebSecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()

//                .antMatchers("/user/hello").authenticated()
                .antMatchers().authenticated()

                .anyRequest().permitAll()  // 允许所有请求，不需要登录
                .and()
                .csrf().disable();  // 如果不需要 CSRF 防护，可以禁用
        // 注册 JwtAuthenticationFilter
        http.addFilterBefore(jwtAuthenticationFilter, AbstractPreAuthenticatedProcessingFilter.class);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) { //前端跨域请求
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:51273")  // 允许的前端地址
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true);

    }
}
