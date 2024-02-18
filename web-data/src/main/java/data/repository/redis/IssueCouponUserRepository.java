package data.repository.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
@Slf4j
public class IssueCouponUserRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedissonClient redissonClient;

    public Long setUserId(String key, Long userId) {
        return redisTemplate.opsForSet().add(key, userId.toString());
    }

    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    public boolean useLock(long userId, String key) {
        RLock lock = redissonClient.getLock("couponLock:" + userId);
        log.info("couponLock: {}", userId);
        boolean isLocked = false;

        try {
            // 분산 락 획득 시도, 최대 3초 대기, 락 유지 시간 20초 설정
            isLocked = lock.tryLock(3, 20, TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("lock을 획득하지 못했습니다.");
                return false;
            }

            long count = increment(key);
            if (count > 1000000000) {
                log.error("쿠폰이 전부 소진되었습니다.");
                return false;
            }

            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            if (isLocked) lock.unlock();
        }
    }

}
