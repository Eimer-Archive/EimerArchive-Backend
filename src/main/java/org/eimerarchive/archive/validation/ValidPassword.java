package org.eimerarchive.archive.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

import java.lang.annotation.ElementType;

/**
 * The annotation for a custom password validator
 *
 * @author steviebeenz
 * @see PasswordConstraintValidator
 */
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {
    String message() default "Invalid Password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}