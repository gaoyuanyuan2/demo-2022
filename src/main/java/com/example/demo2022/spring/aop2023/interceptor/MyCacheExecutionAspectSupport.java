/*
 * Copyright 2002-2022 the original author or authors.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.function.SingletonSupplier;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Supplier;

/**
 * 缓存异常处理器
 */
public abstract class MyCacheExecutionAspectSupport implements BeanFactoryAware {


    protected final Log logger = LogFactory.getLog(getClass());

    private static final Map<String, String> map = new ConcurrentHashMap<>();

    private SingletonSupplier<MyCacheUncaughtExceptionHandler> exceptionHandler;

    @Nullable
    private BeanFactory beanFactory;


    public MyCacheExecutionAspectSupport() {
        this.exceptionHandler = SingletonSupplier.of(SimpleMyCacheUncaughtExceptionHandler::new);
    }


    public MyCacheExecutionAspectSupport(MyCacheUncaughtExceptionHandler exceptionHandler) {
        this.exceptionHandler = SingletonSupplier.of(exceptionHandler);
    }

    public void configure(@Nullable Supplier<MyCacheUncaughtExceptionHandler> exceptionHandler) {
        this.exceptionHandler = new SingletonSupplier<>(exceptionHandler, SimpleMyCacheUncaughtExceptionHandler::new);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }


    @Nullable
    protected Map<String, String> determineMyCache(Method method) {
        return map;
    }


    protected void handleError(Throwable ex, Method method, Object... params) throws Exception {
        if (Future.class.isAssignableFrom(method.getReturnType())) {
            ReflectionUtils.rethrowException(ex);
        } else {
            // Could not transmit the exception to the caller with default executor
            try {
                this.exceptionHandler.obtain().handleUncaughtException(ex, method, params);
            } catch (Throwable ex2) {
                logger.warn("Exception handler for async method '" + method.toGenericString() +
                        "' threw unexpected exception itself", ex2);
            }
        }
    }

}
