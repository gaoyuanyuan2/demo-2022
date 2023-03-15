/*
 * Copyright 2002-2020 the original author or authors.
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

package com.example.demo2022.spring.aop2023.config;

import com.example.demo2022.spring.aop2023.annotation.EnableMyCache;
import com.example.demo2022.spring.aop2023.annotation.MyCacheAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;

/**
 * MyCacheAnnotationBeanPostProcessor 配置
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ProxyMyCacheConfiguration extends AbstractMyCacheConfiguration {

    @Bean(name = CacheManagementConfigUtils.CACHE_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public MyCacheAnnotationBeanPostProcessor myCacheAdvisor() {
        Assert.notNull(this.enableMyCacheAttributes, "@EnableMyCache annotation metadata was not injected");
        MyCacheAnnotationBeanPostProcessor bpp = new MyCacheAnnotationBeanPostProcessor();
        bpp.configure(this.exceptionHandler);
        Class<? extends Annotation> customMyCacheAnnotation = this.enableMyCacheAttributes.getClass("annotation");
        if (customMyCacheAnnotation != AnnotationUtils.getDefaultValue(EnableMyCache.class, "annotation")) {
            bpp.setMyCacheAnnotationType(customMyCacheAnnotation);
        }
        bpp.setProxyTargetClass(this.enableMyCacheAttributes.getBoolean("proxyTargetClass"));
        bpp.setOrder(this.enableMyCacheAttributes.<Integer>getNumber("order"));
        return bpp;
    }

}
