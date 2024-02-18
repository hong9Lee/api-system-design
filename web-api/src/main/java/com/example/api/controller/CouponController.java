package com.example.api.controller;

import com.example.api.model.CouponIssueVO;
import com.example.api.service.CouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/coupon/issue")
    public ResponseEntity<String> couponIssue(@RequestBody CouponIssueVO couponIssueVO) {
        log.info("CouponController couponIssue userId:{}", couponIssueVO.getUserId());

        /** 순차적으로 처리 */
//        couponService.issue(couponIssueVO);

        /** 쿠폰 발급 로직 비동기 큐 사용 */
//        couponService.issueAsync(couponIssueVO);

        /** 분산 락과 kafka 로직 비동기 처리 */
        couponService.tryLockCouponIssue(couponIssueVO);

        return ResponseEntity.ok("OK");
    }
}
