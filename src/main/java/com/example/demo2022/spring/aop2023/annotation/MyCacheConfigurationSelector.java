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

import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.AdviceModeImportSelector;
import org.springframework.lang.Nullable;


public class MyCacheConfigurationSelector extends AdviceModeImportSelector<EnableMyCache> {

    // TODO
    private static final String MY_CACHE_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME =
            "MY_CACHE_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME";


    @Override
    @Nullable
    public String[] selectImports(AdviceMode adviceMode) {
        switch (adviceMode) {
            case PROXY:
                return new String[]{ProxyMyCacheConfiguration.class.getName()};
            case ASPECTJ:
                return new String[]{MY_CACHE_EXECUTION_ASPECT_CONFIGURATION_CLASS_NAME};
            default:
                return null;
        }
    }

}
