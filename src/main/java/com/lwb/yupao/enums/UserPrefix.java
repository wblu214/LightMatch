package com.lwb.yupao.enums;

/**
 * 用户常量
 * @author 路文斌
 */
public interface UserPrefix {
    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "userLoginState";
    //盐值
    String SALT = "1a2b3c4d";
    /**
     * 标签搜索方式
     */
    Boolean SEARCH_TAG_WAY = false; //true为数据库实现，false为内存实现

    // ----------权限----------
    int ROLE_USER = 0; //普通用户
    int ROLE_ADMIN = 1; //管理员
}
