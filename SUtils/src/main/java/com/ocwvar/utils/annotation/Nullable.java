package com.ocwvar.utils.annotation;

import java.lang.annotation.*;

@Target( { ElementType.FIELD, ElementType.LOCAL_VARIABLE, ElementType.METHOD, ElementType.PARAMETER } )
@Retention( RetentionPolicy.SOURCE )
@Documented
public @interface Nullable {
}
