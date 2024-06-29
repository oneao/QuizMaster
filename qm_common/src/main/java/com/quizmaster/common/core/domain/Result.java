package com.quizmaster.common.core.domain;

import com.quizmaster.common.constants.HttpStatusConstants;
import com.quizmaster.common.enums.ResponseEnum;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 成功
     */
    public static final int SUCCESS = HttpStatusConstants.SUCCESS;

    /**
     * 失败
     */
    public static final int FAIL = HttpStatusConstants.ERROR;

    private Integer code;

    private String message;

    private T data;

    public static <T> Result<T> success() {
        return restResult(null, SUCCESS, "操作成功");
    }

    public static <T> Result<T> success(String message) {
        return restResult(null, SUCCESS, message);
    }

    public static <T> Result<T> success(T data) {
        return restResult(data, SUCCESS, "操作成功");
    }

    public static <T> Result<T> success(T data, String message) {
        return restResult(data, SUCCESS, message);
    }

    public static <T> Result<T> fail() {
        return restResult(null, FAIL, "操作失败");
    }

    public static <T> Result<T> fail(String message) {
        return restResult(null, FAIL, message);
    }

    public static <T> Result<T> fail(ResponseEnum responseEnum) {
        return restResult(null, responseEnum.getCode(), responseEnum.getMessage());
    }

    private static <T> Result<T> restResult(T data, Integer code, String message) {
        Result<T> apiResult = new Result<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMessage(message);
        return apiResult;
    }

}
