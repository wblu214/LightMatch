package com.lwb.yupao.service;

import com.lwb.yupao.mapper.UserMapper;
import com.lwb.yupao.model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest()
public class UserServiceTest {
    @Resource
    private  UserService userService;
    @Resource
    private UserMapper userMapper;

    @Test
     public void testAddUser(){
        User user = new User();
        user.setUsername("张三丰");
        user.setUserAccount("7788455");
        user.setImageUrl("");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("14574587458");
        user.setEmail("4848494@qq.com");
        int save = userMapper.insert(user);
        System.out.println(user.getId());
        assertEquals(save,1);
    }

    @Test
    void userRegister() {
        String userAccount = "lwb123";
        String userPassword = "";
        String checkPassword = "123456";
        long res = userService.userRegister(userAccount, userPassword, checkPassword);
        // 用户名不能为空
        Assertions.assertEquals(-1,res);
        userAccount = "lwb";
        userPassword = "123456";
        res = userService.userRegister(userAccount, userPassword, checkPassword);
        // 用户名小于4位
        Assertions.assertEquals(-1,res);
        userAccount = "7788455";
        res = userService.userRegister(userAccount, userPassword, checkPassword);
        // 用户名已存在
        Assertions.assertEquals(-1,res);
        userAccount = "l wb";
        res = userService.userRegister(userAccount, userPassword, checkPassword);
        // 用户名含有特殊字符
        Assertions.assertEquals(-1,res);
        checkPassword= "1234567";
        res = userService.userRegister(userAccount, userPassword, checkPassword);
        // 密码和确认密码不同
        Assertions.assertEquals(-1,res);
        userAccount = "lwb456";
        userPassword = "123456";
        checkPassword = "123456";
        res = userService.userRegister(userAccount, userPassword, checkPassword);
        //注册成功示例
        Assertions.assertTrue(res>0);
    }
}