package com.bmw.remotecollab.admin.rest.errorhandling;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class ApiValidationError implements ApiError.ApiSubError {

    private String field;
    private Object invalidValue;
    private String message;

}
