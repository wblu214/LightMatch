package com.lwb.yupao.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置
 * @author 路文斌
 */
@Configuration
@Data
@Slf4j
public class RedissonConfig {
    private  final String redisHost = "47.108.95.155";
//    private  final String redisHost = "127.0.0.1";

    private int redisPort = 6379;
    private String redisPass = "123456";
    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer() // 使用单机模式
                .setAddress("redis://" + redisHost + ":" + redisPort)
                .setPassword(redisPass)
                .setDatabase(1);
        log.info(config.useSingleServer().getAddress()+"=================");
        RedissonClient redisson = Redisson.create(config);
        log.info(config.useSingleServer().getAddress()+"=================");
        return redisson;
    }
}