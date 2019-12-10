package com.bmw.remotecollab.service.email;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

/**
 * Validator to check if a List of emails contains only valid emails. Checked by apache commons' {@link EmailValidator}.
 * </br>
 * Associated Annotation: {@link EmailList}
 */
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
        return emails == null || emails.stream().allMatch(this::isValidIgnoreEmpty);
    }

    private boolean isValidIgnoreEmpty(String value){
        return StringUtils.isEmpty(value) || this.ev.isValid(value);
    }

}
