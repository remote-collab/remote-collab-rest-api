package com.bmw.remotecollab.admin.service.email;

import org.apache.commons.validator.routines.EmailValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

public class EmailListValidator implements ConstraintValidator<EmailList, List<String>> {

    private EmailValidator ev;
    private boolean emptyListIsValid;
    @Override
    public void initialize(EmailList constraintAnnotation) {
        ev = EmailValidator.getInstance();
        emptyListIsValid = constraintAnnotation.emptyListIsValid();
    }

    @Override
    public boolean isValid(List<String> emails, ConstraintValidatorContext context) {
        if(!emptyListIsValid && (emails == null || emails.isEmpty())) {
            return false;
        }
        return emails == null || emails.stream().allMatch(ev::isValid);
    }

}
