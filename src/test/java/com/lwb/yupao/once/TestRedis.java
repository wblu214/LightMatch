package com.lwb.yupao.once;

import com.lwb.yupao.model.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class TestRedis {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Test
    public void testRedis(){
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();

        valueOperations.set("test","hello");
        String test = (String) valueOperations.get("test");
        User user = new User();
        user.setUserAccount("46544+646");
        valueOperations.set("user",user);

        User user1 = (User) valueOperations.get("user");
        if (user1 != null) {
            System.out.println(test+ user1);
        }
    }
}
