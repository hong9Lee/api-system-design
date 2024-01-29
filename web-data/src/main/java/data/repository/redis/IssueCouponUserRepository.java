package data.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class IssueCouponUserRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public Long setUserId(String key, Long userId) {
        return redisTemplate.opsForSet().add(key, userId.toString());
    }

    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

}
