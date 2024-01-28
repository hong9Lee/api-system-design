package com.example.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "coupon_issued_user")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponIssuedUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_issued_user_seq")
    private long seq;

    private long userId;

    @CreationTimestamp
    private LocalDateTime regDateTime;

    @UpdateTimestamp
    private LocalDateTime updateDateTime;

}
