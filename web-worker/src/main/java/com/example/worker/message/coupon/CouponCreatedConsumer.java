package com.example.worker.message.coupon;

import data.entity.CouponIssuedUserEntity;
import data.repository.jpa.CouponIssuedUserEntityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CouponCreatedConsumer {

    private final CouponIssuedUserEntityRepository couponIssuedUserEntityRepository;

    @KafkaListener(topics = "coupon_create", groupId = "group_id")
    public void listener(String userId) {
        long id = Long.parseLong(userId);
        try {
            var couponIssuedUserEntity = CouponIssuedUserEntity.builder()
                    .userId(id)
                    .build();

            couponIssuedUserEntityRepository.save(couponIssuedUserEntity);
            log.info("CouponCreatedConsumer saved success userId:{}", id);
        } catch (Exception e) {
            log.error("CouponCreatedConsumer failed to create coupon userId:{}", id);
        }

    }
}
