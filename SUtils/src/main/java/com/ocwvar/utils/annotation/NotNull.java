package com.ocwvar.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target( { ElementType.FIELD,ElementType.LOCAL_VARIABLE,ElementType.METHOD, ElementType.PARAMETER} )
@Retention( RetentionPolicy.SOURCE)
public @interface NotNull {
}
