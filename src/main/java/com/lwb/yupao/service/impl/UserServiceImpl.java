package com.lwb.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lwb.yupao.common.BusinessesException;
import com.lwb.yupao.common.ErrorCode;
import com.lwb.yupao.model.User;
import com.lwb.yupao.service.UserService;
import com.lwb.yupao.mapper.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.nio.file.OpenOption;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.lwb.yupao.enums.UserEnum.*;

/**
* @author 路文斌
* &#064;description  针对表【user(用户表)】的数据库操作Service实现
* &#064;createDate  2024-07-03 23:52:45
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;
    /**
     * 用户注册
     * @param   account 用户名 password 密码 checkPassword 验证码
     * @return int
     */
    @Override
    public long userRegister(String account, String password,String checkPassword) {
        //1.校验
        if (StringUtils.isAllBlank(account,password,checkPassword)){
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        if(account.length() < 4 || account.length() > 20){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR);
        }
        if (password.length() < 6 || password.length() > 20){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR);
        }
        //校验特殊字符
        String pattern = "[`~!@#$%^&*()+=|{}:;\\\\.<>/?！￥…（）—【】‘；：”“’。，、？' ]";
        Matcher matcher = Pattern.compile(pattern).matcher(account);
        if (matcher.find()){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"参数包含特殊字符");
        }
        //校验两次密码是否一致
        if(!password.equals(checkPassword)){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"两次输入密码不一致");
        }
        //校验账号是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",account);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0){
            throw new BusinessesException(ErrorCode.SYSTEM_ERROR,"账号已存在");
        }
        //2.密码加密(MD5加盐)
        String saltPassword = DigestUtils.md5DigestAsHex(( SALT + account + password).getBytes());
        //3.插入用户信息
        User user  = new User();
        user.setUserAccount(account);
        user.setUserPassword(saltPassword);
        //4.返回结果
        boolean saveResult = this.save(user);
        if (!saveResult){
            throw new BusinessesException(ErrorCode.SYSTEM_ERROR);
        }
        return user.getId();
    }

    /**
     * 用户登录
     * @param account 账号
     * @param password 密码
     * @return 脱敏后的用户信息
     */
    @Override
    public User userLogin(String account, String password, HttpServletRequest request) {
        //1.校验
        if (StringUtils.isAllBlank(account,password,password)){
            throw new BusinessesException(ErrorCode.NULL_ERROR);
        }
        if(account.length() < 4 || account.length() > 20){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR);
        }
        if (password.length() < 6 || password.length() > 20){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR);
        }
        //校验特殊字符
        String pattern = "[`~!@#$%^&*()+=|{}:;\\\\.<>/?！（）—【】‘；：”“’。，、？' ]";
        Matcher matcher = Pattern.compile(pattern).matcher(account);
        if (matcher.find()){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"参数包含特殊字符");
        }
        //2.加密
        String saltPassword = DigestUtils.md5DigestAsHex((SALT + account + password).getBytes());
        //3.查询用户信息
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",account);
        queryWrapper.eq("userPassword",saltPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            log.info("login failed, account or password error");
            throw new BusinessesException(ErrorCode.USER_NOT_EXIST,"账号或密码错误");
        }
        //4.用户脱敏
        User safetyUser = getSafetyUser(user);
        //5.记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);
        return safetyUser;
    }
    /**
     * 用户注销
     *
     * @param request 请求
     * @return int
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 0;
    }

    /**
     * 根据标签搜索用户
     * @param tagList 标签列表
     * @return List<User>
     */
    @Override
    public List<User> searchUserByTags(List<String> tagList) {
        if (CollectionUtils.isEmpty(tagList)) {
            throw new BusinessesException(ErrorCode.PARAMS_ERROR);
        }
        if(SEARCH_TAG_WAY){
            //数据库实现
            long startTime1 = System.currentTimeMillis();
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            for(String tag : tagList){
                queryWrapper = queryWrapper.like("tags",tag);
            }
            List<User> list = userMapper.selectList(queryWrapper);
            return list.stream().map(this::getSafetyUser).collect(Collectors.toList());
//            log.info("mysql time: {}",System.currentTimeMillis() - startTime1);
        }else{
            //内存实现
            long startTime2 = System.currentTimeMillis();
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
            List<User> userList = userMapper.selectList(queryWrapper1);
            Gson gson = new Gson();
            //可改为并发流parallelStream,但这个并发流使用的是公共线程池，有坑
            return userList.parallelStream().filter(user -> {
                String tagStr = user.getTags();
                if (StringUtils.isBlank(tagStr)){
                    return false;
                }
                Set<String> tempList = gson.fromJson(tagStr, new TypeToken<Set<String>>(){}.getType());
                //使用Optional.ofNullable判空，如果为空就新建一个对象
                tempList = Optional.ofNullable(tempList).orElse(new HashSet<>());
                for (String tag : tagList) {
                    if (!tempList.contains(tag)) {
                        return false;
                    }
                }
                return true;
            }).map(this::getSafetyUser).collect(Collectors.toList());
//            log.info("memory time: {}",System.currentTimeMillis() - startTime2);
        }
}

    @Override
    public int updateUser(User user, HttpServletRequest request) {
        //判断用户是否存在
        User u = userMapper.selectById(user.getId());
        if(u == null){
            throw new BusinessesException(ErrorCode.USER_NOT_EXIST);
        }
        //校验权限
        if (!isAdmin(request)){
            User currentUser = getCurrentUser(request);
            if(!user.getId().equals(currentUser.getId())){
                throw new BusinessesException(ErrorCode.FORBIDDEN);
            }
        }
        //更新用户信息
        try {
            return userMapper.updateById(user);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public User getCurrentUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
         User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        if (user == null) {
            throw new BusinessesException(ErrorCode.USER_NOT_LOGIN);
        }
        return user;
    }
    /**
     * 用户脱敏
     * @param originUser 初始用户
     * @return User
     */
    @Override
    public User getSafetyUser(User originUser){
        if(originUser == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
//        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setImageUrl(originUser.getImageUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setStatus(originUser.getStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
//        safetyUser.setCode(originUser.getCode());
        safetyUser.setProfile(originUser.getProfile());
        return safetyUser;
    }
}




