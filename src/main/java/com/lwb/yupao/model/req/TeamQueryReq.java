package com.lwb.yupao.model.req;

import com.lwb.yupao.common.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQueryReq extends Page implements Serializable {
    @Serial
    private static final long serialVersionUID = -8209894175670684495L;
    /**
     * id
     */
    private Long id;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 搜索关键词
     */
    private String searchText;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;
}

