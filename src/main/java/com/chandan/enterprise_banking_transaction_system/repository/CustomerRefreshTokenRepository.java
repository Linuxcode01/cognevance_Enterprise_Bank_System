package com.chandan.enterprise_banking_transaction_system.repository;

import com.chandan.enterprise_banking_transaction_system.entity.CustomerRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRefreshTokenRepository extends JpaRepository<CustomerRefreshToken, Long> {
}
