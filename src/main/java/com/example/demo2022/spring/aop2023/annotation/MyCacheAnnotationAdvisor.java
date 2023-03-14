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
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.AnnotationAsyncExecutionInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.function.SingletonSupplier;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Supplier;


@SuppressWarnings("serial")
public class MyCacheAnnotationAdvisor extends AbstractPointcutAdvisor implements BeanFactoryAware {

	private Advice advice;

	private Pointcut pointcut;


	/**
	 * Create a new {@code AsyncAnnotationAdvisor} for bean-style configuration.
	 */
	public MyCacheAnnotationAdvisor() {
		this((Supplier<Executor>) null, (Supplier<MyCacheUncaughtExceptionHandler>) null);
	}


	public MyCacheAnnotationAdvisor(
			@Nullable Executor executor, @Nullable MyCacheUncaughtExceptionHandler exceptionHandler) {

		this(SingletonSupplier.ofNullable(executor), SingletonSupplier.ofNullable(exceptionHandler));
	}


	@SuppressWarnings("unchecked")
	public MyCacheAnnotationAdvisor(
			@Nullable Supplier<Executor> executor, @Nullable Supplier<MyCacheUncaughtExceptionHandler> exceptionHandler) {

		Set<Class<? extends Annotation>> myCacheAnnotationTypes = new LinkedHashSet<>(2);
		myCacheAnnotationTypes.add(MyCache.class);
		try {
			myCacheAnnotationTypes.add((Class<? extends Annotation>)
					ClassUtils.forName("javax.ejb.Asynchronous", MyCacheAnnotationAdvisor.class.getClassLoader()));
		}
		catch (ClassNotFoundException ex) {
			// If EJB 3.1 API not present, simply ignore.
		}
		this.advice = buildAdvice(executor, exceptionHandler);
		this.pointcut = buildPointcut(myCacheAnnotationTypes);
	}


	public void setMyCacheAnnotationType(Class<? extends Annotation> asyncAnnotationType) {
		Assert.notNull(asyncAnnotationType, "'asyncAnnotationType' must not be null");
		Set<Class<? extends Annotation>> asyncAnnotationTypes = new HashSet<>();
		asyncAnnotationTypes.add(asyncAnnotationType);
		this.pointcut = buildPointcut(asyncAnnotationTypes);
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


	protected Advice buildAdvice(
			@Nullable Supplier<Executor> executor, @Nullable Supplier<MyCacheUncaughtExceptionHandler> exceptionHandler) {

		AnnotationMyCacheExecutionInterceptor interceptor = new AnnotationMyCacheExecutionInterceptor(null);
		interceptor.configure(executor, exceptionHandler);
		return interceptor;
	}


	protected Pointcut buildPointcut(Set<Class<? extends Annotation>> asyncAnnotationTypes) {
		ComposablePointcut result = null;
		for (Class<? extends Annotation> asyncAnnotationType : asyncAnnotationTypes) {
			Pointcut cpc = new AnnotationMatchingPointcut(asyncAnnotationType, true);
			Pointcut mpc = new AnnotationMatchingPointcut(null, asyncAnnotationType, true);
			if (result == null) {
				result = new ComposablePointcut(cpc);
			}
			else {
				result.union(cpc);
			}
			result = result.union(mpc);
		}
		return (result != null ? result : Pointcut.TRUE);
	}

}
