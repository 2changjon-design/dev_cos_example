package com.example.study.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table
@Entity
@Getter
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, name = "username")
    String name;

    @Column(nullable = false)
    String email;

    @Column(nullable = false)
    String passwordHash;

    @OneToMany(mappedBy = "user")
    private final List<Purchase> purchases = new ArrayList<>();

    // 주민번호
    // 현재 거주 주소

    @Column(nullable = false, updatable = false)
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    LocalDateTime updatedAt;


    @Builder
    public User(
            String name,
            String email,
            String passwordHash
    ) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    @Builder(builderMethodName = "createWithoutPassword", builderClassName = "CreateUserWithoutPasswordBuilder")
    public User(
            String name,
            String email
    ) {
        this.name = name;
        this.email = email;
        this.passwordHash = name+ "111" + email;
    }

    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }

}
