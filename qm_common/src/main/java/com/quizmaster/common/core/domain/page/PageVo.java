package com.quizmaster.common.core.domain.page;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
public class PageVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long total;
    private List<?> rows;
    /**
     * 消息状态码
     */
    private Integer code;
    /**
     * 消息内容
     */
    private String message;

    /**
     * 分页
     *
     * @param list  列表数据
     * @param total 总记录数
     */
    public PageVo(List<?> list, Long total) {
        this.rows = list;
        this.total = total;
    }
}
