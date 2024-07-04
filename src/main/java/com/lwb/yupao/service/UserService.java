package com.lwb.yupao.service;

import com.lwb.yupao.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

/**
* @author 路文斌
* &#064;description  针对表【user(用户表)】的数据库操作Service
* &#064;createDate  2024-07-03 23:52:45
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param account 账号
     * @param password 密码
     * @param checkCode 确认密码
     * @return long
     */
    long userRegister(String account, String password,String checkCode);

    /**
     * 用户登录
     * @param account 账号
     * @param password 密码
     * @return 脱敏后的用户信息
     */
    User userLogin(String account, String password, HttpServletRequest request);

    /**
     * 用户脱敏
     * @param originUser 原始用户信息
     * @return  脱敏后的用户信息
     */
    User getSafetyUser(User originUser);
}
