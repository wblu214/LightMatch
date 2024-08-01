package com.lwb.yupao.model.req;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class UserRegisterReq implements Serializable {

    @Serial
    private static final long serialVersionUID = 3191210229314632079L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
