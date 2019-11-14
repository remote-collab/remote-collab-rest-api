package com.bmw.remotecollab.service.email;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotation to be added to {@code List<Sting>} to validate it to only contain valid emails.
 * <p>
 * Note flag {@link #emptyListIsValid()}:
 * Set to {@code false}, if {@code null} and an empty list is considered invalid. Default {@code true}.
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailListValidator.class)
public @interface EmailList {
    String message() default "Invalid email found in request.";

    /**
     * @return {@code true} iff empty or null list is considered valid. Default {@code true}.
     */
    boolean emptyListIsValid() default true;

    Class[] groups() default {};

    Class[] payload() default {};
}
