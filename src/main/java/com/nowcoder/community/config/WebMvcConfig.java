package com.nowcoder.community.config;

import com.nowcoder.community.controller.intercepter.AlphaIntercepter;
import com.nowcoder.community.controller.intercepter.LoginTicketIntercepter;
import com.nowcoder.community.dao.LoginTicketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 之前设置一个 Config 类是为了声明一个第三方的 bean，例如：KaptchaConfig。但是拦截器的逻辑不一样，它要求实现一个接口而不是装配一个 bean。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    // 用于学习拦截器的使用，没有具体的业务逻辑实现
    @Autowired
    private AlphaIntercepter alphaIntercepter;

    @Autowired
    private LoginTicketIntercepter loginTicketIntercepter;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 用于学习拦截器的使用，没有具体的业务逻辑实现
        registry.addInterceptor(alphaIntercepter)
                // 排除需要拦截的路径，比如：静态资源
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png","/**/*.jpg", "/**/*.jpeg")
                // 指明需要拦截的路径
                .addPathPatterns("/register", "/login", "");

        // LoginTicketInterception业务逻辑实现
        registry.addInterceptor(loginTicketIntercepter)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.png","/**/*.jpg", "/**/*.jpeg");

    }


}
