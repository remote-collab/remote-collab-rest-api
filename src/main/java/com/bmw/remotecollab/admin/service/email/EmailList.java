package com.bmw.remotecollab.admin.service.email;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailListValidator.class)
public @interface EmailList {
    String message() default "Invalid email found in request.";
    boolean emptyListIsValid() default true;
    Class[] groups() default {};
    Class[] payload() default {};
}
