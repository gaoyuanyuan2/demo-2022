/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo2022.spring.aop2023.annotation;

import com.example.demo2022.spring.aop2023.interceptor.MyCacheUncaughtExceptionHandler;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.function.SingletonSupplier;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 自定义缓存 Advisor（存储advice 和 pointcut）
 */
@SuppressWarnings("serial")
public class MyCacheAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

    private Advice advice;

    private Pointcut pointcut;


    public MyCacheAnnotationAdvisor() {
        this((Supplier<MyCacheUncaughtExceptionHandler>) null);
    }


    public MyCacheAnnotationAdvisor(
            @Nullable MyCacheUncaughtExceptionHandler exceptionHandler) {
        this(SingletonSupplier.ofNullable(exceptionHandler));
    }


    @SuppressWarnings("unchecked")
    public MyCacheAnnotationAdvisor(@Nullable Supplier<MyCacheUncaughtExceptionHandler> exceptionHandler) {
        Set<Class<? extends Annotation>> myCacheAnnotationTypes = new LinkedHashSet<>(2);
        myCacheAnnotationTypes.add(MyCache.class);
        this.advice = buildAdvice(exceptionHandler);
        this.pointcut = buildPointcut(myCacheAnnotationTypes);
    }


    public void setMyCacheAnnotationType(Class<? extends Annotation> myCacheAnnotationType) {
        Assert.notNull(myCacheAnnotationType, "'myCacheAnnotationType' must not be null");
        Set<Class<? extends Annotation>> myCacheAnnotationTypes = new HashSet<>();
        myCacheAnnotationTypes.add(myCacheAnnotationType);
        this.pointcut = buildPointcut(myCacheAnnotationTypes);
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        if (this.advice instanceof BeanFactoryAware) {
            ((BeanFactoryAware) this.advice).setBeanFactory(beanFactory);
        }
    }


    @Override
    public Advice getAdvice() {
        return this.advice;
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }


    /**
     * 构建Advice执行器
     *
     * @param exceptionHandler 异常处理
     * @return Advice执行器
     */
    protected Advice buildAdvice(@Nullable Supplier<MyCacheUncaughtExceptionHandler> exceptionHandler) {
        AnnotationMyCacheExecutionInterceptor interceptor = new AnnotationMyCacheExecutionInterceptor(null);
        interceptor.configure(exceptionHandler);
        return interceptor;
    }


    /**
     * 解析注解 获取Pointcut 拦截条件
     *
     * @param myCacheAnnotationTypes
     * @return Pointcut
     */
    protected Pointcut buildPointcut(Set<Class<? extends Annotation>> myCacheAnnotationTypes) {
        ComposablePointcut result = null;
        for (Class<? extends Annotation> myCacheAnnotationType : myCacheAnnotationTypes) {
            Pointcut cpc = new AnnotationMatchingPointcut(myCacheAnnotationType, true);
            Pointcut mpc = new AnnotationMatchingPointcut(null, myCacheAnnotationType, true);
            if (result == null) {
                result = new ComposablePointcut(cpc);
            } else {
                result.union(cpc);
            }
            result = result.union(mpc);
        }
        return (result != null ? result : Pointcut.TRUE);
    }

}
