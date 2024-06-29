package com.quizmaster.common.enums;

import com.quizmaster.common.constants.HttpStatusConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseEnum {
    SUCCESS(HttpStatusConstants.SUCCESS, "请求成功"),
    ERROR(HttpStatusConstants.ERROR, "服务器内部错误"),
    UNAUTHORIZED(HttpStatusConstants.UNAUTHORIZED, "尚未授权");
    private final Integer code;
    private final String message;
}
