/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flipkart.gjex.grpc.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import com.flipkart.gjex.core.util.Pair;

public class AnnotationUtils {

	/**
	 * Helper method to get annotated methods on a Class. Navigates up the superclass hierarchy to get the methods. This is required when used with DI mechanisms like Guice that
	 * create a CGLIB proxy sub-type for instances and annotations are not copied to the sub-type.
	 * Cannot use @Inherited annotation as a workaround because it applies only to Type/Class level annotations and not for Method-level ones.
	 * @see https://github.com/google/guice/issues/101
	 * 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Pair<?,Method>> getAnnotatedMethods(Class<?> cls, Class<? extends Annotation> anno) {
		List<Pair<?,Method>> methods = new LinkedList<Pair<?,Method>>();
		for (Method m : cls.getDeclaredMethods()) {
			if (m.getAnnotation(anno) != null) {
				methods.add(new Pair(cls,m));
			}
		}
		if (methods.isEmpty()) {
			Class<?> superCls = cls.getSuperclass();
			return (superCls != null) ? getAnnotatedMethods(superCls, anno) : null;
		}
		return methods;
	}
	
}
