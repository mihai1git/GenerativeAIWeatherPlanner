package com.amazonaws.lambda.mihai.bedrockinvoker.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
//@Target(ElementType.TYPE)
@Target(ElementType.METHOD)
public @interface Trace {
}
