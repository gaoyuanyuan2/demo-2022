/*
 * Copyright 2002-2021 the original author or authors.
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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.function.SingletonSupplier;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Configuration(proxyBeanMethods = false)
public abstract class AbstractMyCacheConfiguration implements ImportAware {

    @Nullable
    protected AnnotationAttributes enableMyCache;

    @Nullable
    protected Supplier<MyCacheUncaughtExceptionHandler> exceptionHandler;


    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        this.enableMyCache = AnnotationAttributes.fromMap(
                importMetadata.getAnnotationAttributes(EnableMyCache.class.getName()));
        if (this.enableMyCache == null) {
            throw new IllegalArgumentException(
                    "@EnableMyCache is not present on importing class " + importMetadata.getClassName());
        }
    }

    /**
     * Collect any {@link MyCacheConfigurer} beans through autowiring.
     */
    @Autowired
    void setConfigurers(ObjectProvider<MyCacheConfigurer> configurers) {
        Supplier<MyCacheConfigurer> configurer = SingletonSupplier.of(() -> {
            List<MyCacheConfigurer> candidates = configurers.stream().collect(Collectors.toList());
            if (CollectionUtils.isEmpty(candidates)) {
                return null;
            }
            if (candidates.size() > 1) {
                throw new IllegalStateException("Only one MyCacheConfigurer may exist");
            }
            return candidates.get(0);
        });
        this.exceptionHandler = adapt(configurer, MyCacheConfigurer::getMyCacheUncaughtExceptionHandler);
    }

    private <T> Supplier<T> adapt(Supplier<MyCacheConfigurer> supplier, Function<MyCacheConfigurer, T> provider) {
        return () -> {
            MyCacheConfigurer configurer = supplier.get();
            return (configurer != null ? provider.apply(configurer) : null);
        };
    }

}
