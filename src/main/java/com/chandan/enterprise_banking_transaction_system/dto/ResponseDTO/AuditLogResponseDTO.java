package com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
@Getter
@Setter
public class AuditLogResponseDTO {

    private Long id;
    private String username;
    private String action;
    private String entityName;
    private String entityId;
    private String ipAddress;
    private LocalDateTime createdAt;
}
