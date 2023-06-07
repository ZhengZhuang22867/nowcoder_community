package com.nowcoder.community.controller.intercepter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 演示拦截器的语法和使用。
 */
@Component
public class AlphaIntercepter implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(AlphaIntercepter.class);

    // controller之前执行
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("preHandler:" + handler.toString());
        return true;
    }

    // controller之后和模板引擎之前执行
    // modelAndView：用它的理由，看springmvc前后端交互讲解的 6 7 8 9 10。
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        logger.debug("postHandler:" + handler.toString());
    }

    // 模板引擎执行之后执行，即最后执行
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        logger.debug("afterCompletion:"+handler.toString());
    }
}
