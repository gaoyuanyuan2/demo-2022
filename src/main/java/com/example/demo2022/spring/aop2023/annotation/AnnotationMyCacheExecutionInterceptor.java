/*
 * Copyright 2002-2017 the original author or authors.
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

import com.example.demo2022.spring.aop2023.interceptor.MyCacheExecutionInterceptor;
import com.example.demo2022.spring.aop2023.interceptor.MyCacheUncaughtExceptionHandler;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;


public class AnnotationMyCacheExecutionInterceptor extends MyCacheExecutionInterceptor {


    public AnnotationMyCacheExecutionInterceptor() {
        super();
    }


    public AnnotationMyCacheExecutionInterceptor(MyCacheUncaughtExceptionHandler exceptionHandler) {
        super(exceptionHandler);
    }


    @Override
    @Nullable
    protected String getExecutorQualifier(Method method) {
        MyCache myCache = AnnotatedElementUtils.findMergedAnnotation(method, MyCache.class);
        if (myCache == null) {
            myCache = AnnotatedElementUtils.findMergedAnnotation(method.getDeclaringClass(), MyCache.class);
        }
        return (myCache != null ? myCache.value() : null);
    }

}
