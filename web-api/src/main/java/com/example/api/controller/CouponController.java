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
    public ResponseEntity couponIssue(@RequestBody CouponIssueVO couponIssueVO) {
        log.info("CouponController couponIssue userId:{}", couponIssueVO.getUserId());
        couponService.issue(couponIssueVO);
        return ResponseEntity.ok("OK");
    }
}
