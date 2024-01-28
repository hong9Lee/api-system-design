package com.example.api.message.coupon;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CouponCreateProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void create(Long userId) {
        log.info("KafkaTemplate PRODUCE COUPON TOPIC userId: {}", userId);
        kafkaTemplate.send("coupon_create", userId.toString());
    }
}
