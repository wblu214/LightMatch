package com.lwb.yupao.model.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ImageUploadReq implements Serializable {
    @Serial
    private static final long serialVersionUID = -9136287227302667891L;
    /**
     * id
     */
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
     * 编号
     */
    private String code;
    /**
     * 图片类型
     */
    private String imageType;
}
