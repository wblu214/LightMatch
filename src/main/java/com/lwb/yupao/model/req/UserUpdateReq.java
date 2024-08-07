package com.lwb.yupao.model.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
@Data
public class UserUpdateReq implements Serializable {
    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = -3847138779641836035L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String imageUrl;

    /**
     * 性别
     */
    private String gender;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 手机
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    /**
     * 编号
     */
    private String code;
    /**
     * 个人简介
     */
    private String profile;
}
