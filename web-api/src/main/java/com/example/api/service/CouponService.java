package com.example.api.service;

import com.example.api.message.coupon.CouponCreateProducer;
import com.example.api.model.CouponIssueVO;
import data.repository.jpa.CouponIssuedUserEntityRepository;
import data.repository.redis.IssueCouponUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class CouponService {

    private final IssueCouponUserRepository issueCouponUserRepository;

    private final CouponIssuedUserEntityRepository couponIssuedUserEntityRepository;

    private final CouponCreateProducer couponCreateProducer;
    public static String ISSUE_COUPON_USER_KEY = "issue_coupon_user_id";
    public static String ISSUE_COUPON_USER_COUNT_KEY = "issue_coupon_user_count_id";

    public void issue(CouponIssueVO couponIssueVO) {
        if(ObjectUtils.isEmpty(couponIssueVO) || couponIssueVO.getUserId() == 0L) {
            return;
        }

        // Redis 캐시를 체크합니다.
        if (!isCachedUser(couponIssueVO.getUserId())) {
            log.warn("이미 쿠폰을 발급받은 유저입니다. userId:{}", couponIssueVO.getUserId());
            return;
        }

        long count = issueCouponUserRepository.increment(ISSUE_COUPON_USER_COUNT_KEY);
        if(count > 1000000000) {
            log.warn("쿠폰이 전부 소진되었습니다.");
            return;
        }

        couponCreateProducer.create(couponIssueVO.getUserId());
    }

    public boolean isCachedUser(Long userId) {
        Long issue = issueCouponUserRepository.setUserId(ISSUE_COUPON_USER_KEY, userId);
        if(issue == 1) return true;

        /** 캐시 히트되지 않은 경우, db 조회하여 캐싱처리. */
        Boolean isIssued = couponIssuedUserEntityRepository.existsById(userId);

        if(!isIssued) {
            issueCouponUserRepository.setUserId(ISSUE_COUPON_USER_KEY, userId);
            return true;
        }

        return false;
    }
}
