package com.zzl.api.config;

import com.zzl.api.interceptors.AdminTokenInterceptor;
import com.zzl.api.interceptors.PassportInterceptor;
import com.zzl.api.interceptors.UserTokenInterceptor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Bean
    public PassportInterceptor passportInterceptor() {
        return new PassportInterceptor();
    }

    @Bean
    public UserTokenInterceptor userTokenInterceptor() {
        return new UserTokenInterceptor();
    }

    @Bean
    public AdminTokenInterceptor adminTokenInterceptor(){
        return new AdminTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(passportInterceptor()).addPathPatterns("/passport/getSMSCode");
//        registry.addInterceptor(userTokenInterceptor()).addPathPatterns("/user/getAccountInfo").addPathPatterns("/user/updateUserInfo");
//        registry.addInterceptor(adminTokenInterceptor()).addPathPatterns("/admin/adminIsExist").addPathPatterns("/admin/getAdminList");
    }

}
