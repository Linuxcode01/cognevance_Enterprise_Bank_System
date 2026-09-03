package com.chandan.enterprise_banking_transaction_system.dto.ResponseDTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Getter
@Setter
public class ErrorResponseDTO {
    private String errorCode;
    private String message;
    private List<String> details;
    private String path;
    private LocalDateTime timestamp;
}
