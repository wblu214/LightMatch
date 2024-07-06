package com.lwb.yupao.model;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 标签表
 * &#064;TableName  tag
 */
@TableName(value ="tag")
@Data
public class Tag implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 父标签ID
     */
    private Long parentId;

    /**
     * 0不是父标签，1是父标签
     */
    private Integer isParent;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除（1为已删除0为未删除）
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}