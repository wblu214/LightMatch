package com.lwb.yupao.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lwb.yupao.common.BaseResult;
import com.lwb.yupao.common.BusinessesException;
import com.lwb.yupao.common.ErrorCode;
import com.lwb.yupao.model.User;
import com.lwb.yupao.model.req.UserLoginReq;
import com.lwb.yupao.model.req.UserRegisterReq;
import com.lwb.yupao.model.req.UserUpdateReq;
import com.lwb.yupao.service.UserService;
import com.lwb.yupao.utils.QiNiuCloudUtil;
import com.lwb.yupao.utils.ResultUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;
import static com.lwb.yupao.enums.UserPrefix.USER_LOGIN_STATE;


/**
 * 用户接口
 *
 * @author luweibin
 */
@RestController
@RequestMapping("user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private QiNiuCloudUtil qiNiuCloudUtil;

    /**
     * 用户注册
     * @param userRequest 用户注册请求体
     * @return Long
     */

    @PostMapping("/register")
    BaseResult<Long> saveUser(@RequestBody UserRegisterReq userRequest) {
        if (userRequest == null) {
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userRequest.getUserAccount();
        String userPassword = userRequest.getUserPassword();
        String checkPassword = userRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        long register = userService.userRegister(userRequest.getUserAccount(), userRequest.getUserPassword(), userRequest.getCheckPassword());
        return ResultUtil.success(register);
    }

    /**
     * 用户登录
     *
     * @param userRequest 用户登录请求体
     * @param request     HttpServletRequest
     * @return User
     */

    @PostMapping("/login")
    BaseResult<User> loginUser(@RequestBody UserLoginReq userRequest, HttpServletRequest request) {
        if (userRequest == null) {
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        String userAccount = userRequest.getUserAccount();
        String userPassword = userRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtil.success(user);
    }

    /**
     * * 用户登出
     * @param request HttpServletRequest
     * @return Integer
     */
    @PostMapping("/logout")
    BaseResult<Integer> logoutUser(@RequestBody HttpServletRequest request) {
        if (request == null) {
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtil.success(result);
    }
    
    @GetMapping("/search")
    BaseResult<List<User>> getUserList(String username, HttpServletRequest request) {
        if (userService.isAdmin(request)) {
            throw new BusinessesException(ErrorCode.FORBIDDEN);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> collect = userService.list(queryWrapper).stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtil.success(collect);
    }

    @PostMapping("/delete")
    BaseResult<Boolean> deleteUser(long id, HttpServletRequest request) {
        if (userService.isAdmin(request)) {
            throw new BusinessesException(ErrorCode.FORBIDDEN);
        }
        if (id < 0) {
            throw new BusinessesException(ErrorCode.USER_NOT_EXIST);
        }
        boolean res = userService.removeById(id);
        return ResultUtil.success(res);
    }
    @GetMapping("/current")
    BaseResult<User> getCurrentUser(HttpServletRequest request) {
        User currentUser =  (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        try {
            if (currentUser == null) {
                if (redisTemplate.opsForValue().get(USER_LOGIN_STATE) != null) {
                    currentUser = (User) redisTemplate.opsForValue().get(USER_LOGIN_STATE);
                    return ResultUtil.success(currentUser);
                }
                throw new BusinessesException(ErrorCode.USER_NOT_LOGIN);
            }
        } catch (BusinessesException e) {
            throw new RuntimeException(e);
        }
        long id = currentUser.getId();
        // TODO 检验用户是否合法
        User user = userService.getById(id);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtil.success(safetyUser);
    }

    /**
     * 根据标签搜索用户
     * @param tagList 标签列表
     * @return List<User>
     */
    @GetMapping("/searchByTags")
    BaseResult<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagList){
        if (CollectionUtils.isEmpty(tagList)){
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        List<User> userList = userService.searchUserByTags(tagList);
        return ResultUtil.success(userList);
    }
    @GetMapping("/recommend")
    BaseResult<IPage<User>> recommend(HttpServletRequest request){
        if (request == null){
            return  null;
        }
        return userService.recommendUser(request);
    }
    @PostMapping("/update")
    BaseResult<Integer> updateUser(@RequestBody UserUpdateReq userUpdateReq, HttpServletRequest request) {
        //校验参数是否为空
        if (userUpdateReq == null) {
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        int result = userService.updateUser(userUpdateReq, request);
        return ResultUtil.success(result);
    }

    /**
     * 上传图片
     * @param file 文件
     * @return String
     */
    
    @PostMapping("/uploadImage")
    BaseResult<String> uploadImage(MultipartFile file,HttpServletRequest httpRequest) {
        if(file == null){
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        String fileName;
        try{
            fileName = qiNiuCloudUtil.uploadQiNiuCloudImage(file,httpRequest);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultUtil.success(qiNiuCloudUtil.getQiNiuCloudImageUrl(fileName));
    }

    @GetMapping("/getImageUrl")
    BaseResult<String> getImageUrl(String fileName){
        if (StringUtils.isBlank(fileName)){
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        String imageUrl = qiNiuCloudUtil.getQiNiuCloudImageUrl(fileName);
        return ResultUtil.success(imageUrl);
    }
}
