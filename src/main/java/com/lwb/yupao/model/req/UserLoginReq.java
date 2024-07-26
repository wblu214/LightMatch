package com.lwb.yupao.model.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
@Data
public class UserLoginReq implements Serializable {

    @Serial
    private static final long serialVersionUID = -803235316627051162L;
    private String userAccount;
    private String userPassword;
}
