package com.lwb.yupao.model.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 *  * 队伍用户信息封装类
 *  * @author 路文斌
 * */
@Data
public class UserVO implements Serializable {
    /**
     * id
     */
    private long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 标签列表 json
     */
    private String tags;
    /**
     * 用户头像
     */
    private String imageUrl;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;
    @Serial
    private static final long serialVersionUID = -2478292049817286437L;

}
