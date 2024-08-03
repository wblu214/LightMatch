package com.lwb.yupao.model.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TeamJoinReq implements Serializable {
    @Serial
    private static final long serialVersionUID = 1138770575058303441L;

    /**
     * id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;

}
