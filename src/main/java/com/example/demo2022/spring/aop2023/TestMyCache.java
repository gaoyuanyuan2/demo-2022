package com.example.demo2022.spring.aop2023;

import com.example.demo2022.spring.aop2023.annotation.EnableMyCache;
import com.example.demo2022.spring.aop2023.annotation.MyCache;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 *
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

        testMyCache.test();

        // 关闭 Spring 应用上下文
        context.close();
    }

    @Bean
    public TaskExecutor taskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(10);
        taskExecutor.setMaxPoolSize(80);
        taskExecutor.setQueueCapacity(100);
        taskExecutor.initialize();//如果不初始化，导致找到不到执行器
        return taskExecutor;
    }

    @MyCache
    public void test(){
        System.out.println(Thread.currentThread().getName());
    }
}