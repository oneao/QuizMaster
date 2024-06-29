package com.quizmaster.common.core.domain.page;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class PageDomain implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    /** 当前记录起始索引 */
    @TableField(exist = false)
    private Integer start;
    /** 每页显示记录数 */
    @TableField(exist = false)
    private Integer end;
}
