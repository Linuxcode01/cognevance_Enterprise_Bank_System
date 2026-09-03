package com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Getter
@Setter
public class BeneficiaryResponseDTO {
    private Long id;
    private String beneficiaryName;
    private String accountNumber;
    private String ifscCode;
    private String nickname;
    private boolean active;
    private LocalDateTime addedAt;
}
