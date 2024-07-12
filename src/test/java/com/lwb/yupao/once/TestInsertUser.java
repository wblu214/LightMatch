package com.lwb.yupao.once;

import com.lwb.yupao.mapper.UserMapper;
import com.lwb.yupao.model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

@SpringBootTest
public class TestInsertUser {
    @Resource
    private UserMapper userMapper;
    @Test
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int NUM = 1000;
        for(int i = 0;i<NUM;i++) {
            User user = new User();
            user.setUsername("朱棣");
            user.setUserAccount("zdddd"+i);
            user.setImageUrl("");
            user.setGender(1);
            user.setUserPassword("123456");
            user.setPhone("12547458745");
            user.setEmail("45@qq.com");
            user.setStatus(0);
            user.setUserRole(0);
            user.setTags("[]");
            user.setCode(String.valueOf(i+5));
            user.setProfile("永乐大帝");
            userMapper.insert(user);
            System.out.println("插入成功！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！");
        }
        stopWatch.stop();
        System.out.println("执行总时间为"+stopWatch.getTotalTimeMillis()/1000.0000+"秒");
    }

}
