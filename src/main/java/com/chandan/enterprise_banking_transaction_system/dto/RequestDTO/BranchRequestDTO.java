package com.chandan.enterprise_banking_transaction_system.dto.RequestDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class BranchRequestDTO {
    private String branchCode;
    private String branchName;
    private String ifscCode;
    private String address;
    private String city;
    private String state;
    private String phoneNumber;
    private String email;
}
