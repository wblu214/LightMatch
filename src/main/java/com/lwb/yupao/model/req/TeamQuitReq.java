package com.lwb.yupao.model.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class TeamQuitReq implements Serializable {
    @Serial
    private static final long serialVersionUID = -1256387139574289442L;

    /**
     * id
     */
    private Long teamId;
}
