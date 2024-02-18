package com.example.api.service;

import com.example.api.message.coupon.CouponCreateProducer;
import com.example.api.model.CouponIssueVO;
import data.repository.jpa.CouponIssuedUserEntityRepository;
import data.repository.redis.IssueCouponUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponService {

    private final IssueCouponUserRepository issueCouponUserRepository;

    private final CouponIssuedUserEntityRepository couponIssuedUserEntityRepository;

    private final CouponCreateProducer couponCreateProducer;

    private final RedissonClient redissonClient;
    public static String ISSUE_COUPON_USER_KEY = "issue_coupon_user_id";
    public static String ISSUE_COUPON_USER_COUNT_KEY = "issue_coupon_user_count_id";

    public void issue(CouponIssueVO couponIssueVO) {
        if (ObjectUtils.isEmpty(couponIssueVO) || couponIssueVO.getUserId() == 0L) {
            return;
        }

        if (!isCachedUser(couponIssueVO.getUserId())) {
            log.warn("이미 쿠폰을 발급받은 유저입니다. userId:{}", couponIssueVO.getUserId());
            return;
        }

        boolean result = incrementAndCheckCouponLimit(ISSUE_COUPON_USER_COUNT_KEY);
        if (!result) {
            log.error("Coupons exhausted for userId:{}", couponIssueVO.getUserId());
            return;
        }

        couponCreateProducer.create(couponIssueVO.getUserId());
    }

    public void issueAsync(CouponIssueVO couponIssueVO) {
        if (ObjectUtils.isEmpty(couponIssueVO) || couponIssueVO.getUserId() == 0L || !isCachedUser(couponIssueVO.getUserId())) {
            log.warn("이미 쿠폰을 발급받은 유저입니다. userId:{}", couponIssueVO.getUserId());
            return;
        }

        log.info("Issuing coupon asynchronously for userId: {}", couponIssueVO.getUserId());

        // 쿠폰 발급 요청을 비동기 메시지 큐에 삽입
        CompletableFuture.runAsync(() -> {
            log.info("Async task started for userId: {}", couponIssueVO.getUserId());
            boolean result = incrementAndCheckCouponLimit(ISSUE_COUPON_USER_COUNT_KEY);
            if (!result) {
                log.error("Coupons exhausted for userId: {}", couponIssueVO.getUserId());
                return;
            }

            couponCreateProducer.create(couponIssueVO.getUserId());
            log.info("Coupon issued successfully for userId: {}", couponIssueVO.getUserId());
        }).exceptionally(ex -> {
            log.error("Error issuing coupon for userId: {}: {}", couponIssueVO.getUserId(), ex.getMessage());
            return null;
        });
    }

    private boolean isCachedUser(Long userId) {
        Long issue = issueCouponUserRepository.setUserId(ISSUE_COUPON_USER_KEY, userId);
        if (issue == 1) {
            return true;
        }

        if (!getIssued(userId)) {
            issueCouponUserRepository.setUserId(ISSUE_COUPON_USER_KEY, userId);
            return true;
        }
        log.info("User already issued a coupon, skipping: {}", userId);
        return false;
    }

    @Transactional(readOnly = true)
    public Boolean getIssued(Long userId) {
        /** 캐시 히트되지 않은 경우, db 조회하여 캐싱처리. */
        return couponIssuedUserEntityRepository.existsById(userId);
    }

    public CompletableFuture<String> tryLockCouponIssue(CouponIssueVO couponIssueVO) {
        if (ObjectUtils.isEmpty(couponIssueVO) || couponIssueVO.getUserId() == 0L) {
            return CompletableFuture.completedFuture("Invalid request");
        }

        if (!isCachedUser(couponIssueVO.getUserId())) {
            log.warn("이미 쿠폰을 발급받은 유저입니다. userId:{}", couponIssueVO.getUserId());
            return CompletableFuture.completedFuture("Coupon already issued");
        }

        return useLockAndIssueCoupon(couponIssueVO.getUserId(), ISSUE_COUPON_USER_COUNT_KEY);
    }

    private CompletableFuture<String> useLockAndIssueCoupon(long userId, String key) {
        RLock lock = redissonClient.getLock("couponLock:" + userId);
        boolean isLocked = false;

        try {
            isLocked = lock.tryLock(3, 20, TimeUnit.SECONDS);
            if (!isLocked) {
                log.error("Failed to acquire lock for userId :{}", userId);
                return CompletableFuture.completedFuture("Failed to acquire lock");
            }

            boolean result = incrementAndCheckCouponLimit(key);
            if (!result) {
                log.warn("Coupons exhausted for userId :{}", userId);
                return CompletableFuture.completedFuture("No coupons left");
            }

            return asyncIssueCoupon(userId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return CompletableFuture.completedFuture("Interrupted");
        } finally {
            if (isLocked) {
                lock.unlock();
            }
        }
    }

    @Async
    public CompletableFuture<String> asyncIssueCoupon(long userId) {
        couponCreateProducer.create(userId);
        return CompletableFuture.completedFuture("Coupon issued");
    }


    private boolean incrementAndCheckCouponLimit(String key) {
        long count = issueCouponUserRepository.increment(key);
        return count <= 100;
    }
}
