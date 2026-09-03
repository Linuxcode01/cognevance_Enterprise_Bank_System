package com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
@Getter
@Setter
public class NotificationResponseDTO {
    private Long id;
    private String type;
    private String subject;
    private String message;
    private String status;
    private LocalDateTime sentAt;
}
