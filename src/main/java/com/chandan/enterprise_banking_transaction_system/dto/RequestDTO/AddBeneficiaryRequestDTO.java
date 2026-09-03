package com.chandan.enterprise_banking_transaction_system.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class AddBeneficiaryRequestDTO {
    private Long customerId;
    private String beneficiaryName;
    private String accountNumber;
    private String ifscCode;
    private String nickname;
}
