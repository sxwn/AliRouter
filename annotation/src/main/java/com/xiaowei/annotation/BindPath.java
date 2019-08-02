package com.xiaowei.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)//声明注解的生命周期  编译期
@Target(ElementType.TYPE)//声明我们注解的作用域 也就是放在什么上面，TYPE就是放在当前类上
public @interface BindPath {
    String value();
}
