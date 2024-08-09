package com.lwb.yupao.enums;

import com.lwb.yupao.common.BusinessesException;
import com.lwb.yupao.common.ErrorCode;
import lombok.Getter;

@Getter
public enum GenderEnum {
    FEMALE(0,"女"),
    MALE(1,"男"),
    UNKNOWN(-1,"未知");


    private final Integer code;

    private final String desc;

    GenderEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static Integer getCode(String desc) {
        if (desc.equals(FEMALE.desc)){
            return FEMALE.code;
        }else if (desc.equals(MALE.desc)){
            return MALE.code;
        }
       throw new BusinessesException(ErrorCode.PARAMS_ERROR,"输入性别错误");
    }
    public static String getDesc(Integer code) {
        if (code.equals(FEMALE.code)){
            return FEMALE.desc;
        }else if (code.equals(MALE.code)){
            return MALE.desc;
        }
        return null;
    }
}
