package com.xh.blog.commom.aop;

import java.lang.annotation.*;

@Target({ElementType.TYPE,ElementType.METHOD})
//Target注解：标注“被标注的注解”可以出现在哪些位置上
//type:表示可以放在类上面 method表示可以放在方法上
@Retention(RetentionPolicy.RUNTIME)
//Retention注解：用来“被标注的注解”最终保存在哪里
//RUNTIME 表示保存在class文件中，并且可以被反射机制读取
@Documented
//在自定义注解的时候可以使用@Documented来进行标注，
//如果使用@Documented标注了，在生成javadoc的时候就会把@Documented注解给显示出来。
public @interface LogAnnotation {

    String module() default "";

    String operator() default "";
}
