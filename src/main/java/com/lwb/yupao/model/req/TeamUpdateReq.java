package com.lwb.yupao.model.req;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
public class TeamUpdateReq implements Serializable {
    @Serial
    private static final long serialVersionUID = 5774963918113565043L;
    /**
     * id
     */
    private Long id;
    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;
    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id（队长 id）
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}
