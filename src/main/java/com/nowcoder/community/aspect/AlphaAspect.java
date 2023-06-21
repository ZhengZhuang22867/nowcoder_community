package com.nowcoder.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {
    // 固定的语句 所有的返回值 service包下所有的类的所有的方法 以及所有的参数
    // pointcut是指所有被增强的方法，所以意思就是：该包下所有的业务组件的所有的方法都会被处理
    @Pointcut("execution(* com.nowcoder.community.service.*.*(..))")
    public void pointcut(){}


    @Before("pointcut()")
    public void before(){
        System.out.println("before");
    }

    @After("pointcut()")
    public void after(){
        System.out.println("after");
    }

    @AfterReturning("pointcut()")
    public void afterReturning(){
        System.out.println("afterReturning");
    }

    @AfterThrowing("pointcut()")
    public void afterThrowing(){
        System.out.println("afterThrowing");
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("around before");
        Object obj = joinPoint.proceed();
        System.out.println("around after");

        return obj;
    }
}
