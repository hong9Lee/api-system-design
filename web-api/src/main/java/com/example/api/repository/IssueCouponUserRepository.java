package com.example.api.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import static com.example.api.service.CouponService.ISSUE_COUPON_USER_COUNT_KEY;
import static com.example.api.service.CouponService.ISSUE_COUPON_USER_KEY;


@Repository
@RequiredArgsConstructor
public class IssueCouponUserRepository {

    private final RedisTemplate<String, String> redisTemplate;


    public Long setUserId(Long userId) {
        return redisTemplate.opsForSet().add(ISSUE_COUPON_USER_KEY, userId.toString());
    }

    public Long increment() {
        return redisTemplate.opsForValue().increment(ISSUE_COUPON_USER_COUNT_KEY);
    }

}
