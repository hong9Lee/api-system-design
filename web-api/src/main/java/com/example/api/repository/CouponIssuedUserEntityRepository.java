package com.example.api.repository;


import com.example.api.entity.CouponIssuedUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

// TODO: 공통화 필요
public interface CouponIssuedUserEntityRepository extends JpaRepository<CouponIssuedUserEntity, Long> {

}
