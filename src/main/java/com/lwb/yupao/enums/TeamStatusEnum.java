package com.lwb.yupao.enums;

import lombok.Data;
import lombok.Getter;

/**
 * * 队伍状态枚举
 * * @author 路文斌
 */
@Getter
public enum TeamStatusEnum {
    PUBLIC(0,"公开"),
    PRIVATE(1,"私有"),
    SECRET(2,"加密");
    private final Integer code;
        
    private final String desc;
    
    TeamStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    public static TeamStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (TeamStatusEnum teamStatusEnum : TeamStatusEnum.values()) {
            if (teamStatusEnum.getCode().equals(value)) {
                return teamStatusEnum;
            }
        }
        return null;
    }
}
