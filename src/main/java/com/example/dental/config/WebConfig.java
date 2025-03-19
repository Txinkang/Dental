package com.example.dental.config;

import com.example.dental.interceptors.CheckTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

    
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private CheckTokenInterceptor checkTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> excludePathList=new ArrayList<>();
        excludePathList.add("/user/register");
        excludePathList.add("/user/login");
        excludePathList.add("/user/logout");
        excludePathList.add("/admin/login");
        excludePathList.add("/admin/logout");
        excludePathList.add("/error");
        // 添加图片路径到排除列表
        excludePathList.add("/images/**");  // 排除所有图片路径
        excludePathList.add("/static/**");
        registry.addInterceptor(checkTokenInterceptor).addPathPatterns("/**").excludePathPatterns(excludePathList);
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
    
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(5000);
    
        restTemplate.setRequestFactory(requestFactory);
        
        return restTemplate;
    }
}
