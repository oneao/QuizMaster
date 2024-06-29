package com.quizmaster.web.controller;

import com.quizmaster.common.core.domain.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class WebUserController {
    @GetMapping
    public Result<Object> test() {
        return Result.success();
    }
}
