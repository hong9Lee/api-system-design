package com.example.api.repository;

import com.example.api.entity.UserRegisterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRegisterEntityRepository extends JpaRepository<UserRegisterEntity, Long> {

    UserRegisterEntity findByEmail(String email);
}
