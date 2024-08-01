package com.lwb.yupao.job;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lwb.yupao.model.User;
import com.lwb.yupao.service.UserService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class PreCacheJob {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;

    //重点用户
    private final List<Long> list  = List.of(1L);
    //每天执行
    //预热推荐用户
    @Scheduled(cron = "0 00 00 * * *")
    public void doCache(){
        RLock lock = redissonClient.getLock("yupao:job:doCache:lock");
        //加锁
        try {
            if (lock.tryLock(0L,30L, TimeUnit.SECONDS)){
                log.info("get Lock successful!!!"+Thread.currentThread().getId());
                for (Long userId : list){
                    String redisKey = String.format("yupao:user:recommend:%s", userId);
                    ValueOperations<String, Object> redis = redisTemplate.opsForValue();
                    //没有缓存，读数据库，写缓存
                    IPage<User>  page = userService.page(new Page<>(1,10));
                    try {
                        redis.set(redisKey,page,10, TimeUnit.MINUTES);
                    } catch (Exception e) {
                        log.error("Redis写入失败",e);
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error("doCacheRecommendUser",e);
        }finally {
            log.info("remove Lock successful!!!"+Thread.currentThread().getId());
            //一定要放到finally中执行
            //释放锁
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
}
