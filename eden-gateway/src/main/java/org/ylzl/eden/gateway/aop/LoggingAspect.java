/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ylzl.eden.gateway.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.ylzl.eden.spring.boot.support.aop.LoggingAspectAdapter;

/**
 * 日志切面
 *
 * @author gyl
 * @since 1.0.0
 */
@Slf4j
@Aspect
public class LoggingAspect extends LoggingAspectAdapter {

	@Pointcut("within(@org.springframework.stereotype.Service *)" +
		" || within(@org.springframework.stereotype.Controller *)" +
		" || within(@org.springframework.stereotype.Component *)" +
		" || within(@org.springframework.web.bind.annotation.RestController *)")
	public void springBeanPointcut() {}

	@Pointcut("within(org.ylzl.eden.gateway.service..*)" +
		" || within(org.ylzl.eden.gateway.scheduling..*)")
	public void applicationPackagePointcut() {}

	@AfterThrowing(pointcut = "applicationPackagePointcut() && springBeanPointcut()", throwing = "e")
	@Override
	public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
		super.logAfterThrowing(joinPoint, e);
	}

	@Around("applicationPackagePointcut() && springBeanPointcut()")
	@Override
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
		return super.logAround(joinPoint);
	}
}
