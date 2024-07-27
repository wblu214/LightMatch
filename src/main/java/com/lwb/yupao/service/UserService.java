package com.lwb.yupao.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lwb.yupao.common.BaseResult;
import com.lwb.yupao.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import static com.lwb.yupao.enums.UserPrefix.ROLE_ADMIN;
import static com.lwb.yupao.enums.UserPrefix.USER_LOGIN_STATE;

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
     * 用户注销
     * @param request 请求
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签搜索用户
     * @param tagList 标签列表
     * @return List<User>
     */
    List<User> searchUserByTags(List<String> tagList);
    /**
     * 更新用户信息
     * @param user 用户信息
     * @param request HttpServletRequest
     * @return int
     */
    int updateUser(User user,HttpServletRequest request);

    /**
     * 获取当前用户
     * @param request HttpServletRequest
     * @return User
     */
    User getCurrentUser(HttpServletRequest request);
    /**
     * 推荐用户
     *
     */
    BaseResult<IPage<User>> recommendUser(HttpServletRequest request);
    /**
     * 用户脱敏
     * @param originUser 原始用户信息
     * @return  脱敏后的用户信息
     */
    User getSafetyUser(User originUser);
    /**
     * 判断是否为管理员
     * @param request HttpServletRequest
     * @return boolean
     */
    default boolean isAdmin(HttpServletRequest request) {
        User safeUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return safeUser != null && safeUser.getUserRole() == ROLE_ADMIN;
    }
}
