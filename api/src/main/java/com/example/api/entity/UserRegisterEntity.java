package com.example.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Table(name = "user_register")
@AllArgsConstructor
@NoArgsConstructor
public class UserRegisterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_register_seq")
    private long userRegisterSeq;
    private String email;
    private String userPw;

}
