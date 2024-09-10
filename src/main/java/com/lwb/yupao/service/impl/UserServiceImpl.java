package com.lwb.yupao.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lwb.yupao.common.BaseResult;
import com.lwb.yupao.common.BusinessesException;
import com.lwb.yupao.common.ErrorCode;
import com.lwb.yupao.enums.GenderEnum;
import com.lwb.yupao.model.User;
import com.lwb.yupao.model.req.UserUpdateReq;
import com.lwb.yupao.service.UserService;
import com.lwb.yupao.mapper.UserMapper;
import com.lwb.yupao.utils.ResultUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.lwb.yupao.enums.UserPrefix.*;

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
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
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
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"账号长度在4-20之间");
        }
        if (password.length() < 6 || password.length() > 20){
            throw new BusinessesException(ErrorCode.PARAMS_ERROR,"密码长度在6-20之间");
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
        redisTemplate.opsForValue().set(USER_LOGIN_STATE,safetyUser,10, TimeUnit.MINUTES);
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
        redisTemplate.opsForValue().set(USER_LOGIN_STATE,"",10, TimeUnit.MINUTES);
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
    public int updateUser(UserUpdateReq userUpdateReq, HttpServletRequest request) {
        //判断用户是否存在
        User u = userMapper.selectById(userUpdateReq.getId());
        if(u == null){
            throw new BusinessesException(ErrorCode.USER_NOT_EXIST);
        }
        //校验权限
        if (!isAdmin(request)){
            User currentUser = getCurrentUser(request);
            if(!userUpdateReq.getId().equals(currentUser.getId())){
                throw new BusinessesException(ErrorCode.FORBIDDEN);
            }
        }
        if (userUpdateReq.getUserPassword() != null){
            //密码加密(MD5加盐)
            String saltPassword = DigestUtils.md5DigestAsHex(( SALT + u.getUserAccount() + userUpdateReq.getUserPassword()).getBytes());
            userUpdateReq.setUserPassword(saltPassword);
        }
        int result;
        try {
            //更新用户信息
            User user = new User();
            if (userUpdateReq.getGender() != null){
                user.setGender(GenderEnum.getCode(userUpdateReq.getGender()));
            }
            BeanUtils.copyProperties(userUpdateReq,user);
            user.setCode(String.valueOf(user.getId()));
            result =  userMapper.updateById(user);
            //更新缓存
            User updateUser = userMapper.selectById(userUpdateReq.getId());
            redisTemplate.opsForValue().set(USER_LOGIN_STATE,updateUser,10, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    public User getCurrentUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
         User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        log.info("从session读到的currentUser: {}",currentUser);
        try {
            if (currentUser == null) {
                if (redisTemplate.opsForValue().get(USER_LOGIN_STATE) != null) {
                    currentUser = (User) redisTemplate.opsForValue().get(USER_LOGIN_STATE);
                    log.info("从redis读到的currentUser: {}",currentUser);
                    return currentUser;
                }
                throw new BusinessesException(ErrorCode.USER_NOT_LOGIN);
            }
        } catch (BusinessesException e) {
            throw new RuntimeException(e);
        }
        return currentUser;
    }

    @Override
    public BaseResult<IPage<User>> recommendUser(HttpServletRequest request) {
        User loginUser = getCurrentUser(request);
        String redisKey = String.format("yupao:user:recommend:%s", loginUser.getId());
        ValueOperations<String, Object> redis = redisTemplate.opsForValue();
        IPage<User>  redisPage = (IPage<User>) redis.get(redisKey);
        //有缓存，读缓存
        if (redisPage != null){
            return ResultUtil.success(redisPage);
        }
        //没有缓存，读数据库，写缓存
        IPage<User>  page = page(new Page<>(1,10));
        try {
            redis.set(redisKey,page,10, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Redis写入失败",e);
            throw new RuntimeException(e);
        }
        return ResultUtil.success(page);
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
        safetyUser.setCode(originUser.getCode());
        safetyUser.setProfile(originUser.getProfile());
        safetyUser.setUserRole(originUser.getUserRole());
        return safetyUser;
    }
}




