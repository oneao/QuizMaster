package com.quizmaster.common.core.validation;

import jakarta.validation.GroupSequence;

public class ValidGroup {
    // 新增使用(配合spring的@Validated功能分组使用)
    public interface Insert{}

    // 更新使用(配合spring的@Validated功能分组使用)
    public interface Update{}

    // 删除使用(配合spring的@Validated功能分组使用)
    public interface Delete{}

    // 属性必须有这两个分组的才验证(配合spring的@Validated功能分组使用)
    @GroupSequence({Insert.class, Update.class,Delete.class})
    public interface All{}
}
