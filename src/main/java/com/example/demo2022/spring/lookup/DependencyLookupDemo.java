package com.example.demo2022.spring.lookup;

import com.example.demo2022.domain.User;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 依赖查找
 *
 * @author : Y
 * @since 2022/12/21 21:23
 */
public class DependencyLookupDemo {
    public static void main(String[] args) {
        // 配置 XML 配置文件
        // 启动 Spring 应用上下文
        BeanFactory beanFactory = new ClassPathXmlApplicationContext("classpath:/META-INF/dependency-lookup-context.xml");
        lookupInLazy(beanFactory);
    }
    private static void lookupInLazy(BeanFactory beanFactory) {
        ObjectFactory<User> objectFactory = (ObjectFactory<User>) beanFactory.getBean("objectFactory");
        User user = objectFactory.getObject();
        System.out.println("延迟查找：" + user);
    }

}