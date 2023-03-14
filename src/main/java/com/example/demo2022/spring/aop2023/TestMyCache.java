package com.example.demo2022.spring.aop2023;

import com.example.demo2022.spring.aop2023.annotation.EnableMyCache;
import com.example.demo2022.spring.aop2023.annotation.MyCache;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @author : Y
 * @since 2023/3/14 19:18
 */
@EnableMyCache
public class TestMyCache {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // 注册 Configuration Class
        context.register(TestMyCache.class);

        // 启动 Spring 应用上下文
        context.refresh();

        TestMyCache testMyCache = context.getBean("testMyCache", TestMyCache.class);

        for (int i = 0; i < 10; i++) {
            System.out.println(System.currentTimeMillis() + testMyCache.test());
        }


        // 关闭 Spring 应用上下文
        context.close();
    }


    @MyCache("12345")
    public String test() {
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello world!";
    }
}