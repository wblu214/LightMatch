package com.lwb.yupao.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lwb.yupao.model.User;
import com.lwb.yupao.model.req.UserLoginRequest;
import com.lwb.yupao.model.req.UserRegisterRequest;
import com.lwb.yupao.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.lwb.yupao.enums.UserEnum.ROLE_ADMIN;
import static com.lwb.yupao.enums.UserEnum.USER_LOGIN_STATE;


/**
 * 用户接口
 *
 * @author luweibin
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     *
     * @param userRequest 用户注册请求体
     * @return Long
     */
    @PostMapping("/register")
    Long saveUser(@RequestBody UserRegisterRequest userRequest) {
        if (userRequest == null) {
            return null;
        }
        String userAccount = userRequest.getUserAccount();
        String userPassword = userRequest.getUserPassword();
        String checkPassword = userRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        return userService.userRegister(userRequest.getUserAccount(), userRequest.getUserPassword(), userRequest.getCheckPassword());
    }

    /**
     * 用户登录
     *
     * @param userRequest 用户登录请求体
     * @param request     HttpServletRequest
     * @return User
     */
    @PostMapping("/login")
    User loginUser(@RequestBody UserLoginRequest userRequest, HttpServletRequest request) {
        if (userRequest == null) {
            return null;
        }
        String userAccount = userRequest.getUserAccount();
        String userPassword = userRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        return userService.userLogin(userAccount, userPassword, request);
    }

    @GetMapping("/search")
    List<User> getUserList(String username, HttpServletRequest request) {
        if (isAdmin(request)) {
            return new ArrayList<>();
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        return userService.list(queryWrapper).stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
    }

    @PostMapping("/delete")
    boolean deleteUser(long id, HttpServletRequest request) {
        if (isAdmin(request)) {
            return false;
        }
        if (id < 0) {
            return false;
        }
        return userService.removeById(id);
    }

    /**
     * 判断是否为管理员
     * @param request HttpServletRequest
     * @return boolean
     */
    private boolean isAdmin(HttpServletRequest request) {
        User safeUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return safeUser != null && safeUser.getUserRole() == ROLE_ADMIN;
    }
}
