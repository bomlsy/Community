package com.school.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class AlphaAspect {

    //定义切点
    //所有的返回值，service包下所有的类，所有的方法，所有的参数
    @Pointcut("execution(* com.school.community.service.*.*(..))")
    public void pointcut() {

    }
    //定义通知
    //在连接点之前打印日志
    @Before("pointcut()")
    public void before() {
        System.out.println("before");
    }
    @After("pointcut()")
    public void after() {
        System.out.println("after");
    }
    //有了返回值以后记录日志
    @AfterReturning("pointcut()")
    public void afterReturning() {
        System.out.println("afterReturning");
    }
    //在抛异常的时候记录日志
    @AfterThrowing("pointcut()")
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }
    //在连接点前后都打印日志
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        System.out.println("aroundbefore");
        Object obj = joinPoint.proceed();//调用需要处理的目标组件
        System.out.println("aroundafter");
        return obj;
    }
}

