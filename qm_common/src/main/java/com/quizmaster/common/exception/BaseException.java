package com.quizmaster.common.exception;

import com.quizmaster.common.enums.ResponseEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BaseException extends RuntimeException {
    private Integer code;
    private String message;

    public BaseException() {
        super();
    }

    public BaseException(ResponseEnum responseEnum) {
        super(responseEnum.getMessage());
        this.code = responseEnum.getCode();
        this.message = responseEnum.getMessage();
    }
}
