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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.framework.autoproxy.AbstractBeanFactoryAwareAdvisingPostProcessor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.function.SingletonSupplier;

import java.lang.annotation.Annotation;
import java.util.function.Supplier;

/**
 * Advisor 缓存后置处理器
 */

@SuppressWarnings("serial")
public class MyCacheAnnotationBeanPostProcessor extends AbstractBeanFactoryAwareAdvisingPostProcessor {


    protected final Log logger = LogFactory.getLog(getClass());

    @Nullable
    private Supplier<MyCacheUncaughtExceptionHandler> exceptionHandler;

    @Nullable
    private Class<? extends Annotation> myCacheAnnotationType;


    public MyCacheAnnotationBeanPostProcessor() {
        setBeforeExistingAdvisors(true);
    }


    public void configure(@Nullable Supplier<MyCacheUncaughtExceptionHandler> exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }


    public void setExceptionHandler(MyCacheUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = SingletonSupplier.of(exceptionHandler);
    }


    public void setMyCacheAnnotationType(Class<? extends Annotation> myCacheAnnotationType) {
        Assert.notNull(myCacheAnnotationType, "'myCacheAnnotationType' must not be null");
        this.myCacheAnnotationType = myCacheAnnotationType;
    }


    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);

        MyCacheAnnotationAdvisor advisor = new MyCacheAnnotationAdvisor(this.exceptionHandler);
        if (this.myCacheAnnotationType != null) {
            advisor.setMyCacheAnnotationType(this.myCacheAnnotationType);
        }
        advisor.setBeanFactory(beanFactory);
        this.advisor = advisor;
    }

}
