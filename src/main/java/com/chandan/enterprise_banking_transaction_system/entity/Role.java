package com.chandan.enterprise_banking_transaction_system.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private RoleType roleName; // ADMIN, CUSTOMER, MANAGER
    private String description;
    private LocalDateTime createdAt;
}
