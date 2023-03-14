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

package com.example.demo2022.spring.aop2023.interceptor;

import com.example.demo2022.spring.aop2023.annotation.MyCache;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.interceptor.AsyncExecutionInterceptor;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * {@link AsyncExecutionInterceptor}
 */

public class MyCacheExecutionInterceptor extends MyCacheExecutionAspectSupport implements MethodInterceptor, Ordered {


    public MyCacheExecutionInterceptor() {
        super();
    }


    public MyCacheExecutionInterceptor(MyCacheUncaughtExceptionHandler exceptionHandler) {
        super(exceptionHandler);
    }


    @Override
    @Nullable
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        Class<?> targetClass = (invocation.getThis() != null ? AopUtils.getTargetClass(invocation.getThis()) : null);
        Method specificMethod = ClassUtils.getMostSpecificMethod(invocation.getMethod(), targetClass);
        final Method userDeclaredMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

        MyCache myCache = AnnotationUtils.findAnnotation(userDeclaredMethod, MyCache.class);
        String key = myCache.value();
        Map<String, String> map = determineMyCache(userDeclaredMethod);
        if (map == null) {
            throw new IllegalStateException(
                    "No executor specified and no default executor set on MyCacheExecutionInterceptor either");
        }
        Object result = null;
        try {
            if (map.containsKey(key)) {
                return map.get(key);
            }
            result = invocation.proceed();
            return result;
        } catch (ExecutionException ex) {
            handleError(ex.getCause(), userDeclaredMethod, invocation.getArguments());
        } catch (Throwable ex) {
            handleError(ex, userDeclaredMethod, invocation.getArguments());
        } finally {
            if (result != null) {
                map.put(key, result.toString());
            }
        }
        return null;

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
